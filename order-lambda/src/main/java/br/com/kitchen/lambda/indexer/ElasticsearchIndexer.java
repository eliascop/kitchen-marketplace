package br.com.kitchen.lambda.indexer;

import br.com.kitchen.lambda.document.OrderIndexDocument;
import br.com.kitchen.lambda.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class ElasticsearchIndexer implements SearchIndexer {

    private final String baseUrl;
    private final String indexName;
    private final HttpClient httpClient;

    public ElasticsearchIndexer() {
        this.baseUrl = "http://elasticsearch:9200";
        this.indexName = "kitchen-orders";
        this.httpClient = HttpClient.newBuilder().build();
    }

    @Override
    public void index(OrderIndexDocument document) {
        try {
            log.info("Entering in the elasticsearch indexer");
            String json = JsonUtils.MAPPER.writeValueAsString(document);

            String path = String.format("%s/%s/_doc/%d", baseUrl, indexName, document.id());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException(
                        "Erro ao indexar pedido no Elasticsearch. Status: " + status +
                                " Body: " + response.body()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao indexar documento no Elasticsearch", e.getCause());
        }
    }
}