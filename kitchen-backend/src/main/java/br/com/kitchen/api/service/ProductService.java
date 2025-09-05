package br.com.kitchen.api.service;

import br.com.kitchen.api.model.*;
import br.com.kitchen.api.dto.request.ProductRequestDTO;
import br.com.kitchen.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService extends GenericService<Product, Long>{

    private final SellerService sellerService;
    private final CatalogService catalogService;
    private final CategoryService categoryService;
    private final SkuService skuService;
    private final OutboxService outboxService;
    private final ProductRepository productRepository;

    public ProductService(SellerService sellerService,
                          CatalogService catalogService,
                          CategoryService categoryService,
                          SkuService skuService,
                          OutboxService outboxService,
                          ProductRepository productRepository) {
        super(productRepository, Product.class);
        this.sellerService = sellerService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.skuService = skuService;
        this.outboxService = outboxService;
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(User user, ProductRequestDTO dto) {
        Seller seller = sellerService.getActiveSeller(user);
        Catalog catalog = catalogService.findOrCreate(dto.catalog(), seller);
        Category category = categoryService.findOrCreate(dto.category());

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.basePrice());
        product.setCatalog(catalog);
        product.setCategory(category);
        product.setSeller(seller);

        product = productRepository.save(product);

        List<ProductSku> skus = skuService.createOrUpdateSkus(product, seller, dto.skus());
        product.setSkus(skus);
        product = productRepository.save(product);

        outboxService.publishProductCreated(product);
        return product;
    }

    @Transactional
    public List<Product> createProducts(User user, List<ProductRequestDTO> dtos) {
        return dtos.stream()
                .map(dto -> createProduct(user, dto))
                .toList();
    }

    public List<Product> findProductsBySellerId(Long sellerId) {
        System.out.print("findProductsBySellerId::");
        return productRepository.findBySellerId(sellerId);
    }

    public Product findProductById(Long id) {
        System.out.print("findProductById::");
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
