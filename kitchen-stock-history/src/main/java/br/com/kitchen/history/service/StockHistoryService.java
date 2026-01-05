package br.com.kitchen.history.service;

import br.com.kitchen.history.dto.StockHistoryDTO;
import br.com.kitchen.history.mapper.StockHistoryMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class StockHistoryService {

    private final DynamoDbClient dynamoDbClient;

    public StockHistoryService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public List<StockHistoryDTO> getBySellerId(Long sellerId) {
        return queryStockHistory("sellerId = :sellerId", Map.of(
                ":sellerId", AttributeValue.builder().s(sellerId.toString()).build()
        ));
    }

    public List<StockHistoryDTO> getBySku(String sku) {
        return queryStockHistory("sku = :sku", Map.of(
                ":sku", AttributeValue.builder().s(sku).build()
        ));
    }

    /**
     * Método genérico para buscar no DynamoDB usando filtro e valores dinâmicos
     */
    private List<StockHistoryDTO> queryStockHistory(String filterExpression,
                                                    Map<String, AttributeValue> expressionValues) {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName("StockHistory")
                    .filterExpression(filterExpression)
                    .expressionAttributeValues(expressionValues)
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);
            return StockHistoryMapper.toDTOList(response.items());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar StockHistory: " + e.getMessage(), e);
        }
    }
}
