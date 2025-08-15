package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.CatalogResponseDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.response.ProductResponseDTO;
import br.com.kitchen.api.service.CatalogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/v1")
@SecurityRequirement(name = "bearer-key")
public class CatalogController {

    private final CatalogService service;

    @Autowired
    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CatalogResponseDTO>> showAll() {
        List<CatalogResponseDTO> response = service.findAllDistinctive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("{catalogSlug}/products")
    public ResponseEntity<List<ProductResponseDTO>> findProductsInCatalog(@PathVariable String catalogSlug) {
        List<ProductResponseDTO> response = service.findProductsByCatalogSlug(catalogSlug);
        return ResponseEntity.ok(response);
    }

}
