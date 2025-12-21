package br.com.kitchen.indexation.client;

import br.com.kitchen.indexation.utils.JsonUtils;
import br.com.kitchen.indexation.dto.CategoryDTO;
import br.com.kitchen.indexation.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpProductClient {

    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    public HttpProductClient() {
        this.baseUrl = "http://kitchen-api:8080/products/v1";
    }

    public ProductDTO updateProduct(Long id, CategoryDTO category) {
        try {
            String internalToken = "1733861556000.X5sMjdTgP5m4hoSJ8iItnY244pb-00WaaRe9mFqzxVg";
            String body = JsonUtils.MAPPER.writeValueAsString(category);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + id + "/category" ))
                    .header("Authorization", internalToken)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException(
                        "An error has occurred on updating categoria: "+category.getName()+" with product id: "+id+". " + resp.statusCode()
                );
            }
            return JsonUtils.MAPPER.readValue(resp.body(), ProductDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("### An error has occurred on updating product id: " + e.getMessage());
        }
    }
}
