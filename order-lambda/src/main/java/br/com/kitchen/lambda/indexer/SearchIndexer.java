package br.com.kitchen.lambda.indexer;

import br.com.kitchen.lambda.document.OrderIndexDocument;

public interface SearchIndexer {
    void index(OrderIndexDocument document);
}
