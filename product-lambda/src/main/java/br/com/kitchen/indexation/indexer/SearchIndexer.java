package br.com.kitchen.indexation.indexer;

import br.com.kitchen.indexation.document.ProductIndexDocument;

public interface SearchIndexer {
    void index(ProductIndexDocument document);
}
