package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.producer.KafkaProducer;
import br.com.kitchen.api.record.ProductAttributeDTO;
import br.com.kitchen.api.record.ProductRequestDTO;
import br.com.kitchen.api.record.ProductSkuDTO;
import br.com.kitchen.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService extends GenericService<Product, Long> {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final CatalogRepository catalogRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    private final KafkaProducer<ProductDTO> productProducer;

    @Autowired
    public ProductService(@Qualifier("productKafkaProducer") KafkaProducer<ProductDTO> productProducer,
                          ProductRepository productRepository,
                          ProductSkuRepository productSkuRepository,
                          SellerRepository sellerRepository,
                          CatalogRepository catalogRepository,
                          CategoryRepository categoryRepository) {
        super(productRepository, Product.class);
        this.sellerRepository = sellerRepository;
        this.productProducer = productProducer;
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
        this.catalogRepository = catalogRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product createProduct(User user, ProductRequestDTO dto) {

        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller not found."));

        Catalog catalog = catalogRepository.findByNameAndSellerId(dto.catalog(), seller.getId())
                .orElseGet(() -> catalogRepository.save(new Catalog(seller, dto.catalog())));

        Category category = categoryRepository.findByName(dto.category())
                .orElseGet(() -> categoryRepository.save(new Category(dto.category())));

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.basePrice());
        product.setCatalog(catalog);
        product.setCategory(category);
        product = productRepository.save(product);

        List<ProductSku> skuList = new ArrayList<>();

        for (ProductSkuDTO skuDTO : dto.skus()) {

            String generatedSku = generateSku(product.getId(), skuDTO.attributes());
            ProductSku sku = productSkuRepository.findBySku(generatedSku).orElseGet(ProductSku::new);
            sku.setSku(generatedSku);
            sku.setPrice(skuDTO.price());
            sku.setProduct(product);

            List<ProductAttribute> attributes = skuDTO.attributes().stream().map(attrDTO -> {
                ProductAttribute attr = new ProductAttribute();
                attr.setName(attrDTO.name());
                attr.setAttributeValue(attrDTO.value());
                attr.setSku(sku);
                return attr;
            }).toList();
            sku.setAttributes(attributes);

            Stock stock = sku.getStock();
            if (stock != null) {
                stock.setTotalQuantity(stock.getTotalQuantity() + skuDTO.stock());
            } else {
                stock = new Stock();
                stock.setSku(sku);
                stock.setSeller(seller);
                stock.setTotalQuantity(skuDTO.stock());
                stock.setReservedQuantity(0);
                stock.setSoldQuantity(0);
            }
            sku.setStock(stock);

            skuList.add(sku);
        }

        product.setSkus(skuList);
        product = productRepository.save(product);

        productProducer.sendNotification(new ProductDTO(product.getId()));

        return product;
    }

    @Transactional
    public List<Product> createProducts(User user, List<ProductRequestDTO> dtos) {
        List<Product> savedProducts = new ArrayList<>();
        for (ProductRequestDTO dto : dtos) {
            Product product = createProduct(user, dto);
            savedProducts.add(product);
        }
        return savedProducts;
    }

    public void deleteProduct(Long id){
        this.productRepository.deleteById(id);
    }

    private String generateSku(Long productId, List<ProductAttributeDTO> attributesDTO) {
        return "PROD-" + productId + attributesDTO.stream()
                .sorted(Comparator.comparing(ProductAttributeDTO::name))
                .map(attr -> "-" + normalize(attr.value()))
                .collect(Collectors.joining());
    }

    private String normalize(String input) {
        String normalized = input == null ? "" : input
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");

        return normalized.substring(0, Math.min(5, normalized.length()));
    }

    public List<Product> findByUserId(Long sellerId){
        return productRepository.findByCatalog_Seller_UserId(sellerId);
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }
}
