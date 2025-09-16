package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class HistoryServiceClient {

    private final RestTemplate restTemplate;
    @Value("${app.stock.history.url}")
    String baseUrl;

    @Autowired
    public HistoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StockHistoryDTO> getHistoryBySku(String sku) {
        try {
            String url = baseUrl + sku;
            StockHistoryDTO[] histories = restTemplate.getForObject(url, StockHistoryDTO[].class);
            return histories != null ? Arrays.asList(histories) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico do SKU " + sku + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
