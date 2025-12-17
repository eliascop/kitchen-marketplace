package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CatalogDTO;
import br.com.kitchen.api.dto.CategoryDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.StockHistoryDTO;
import br.com.kitchen.api.dto.request.ProductRequestDTO;
import br.com.kitchen.api.dto.request.ProductSkuRequestDTO;
import br.com.kitchen.api.dto.response.PaginatedResponse;
import br.com.kitchen.api.enumerations.EventType;
import br.com.kitchen.api.enumerations.ProductStatus;
import br.com.kitchen.api.mapper.PaginateMapper;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.*;
import br.com.kitchen.api.repository.jpa.ProductRepository;
import br.com.kitchen.api.repository.search.ProductSearchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService extends GenericService<Product, Long>{

    private final CatalogService catalogService;
    private final CategoryService categoryService;
    private final SkuService skuService;
    private final OutboxService outboxService;
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final HistoryServiceClient historyClient;

    public ProductService(CatalogService catalogService,
                          CategoryService categoryService,
                          SkuService skuService,
                          OutboxService outboxService,
                          HistoryServiceClient historyClient,
                          ProductRepository productRepository,
                          ProductSearchRepository productSearchRepository) {
        super(productRepository, Product.class);
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.skuService = skuService;
        this.outboxService = outboxService;
        this.productRepository = productRepository;
        this.historyClient = historyClient;
        this.productSearchRepository = productSearchRepository;
    }

    @Transactional
    public Product createProduct(Seller seller, ProductRequestDTO dto) {
        if(seller.isBlocked()) throw new RuntimeException("Seller is blocked");
        Catalog catalog = catalogService.findOrCreate(dto.catalogName(), seller);

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCatalog(catalog);
        product.setSeller(seller);
        product.setImageUrl(dto.imageUrl());

        product = productRepository.save(product);

        if(dto.skus() == null || dto.skus().isEmpty()){
            return product;
        }

        List<ProductSku> skus = new ArrayList<>(skuService.createOrUpdateSkus(product, seller, dto.skus()));
        product.setSkus(skus);
        product = productRepository.save(product);

        outboxService.publishProductEvent(product, EventType.Created);
        return product;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public List<Product> createProducts(Seller seller, List<ProductRequestDTO> dtos) {
        log.info("createProducts::{}", dtos.toString());
        return dtos.stream()
                .map(dto -> createProduct(seller, dto))
                .toList();
    }

    public PaginatedResponse<ProductDTO> findProductsBySellerId(Seller seller, Pageable pageable) {
        log.info("findProductsBySellerId::{}",seller.getId());

        List<StockHistoryDTO> allHistories = historyClient.getStockHistoriesBySellerId(seller.getId());

        Map<String, List<StockHistoryDTO>> historiesMap = allHistories.stream()
                .collect(Collectors.groupingBy(StockHistoryDTO::getSku));

        Page<Product> paginatedProducts = productRepository.findBySellerId(seller.getId(), pageable);

        paginatedProducts.getContent().stream()
                .flatMap(product -> product.getSkus().stream())
                .forEach(sku -> {
                    List<StockHistoryDTO> history =
                            historiesMap.getOrDefault(sku.getSku(), Collections.emptyList());
                    sku.setStockHistory(history);
                });

        Page<ProductDTO> mapped = paginatedProducts.map(ProductMapper::toProductResponseDTO);

        return PaginateMapper.toDTO(mapped);
    }

    @Cacheable(value = "products", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<Product> findActiveProducts(Pageable pageable) {
        return productRepository.findAllActiveProducts(pageable);
    }

    public Product findProductById(Long id) {
        log.info("findProductById::{}",id);
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public Product findProductByIdAndSeller(Long id, Seller seller) {
        log.info("findProductByIdAndSeller::{}-{}",id,seller.getId());
        return productRepository.findByIdAndSellerId(id, seller.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found for this seller"));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Page<ProductDTO> searchProducts(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            System.out.println("🟢 MySQL query (fallback)");
            return productRepository.findAllActiveProducts(pageable)
                    .map(ProductMapper::toProductResponseDTO);
        }

        System.out.println("🔍 Elasticsearch query");
        return productSearchRepository.search(query, pageable)
                .map(ProductMapper::fromSearchDocument);
    }

    @Transactional
    public Product updateProduct(Seller seller, ProductRequestDTO dto) {
        if(seller.isBlocked()) throw new RuntimeException("Seller is blocked");

        Product product = findProductByIdAndSeller(dto.id(), seller);
        Catalog catalog = catalogService.findOrCreate(dto.catalogName(), seller);
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCatalog(catalog);
        product.setImageUrl(dto.imageUrl());
        product.setProductStatus(ProductStatus.PENDING_INDEXING);

        product = productRepository.save(product);

        outboxService.publishProductEvent(product, EventType.Updated);
        return product;
    }

    public void createOrUpdateSkus(Long productId,
                                   Seller seller,
                                   List<ProductSkuRequestDTO> skuDTOs) {

        Product product = findProductByIdAndSeller(productId, seller);
        skuService.createOrUpdateSkus(product, seller, skuDTOs);
        outboxService.publishProductEvent(product, EventType.Updated);
    }

    @Transactional
    public void updateProductCategory(Long id, CategoryDTO categoryDTO) {
        if(categoryDTO.getId() != null && categoryDTO.getId() > 0) {
            productRepository.updateCategoryAndStatus(id, categoryDTO.getId());
        }else{
            Category newCategory = categoryService.findOrCreate(categoryDTO.getName());
            Product product = findProductById(id);
            product.setCategory(newCategory);
            product.setProductStatus(ProductStatus.ACTIVE);
            product.setActivatedAt(LocalDateTime.now());
        }
    }
}
