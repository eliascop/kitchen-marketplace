package br.com.kitchen.indexation.indexer;

import br.com.kitchen.indexation.document.ProductIndexDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public ElasticsearchIndexer() {
//        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.baseUrl = "http://elasticsearch:9200";
        this.indexName = "kitchen-products";
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void index(ProductIndexDocument document) {
        try {
            log.info("Enter in the elasticsearch classification");
            String json = objectMapper.writeValueAsString(document);

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
                        "Erro ao indexar produto no Elasticsearch. Status: " + status +
                                " Body: " + response.body()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao indexar documento no Elasticsearch", e.getCause());
        }
    }
}