package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockHistoryDTO;
import br.com.kitchen.api.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

            if (histories == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(histories)
                    .map(StockMapper::convertCreatedAt)
                    .toList();

        } catch (Exception e) {
            log.error("An error occurred on get history by sellerId {}: Error: {}", sellerId, e.getMessage());
            return Collections.emptyList();
        }
    }

}
