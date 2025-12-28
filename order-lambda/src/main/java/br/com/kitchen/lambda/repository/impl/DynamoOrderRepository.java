package br.com.kitchen.lambda.repository.impl;

import br.com.kitchen.lambda.dto.OrderDTO;
import br.com.kitchen.lambda.repository.OrderRepository;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamoOrderRepository implements OrderRepository {

    private final DynamoDbClient dynamoDbClient;

    public DynamoOrderRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void save(OrderDTO order) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().n(order.getId().toString()).build());
        item.put("status", AttributeValue.builder().s(order.getStatus()).build());
        item.put("customerId", AttributeValue.builder().n(order.getCustomerId().toString()).build());

        dynamoDbClient.putItem(builder ->
                builder.tableName("Order").item(item)
        );

        log.info("Order saved on DynamoDB: {}", order.getId());
    }
}
