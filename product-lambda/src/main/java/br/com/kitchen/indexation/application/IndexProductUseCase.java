package br.com.kitchen.indexation.application;

import br.com.kitchen.indexation.classification.CategoryClassifier;
import br.com.kitchen.indexation.classification.CategoryNameGenerator;
import br.com.kitchen.indexation.client.HttpProductClient;
import br.com.kitchen.indexation.document.ProductIndexDocument;
import br.com.kitchen.indexation.dto.CategoryDTO;
import br.com.kitchen.indexation.dto.ProductDTO;
import br.com.kitchen.indexation.indexer.ElasticsearchIndexer;
import br.com.kitchen.indexation.indexer.SearchIndexer;

public class IndexProductUseCase {

    private final CategoryClassifier classifier;
    private final CategoryNameGenerator generator;
    private final SearchIndexer indexer;
    private final HttpProductClient productClient;

    public IndexProductUseCase() {
        this.classifier = new CategoryClassifier();
        this.generator  = new CategoryNameGenerator();
        this.indexer    = new ElasticsearchIndexer();
        this.productClient = new HttpProductClient();
    }

    public void execute(ProductDTO product) {

        CategoryDTO category = resolveCategory(product);

        ProductDTO productUpdated = productClient.updateProduct(product.getId(), category);

        indexer.index(ProductIndexDocument.from(productUpdated, category));
    }

    private CategoryDTO resolveCategory(ProductDTO product) {
        return classifier
                .classify(product.getName(), product.getDescription())
                .map(match -> CategoryDTO.builder()
                        .name(match.name())
                        .build()
                )
                .orElseGet(() -> CategoryDTO.builder()
                        .name(generator.generateCategoryName(
                                product.getName(),
                                product.getDescription()
                        ))
                        .build());
    }
}