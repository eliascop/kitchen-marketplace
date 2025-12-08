package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.request.ProductRequestDTO;
import br.com.kitchen.api.dto.request.ProductSkuRequestDTO;
import br.com.kitchen.api.dto.response.PaginatedResponse;
import br.com.kitchen.api.mapper.PaginateMapper;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.CatalogService;
import br.com.kitchen.api.service.ProductService;
import br.com.kitchen.api.service.SkuService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products/v1")
@SecurityRequirement(name = "bearer-key")
public class ProductController {

    private final ProductService productService;

    private final CatalogService catalogService;

    public ProductController(ProductService productService,
                             CatalogService catalogService) {
        this.productService = productService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<?> listAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String catalog
    ) {
        try {
            Page<Product> products;
            if (catalog != null && !catalog.isEmpty()) {
                products = catalogService.findProductsByCatalogSlug(catalog, PageRequest.of(page, size));
            } else {
                products = productService.findActiveProducts(PageRequest.of(page, size));
            }

            Page<ProductDTO> mapped = products.map(ProductMapper::toProductResponseDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(PaginateMapper.toDTO(mapped));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when list all products",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query
    ) {
        try {
            Page<ProductDTO> products = productService.searchProducts(query, PageRequest.of(page, size));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(PaginateMapper.toDTO(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when searching for products",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> listMyProducts(@AuthenticationPrincipal UserPrincipal principal,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PaginatedResponse<ProductDTO> response = productService.findProductsBySellerId(principal.getSeller().get(),PageRequest.of(page, size));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when list seller products",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(ProductMapper.toProductResponseDTO(product));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProduct(@AuthenticationPrincipal UserPrincipal principal,
                                           @RequestBody ProductRequestDTO dto) {
        try {
            Product created = productService.createProduct(principal.getSeller().get(), dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ProductMapper.toProductResponseDTO(created));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when creating the product",
                            "details", e.getMessage()
                    ));
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProductBatch(@AuthenticationPrincipal UserPrincipal principal,
                                                @RequestBody List<ProductRequestDTO> dtos) {
        try {
            List<ProductDTO> response = productService.createProducts(principal.getSeller().get(), dtos)
                    .stream()
                    .map(ProductMapper::toProductResponseDTO)
                    .toList();
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when creating the product",
                            "details", e.getMessage()
                    ));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateProduct(@AuthenticationPrincipal UserPrincipal principal,
                                           @RequestBody ProductRequestDTO dto) {
        try {
            Product updated = productService.updateProduct(principal.getSeller().get(), dto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ProductMapper.toProductResponseDTO(updated));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when updating the product",
                            "details", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                Map.of("message", "Product deleted successfully",
                        "code", HttpStatus.OK.value())
        );
    }

    @PutMapping("/{id}/skus")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateProductSku(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable Long id,
                                              @RequestBody List<ProductSkuRequestDTO> dto) {
        try {
            productService.createOrUpdateSkus(id,principal.getSeller().get(), dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Product SKUs updated successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "An error has occurred when updating the product",
                            "details", e.getMessage()
                    ));
        }
    }
}
