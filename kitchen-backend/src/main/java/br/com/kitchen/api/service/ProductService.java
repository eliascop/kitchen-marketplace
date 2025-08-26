package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.record.ProductAttributeDTO;
import br.com.kitchen.api.record.ProductRequestDTO;
import br.com.kitchen.api.repository.*;
import br.com.kitchen.api.util.JsonUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService extends GenericService<Product, Long> {

    private final ProductRepository productRepository;
    private final ProductSkuRepository productSkuRepository;
    private final CatalogRepository catalogRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final OutboxRepository outboxRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductSkuRepository productSkuRepository,
            SellerRepository sellerRepository,
            CatalogRepository catalogRepository,
            CategoryRepository categoryRepository,
            OutboxRepository outboxRepository) {
        super(productRepository, Product.class);
        this.outboxRepository = outboxRepository;
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
        this.sellerRepository = sellerRepository;
        this.catalogRepository = catalogRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product createProduct(User user, ProductRequestDTO dto) {
        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller not found."));
        if(seller.isBlocked()){
            throw new RuntimeException("Seller is not active");
        }

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
        product.setSeller(seller);

        Product productSaved = productRepository.save(product);

        List<ProductSku> skuList = dto.skus().stream().map(skuDTO -> {
            String generatedSku = generateSku(productSaved.getId(), skuDTO.attributes());
            ProductSku sku = productSkuRepository.findBySkuAndProductId(generatedSku, productSaved.getId())
                    .orElse(new ProductSku());

            sku.setSku(generatedSku);
            sku.setPrice(skuDTO.price());
            sku.setProduct(productSaved);

            List<ProductAttribute> attributes = skuDTO.attributes().stream().map(attrDTO -> {
                ProductAttribute attr = new ProductAttribute();
                attr.setName(attrDTO.name());
                attr.setAttributeValue(attrDTO.value());
                attr.setSku(sku);
                return attr;
            }).toList();
            sku.setAttributes(attributes);

            Stock stock = sku.getStock();
            if (stock == null) {
                stock = new Stock();
                stock.setSku(sku);
                stock.setSeller(seller);
                stock.setReservedQuantity(0);
                stock.setSoldQuantity(0);
            }
            stock.setTotalQuantity(skuDTO.stock());
            sku.setStock(stock);

            return sku;
        }).toList();

        product.setSkus(skuList);
        product = productRepository.save(product);

        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("PRODUCT")
                .aggregateId(product.getId())
                .eventType("PRODUCT_CREATED")
                .payload(JsonUtils.toJson(
                        ProductDTO.builder().id(product.getId()).build()
                )).build();
        outboxRepository.save(event);

        return product;
    }

    @Transactional
    public List<Product> createProducts(User user, List<ProductRequestDTO> dtos) throws Exception {
        return dtos.stream()
                .map(dto -> createProduct(user, dto))
                .toList();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
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

    public List<Product> findProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found"));
    }

    public Product findProductBySkuAndSeller(String sku, Long sellerId) {
        return productRepository.findBySkuAndSellerId(sku, sellerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product [" + sku + "] not found for this seller"));
    }
}
