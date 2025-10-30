package br.com.kitchen.api.repository.search;

import br.com.kitchen.api.dto.search.ProductSearchDocumentDTO;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepositoryCustom {

    private final ElasticsearchClient client;

    @Override
    public Page<ProductSearchDocumentDTO> search(String query, Pageable pageable) {
        try {

            SearchResponse<ProductSearchDocumentDTO> response = client.search(s -> s
                            .index("kitchen-products")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(query)
                                            .fields("name^2", "description", "category")
                                            .fuzziness("AUTO")
                                    )
                            )
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    ProductSearchDocumentDTO.class
            );

            List<ProductSearchDocumentDTO> products = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            long total = response.hits().total() != null
                    ? response.hits().total().value()
                    : products.size();

            return new PageImpl<>(products, pageable, total);

        } catch (IOException e) {
            throw new RuntimeException("An error has occurred when search products on Elasticsearch", e);
        }
    }
}
