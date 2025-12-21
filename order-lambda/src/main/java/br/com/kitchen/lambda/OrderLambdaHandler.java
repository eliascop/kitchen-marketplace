package br.com.kitchen.lambda;

import br.com.kitchen.dto.OrderDTO;
import br.com.kitchen.dto.SnsNotificationDTO;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OrderLambdaHandler implements RequestHandler<SQSEvent, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DynamoDbClient dynamoDbClient;

    public OrderLambdaHandler() {
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
        log.info("################## OrderLambda initialized ################## ");
        try {
            for (SQSEvent.SQSMessage msg: event.getRecords()) {

                SnsNotificationDTO notification = mapper.readValue(msg.getBody(), SnsNotificationDTO.class);
                OrderDTO orderDTO = mapper.readValue(notification.getMessage(), OrderDTO.class);

                if (orderDTO.getId() == null) {
                    log.warn("Message was ignored by lambda. Order id is null");
                    continue;
                }

                log.info("Order received in Lambda: {}", orderDTO);

                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().n(orderDTO.getId().toString()).build());
                item.put("status", AttributeValue.builder().s(orderDTO.getStatus()).build());

                dynamoDbClient.putItem(builder -> builder.tableName("Order").item(item));
                log.info("Order DTO was stored, order:"+ item.toString());
            }

            return "OK";
        } catch (Exception e) {
            log.error("An error occurred on processing lambda: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
