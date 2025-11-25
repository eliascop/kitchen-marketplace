package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.CatalogDTO;
import br.com.kitchen.api.service.CatalogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<CatalogDTO>> showAll() {
        List<CatalogDTO> response = service.findAllDistinctive();
        return ResponseEntity.ok(response);
    }

}
