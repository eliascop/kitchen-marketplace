package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.request.ProductRequestDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.response.PaginatedResponse;
import br.com.kitchen.api.mapper.PaginateMapper;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.CatalogService;
import br.com.kitchen.api.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public ProductController(ProductService productService, CatalogService catalogService) {
        this.productService = productService;
        this.catalogService = catalogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<ProductDTO>> listAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String catalog
    ) {
        Page<Product> products;
        if (catalog != null && !catalog.isEmpty()) {
            products = catalogService.findProductsByCatalogSlug(catalog, PageRequest.of(page, size));
        } else {
            products = productService.findAllproducts(PageRequest.of(page, size));
        }

        Page<ProductDTO> mapped = products.map(ProductMapper::toProductResponseDTO);
        return ResponseEntity.ok(PaginateMapper.toDTO(mapped));
    }


    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductDTO>> listMyProducts(@AuthenticationPrincipal UserPrincipal principal) {
        List<ProductDTO> response = productService.findProductsBySellerId(principal.user())
                .stream()
                .map(ProductMapper::toProductResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
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
            Product created = productService.createProduct(principal.user(), dto);
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
            List<ProductDTO> response = productService.createProducts(principal.user(), dtos)
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                Map.of("message", "Product deleted successfully",
                        "code", HttpStatus.OK.value())
        );
    }
}
