package br.com.kitchen.lambda;

import br.com.kitchen.dto.SnsNotificationDTO;
import br.com.kitchen.dto.StockDTO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StockLambdaHandler implements RequestHandler<SQSEvent, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DynamoDbClient dynamoDbClient;

    public StockLambdaHandler() {

        String lsHost = System.getenv().getOrDefault("LOCALSTACK_HOSTNAME", "localstack");
        URI endpoint = URI.create("http://" + lsHost + ":4566");

        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(endpoint)
                .region(software.amazon.awssdk.regions.Region.SA_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();
    }

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        log.info("################## Stocklambda initialized ################## ");
        try {
            for (SQSEvent.SQSMessage msg: event.getRecords()) {

                SnsNotificationDTO notification = mapper.readValue(msg.getBody(), SnsNotificationDTO.class);
                StockDTO stockDTO = mapper.readValue(notification.getMessage(), StockDTO.class);

                if (stockDTO.getId() == null) {
                    log.error("Message was ignored: Stock id é null");
                    continue;
                }

                log.info("Stock Update received: {}", stockDTO);

                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().n(stockDTO.getId().toString()).build());
                item.put("sku", AttributeValue.builder().s(stockDTO.getSku()).build());
                item.put("sellerId", AttributeValue.builder().s(String.valueOf(stockDTO.getSellerId())).build());
                item.put("soldQuantity", AttributeValue.builder().s(String.valueOf(stockDTO.getSoldQuantity())).build());
                item.put("stockAction", AttributeValue.builder().s(stockDTO.getStockAction()).build());
                item.put("reservedQuantity", AttributeValue.builder().s(String.valueOf(stockDTO.getReservedQuantity())).build());
                item.put("totalQuantity", AttributeValue.builder().s(String.valueOf(stockDTO.getTotalQuantity())).build());
                item.put("createdAt", AttributeValue.builder().s(String.valueOf(System.currentTimeMillis())).build());

                dynamoDbClient.putItem(builder -> builder.tableName("StockHistory").item(item));
                log.info("Stock Update stored: {}", item.toString());
            }

            return "OK";
        } catch (Exception e) {
            log.error("An error occurred on processing Lambda: {} ", e.getMessage());
            return "ERROR";
        }
    }
}
