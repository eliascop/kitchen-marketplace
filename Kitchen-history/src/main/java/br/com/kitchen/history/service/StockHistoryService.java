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

    public List<StockHistoryDTO> getStockHistories(Long sellerId) {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName("StockHistory")
                    .filterExpression("sellerId = :sellerId")
                    .expressionAttributeValues(Map.of(
                            ":sellerId", AttributeValue.builder().s(sellerId.toString()).build()
                    ))
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);

            return StockHistoryMapper.toDTOList(response.items());
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<StockHistoryDTO> getStockHistoryBySku(String sku) {
        System.out.println("getStockHistoryBySku:: "+sku);
        try{
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName("StockHistory")
                    .filterExpression("sku = :sku")
                    .expressionAttributeValues(Map.of(
                            ":sku", AttributeValue.builder().s(sku).build()
                    ))
                    .build();

            ScanResponse response = dynamoDbClient.scan(scanRequest);
            return StockHistoryMapper.toDTOList(response.items());
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
