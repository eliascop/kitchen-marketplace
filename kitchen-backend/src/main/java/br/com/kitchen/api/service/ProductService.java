package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockHistoryDTO;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.dto.request.ProductRequestDTO;
import br.com.kitchen.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService extends GenericService<Product, Long>{

    private final SellerService sellerService;
    private final CatalogService catalogService;
    private final CategoryService categoryService;
    private final SkuService skuService;
    private final OutboxService outboxService;
    private final ProductRepository productRepository;
    private final HistoryServiceClient historyClient;

    public ProductService(SellerService sellerService,
                          CatalogService catalogService,
                          CategoryService categoryService,
                          SkuService skuService,
                          OutboxService outboxService,
                          HistoryServiceClient historyClient,
                          ProductRepository productRepository) {
        super(productRepository, Product.class);
        this.sellerService = sellerService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.skuService = skuService;
        this.outboxService = outboxService;
        this.productRepository = productRepository;
        this.historyClient = historyClient;
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
        log.info("createProducts::{}", dtos.toString());
        return dtos.stream()
                .map(dto -> createProduct(user, dto))
                .toList();
    }

    public List<Product> findProductsBySellerId(User user) {
        Seller seller = sellerService.getActiveSeller(user);

        log.info("findProductsBySellerId::{}",seller.getId());

        List<Product> products = productRepository.findBySellerId(seller.getId());

        List<StockHistoryDTO> allHistories = historyClient.getStockHistoriesBySellerId(seller.getId());

        Map<String, List<StockHistoryDTO>> historiesMap = allHistories.stream()
                .collect(Collectors.groupingBy(StockHistoryDTO::getSku));

        for (Product product: products) {
            for (ProductSku sku: product.getSkus()) {
                List<StockHistoryDTO> h = historiesMap.getOrDefault(sku.getSku(), Collections.emptyList());
                sku.setStockHistory(h);
            }
        }

        return products;
    }

    public Product findProductBySku(String sku){
        log.info("findProductBySku::sku{}",sku);
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public Product findProductById(Long id) {
        log.info("findProductById::{}",id);
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
