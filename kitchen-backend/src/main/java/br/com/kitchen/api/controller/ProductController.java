package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.response.ProductResponseDTO;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.record.ProductRequestDTO;
import br.com.kitchen.api.security.UserPrincipal;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductResponseDTO>> showAllProducts() {
        List<ProductResponseDTO> response = service.findAll()
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponseDTO>> showMyProducts(@AuthenticationPrincipal UserPrincipal userDetails) {
        List<ProductResponseDTO> response = service.findProductsBySellerId(userDetails.user().getId())
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product p = service.findProductById(id);
        return ResponseEntity.ok().body(ProductMapper.toResponseDTO(p));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponseDTO>> createProductBatch(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @RequestBody List<ProductRequestDTO> dtos) {

        List<ProductResponseDTO> response = service.createProducts(userDetails.user(), dtos)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @RequestBody ProductRequestDTO dto) {

        Product productSaved = service.createProduct(userDetails.user(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toResponseDTO(productSaved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.ok(Map.of(
                "message", "Product deleted successfully",
                "code", HttpStatus.OK.value()
        ));
    }
}
