package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.search.*;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.repository.jpa.ProductRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private final ElasticsearchClient client;
    private final ProductRepository productRepository;

    @PostConstruct
    public void createProductIndex() throws IOException {
        boolean exists = client.indices().exists(e -> e.index("kitchen-products")).value();

        if (!exists) {
            client.indices().create(c -> c
                    .index("kitchen-products")
                    .settings(s -> s
                            .numberOfShards("1")
                            .numberOfReplicas("0")
                    )
                    .mappings(m -> m
                            .properties("id", p -> p.long_(l -> l))
                            .properties("name", p -> p.text(t -> t.analyzer("standard")))
                            .properties("description", p -> p.text(t -> t.analyzer("standard")))
                            .properties("basePrice", p -> p.double_(d -> d))
                            .properties("imageUrl", p -> p.keyword(k -> k))
                            .properties("sellerId", p -> p.long_(l -> l))
                            .properties("sellerName", p -> p.text(t -> t.analyzer("standard")))
                            .properties("catalogId", p -> p.long_(l -> l))
                            .properties("catalogName", p -> p.keyword(k -> k))
                            .properties("categoryId", p -> p.long_(l -> l))
                            .properties("categoryName", p -> p.keyword(k -> k))
                            .properties("createdAt", p -> p.date(d -> d))
                            .properties("productStatus", p -> p.text(t -> t.analyzer("standard")))

                            // ✅ SKUs nested
                            .properties("skus", p -> p.nested(n -> n
                                    .properties("id", pp -> pp.long_(l -> l))
                                    .properties("sku", pp -> pp.keyword(k -> k))
                                    .properties("price", pp -> pp.double_(d -> d))
                                    .properties("stockQuantity", pp -> pp.integer(i -> i))

                                    // ✅ Attributes nested
                                    .properties("attributes", pp -> pp.nested(n2 -> n2
                                            .properties("name", ppp -> ppp.keyword(k -> k))
                                            .properties("value", ppp -> ppp.keyword(k -> k))
                                    ))
                            ))
                    )
            );

            System.out.println("✅ Índice 'kitchen-products' criado com sucesso!");
        }
    }


    @PostConstruct
    public void syncExistingProducts() throws IOException {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {

            ProductSearchDocumentDTO doc = ProductSearchDocumentDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .basePrice(product.getBasePrice())
                    .imageUrl(product.getImageUrl())
                    .sellerId(product.getSeller().getId())
                    .sellerName(product.getSeller().getStoreName())
                    .catalogId(product.getCatalog().getId())
                    .catalogName(product.getCatalog().getName())
                    .createdAt(product.getCreatedAt()
                            .atOffset(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.MILLIS))
                    .activatedAt(
                            product.getActivatedAt() != null
                                    ? product.getActivatedAt()
                                    .atOffset(ZoneOffset.UTC)
                                    .truncatedTo(ChronoUnit.MILLIS)
                                    : null
                    )
                    .productStatus(product.getProductStatus().toString())
                    .skus(
                            product.getSkus().stream().map(sku ->
                                    ProductSkuSearchDocumentDTO.builder()
                                            .id(sku.getId())
                                            .sku(sku.getSku())
                                            .price(sku.getPrice())
                                            .attributes(
                                                    sku.getAttributes().stream().map(attr ->
                                                            SkuAttributeSearchDocumentDTO.builder()
                                                                    .name(attr.getAttributeName())
                                                                    .value(attr.getAttributeValue())
                                                                    .build()
                                                    ).toList()
                                            )
                                            .build()
                            ).toList()
                    )
                    .build();

            client.index(i -> i
                    .index("kitchen-products")
                    .id(doc.getId().toString())
                    .document(doc)
            );
        }

        System.out.println("✅ " + products.size() + " synchronized in the Elasticsearch!");
    }


}
