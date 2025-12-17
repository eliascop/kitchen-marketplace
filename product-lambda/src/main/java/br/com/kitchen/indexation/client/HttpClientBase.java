package br.com.kitchen.indexation.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpClientBase {

    protected final String baseUrl;
    protected static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules();

    protected HttpClientBase(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else {
            this.baseUrl = baseUrl;
        }
    }

    protected <T> T get(String path, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            ensureSuccess(response);

            return OBJECT_MAPPER.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer GET para " + path, e);
        }
    }

    protected void put(String path, Object body) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            ensureSuccess(response);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer PUT para " + path, e);
        }
    }

    private void ensureSuccess(HttpResponse<?> response) {
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("Chamada HTTP falhou. Status: "
                    + status + " - Body: " + response.body());
        }
    }
}
