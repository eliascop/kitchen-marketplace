package br.com.kitchen.indexation.client;

import br.com.kitchen.indexation.dto.CategoryDTO;
import br.com.kitchen.indexation.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpProductClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    public HttpProductClient() {
        this.baseUrl = "http://kitchen-api:8080/products/v1";
    }

    public ProductDTO getById(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new ObjectMapper().readValue(response.body(), ProductDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProduct(Long id, CategoryDTO category) {
        try {
            String internalToken = "1733861556000.X5sMjdTgP5m4hoSJ8iItnY244pb-00WaaRe9mFqzxVg";
            String body = MAPPER.writeValueAsString(category);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + id + "/category" ))
                    .header("Authorization", internalToken)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() >= 400) {
                log.error("Erro ao atualizar categoria do produto {}. Resposta: {}", id, resp.body());
            }

        } catch (Exception e) {
            log.error("### erro ao atualizar produto:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
