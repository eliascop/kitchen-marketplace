package br.com.kitchen.lambda.application;

import br.com.kitchen.lambda.document.OrderIndexDocument;
import br.com.kitchen.lambda.dto.OrderDTO;
import br.com.kitchen.lambda.indexer.ElasticsearchIndexer;
import br.com.kitchen.lambda.indexer.SearchIndexer;

public class IndexOrderUseCase {

    private final SearchIndexer indexer;

    public IndexOrderUseCase() {
        this.indexer = new ElasticsearchIndexer();
    }

    public void execute(OrderDTO order) {
        indexer.index(OrderIndexDocument.from(order));
    }

}