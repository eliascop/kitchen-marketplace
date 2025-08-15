package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CatalogResponseDTO;
import br.com.kitchen.api.dto.response.ProductResponseDTO;
import br.com.kitchen.api.mapper.CatalogMapper;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Catalog;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.repository.CatalogRepository;
import br.com.kitchen.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService extends GenericService<Catalog, Long>{

    private final CatalogRepository repository;
    private final ProductRepository productRepository;

    @Autowired
    public CatalogService(CatalogRepository repository,
                          ProductRepository productRepository) {
        super(repository, Catalog.class);
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public List<CatalogResponseDTO> findAllDistinctive(){
        List<Catalog> catalog = repository.findAllDistinctBySlug();
        return CatalogMapper.toDTOList(catalog);
    }

    public List<ProductResponseDTO> findProductsByCatalogSlug(String catalogSlug) {
        List<Long> catalogIds = repository.findBySlug(catalogSlug)
                .stream()
                .map(Catalog::getId)
                .toList();

        List<Product> productsList = productRepository.findByCatalogIdIn(catalogIds);
        return ProductMapper.toDTOList(productsList);
    }
}
