package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.response.ProductResponseDTO;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.record.CustomUserDetails;
import br.com.kitchen.api.record.ProductRequestDTO;
import br.com.kitchen.api.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products/v1")
@SecurityRequirement(name = "bearer-key")
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> showAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Product> products = service.findByUserId(userDetails.user().getId());

        List<ProductResponseDTO> response = products.stream()
                .map(ProductMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> productOpt = service.findProductById(id);

            if (productOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "errorCode", 404,
                                "message", "Product not found"
                        ));
            }

            return ResponseEntity
                    .ok(ProductMapper.toResponseDTO(productOpt.get()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "Erro ao buscar produto",
                            "details", e.getMessage()
                    ));
        }
    }


    @GetMapping("/search")
    public List<Product> findByType(@RequestParam String type) {
        return service.findByField("type", type);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProductBatch(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody List<ProductRequestDTO> dtos) {
        try {
            List<Product> productsSaved = service.createProducts(userDetails.user(), dtos);
            List<ProductResponseDTO> response = productsSaved.stream()
                    .map(ProductMapper::toResponseDTO)
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Erro ao salvar produtos",
                            "details", e.getMessage()
                    ));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody ProductRequestDTO dto) {
        try {
            Product productSaved = service.createProduct(userDetails.user(), dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ProductMapper.toResponseDTO(productSaved));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "An error occurred when saving product",
                            "details", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try{
            Optional<Product> product = service.findById(id);
            product.ifPresent(p ->{
                service.deleteProduct(id);
            });
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Product deleted successfully",
                            "code", HttpStatus.OK
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "An error occurred when delete product",
                            "details", e.getMessage()
                    ));
        }

    }

}