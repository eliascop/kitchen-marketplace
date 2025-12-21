package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CatalogDTO;
import br.com.kitchen.api.dto.request.CatalogRequestDTO;
import br.com.kitchen.api.mapper.CatalogMapper;
import br.com.kitchen.api.model.Catalog;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.repository.jpa.CatalogRepository;
import br.com.kitchen.api.repository.jpa.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<CatalogDTO> findAllDistinctive(){
        List<Catalog> catalog = repository.findAllDistinctBySlug();
        return CatalogMapper.toDTOList(catalog);
    }

    public Catalog findOrCreate(CatalogRequestDTO catalog, Seller seller) {
        if (catalog.id() != null) {
            return repository.findById(catalog.id())
                    .orElseThrow(() -> new RuntimeException("Catalog not found with id " + catalog.id()));
        }

        return repository.findByNameIgnoreCaseAndSellerId(catalog.name(), seller.getId())
                .orElseGet(() -> repository.save(new Catalog(seller, catalog.name())));
    }

    public Page<Product> findProductsByCatalogSlug(String catalogSlug, PageRequest pageRequest) {
        List<Long> catalogIds = repository.findBySlug(catalogSlug)
                .stream()
                .map(Catalog::getId)
                .toList();

        return productRepository.findActiveProductsByCatalogIdIn(catalogIds, pageRequest);
    }
}
