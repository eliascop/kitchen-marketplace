package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockHistoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HistoryServiceClient {

    private final RestTemplate restTemplate;
    @Value("${app.stock.history.url}")
    String baseUrl;

    @Autowired
    public HistoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StockHistoryDTO> getStockHistoriesBySellerId(Long sellerId) {
        try {
            String url = baseUrl + sellerId;
            StockHistoryDTO[] histories = restTemplate.getForObject(url, StockHistoryDTO[].class);
            return histories != null ? Arrays.asList(histories) : Collections.emptyList();
        } catch (Exception e) {
            log.error("An error occurred on get history by sellerId {}: Error: {}", sellerId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
