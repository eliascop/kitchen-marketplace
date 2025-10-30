package br.com.kitchen.api.repository.search;

import br.com.kitchen.api.model.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository
        extends ElasticsearchRepository<Product, String>, ProductSearchRepositoryCustom {
}