package br.com.kitchen.lambda;

import br.com.kitchen.dto.OrderDTO;
import br.com.kitchen.dto.SnsNotificationDTO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class OrderLambdaHandler implements RequestHandler<SQSEvent, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DynamoDbClient dynamoDbClient;

    public OrderLambdaHandler() {
        String lsHost = System.getenv("LOCALSTACK_HOSTNAME");
        URI endpoint = URI.create("http://" + lsHost + ":4566");

        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(endpoint)
                .region(software.amazon.awssdk.regions.Region.US_EAST_1)
                .build();
    }

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        try {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {

                SnsNotificationDTO notification = mapper.readValue(msg.getBody(), SnsNotificationDTO.class);
                OrderDTO orderDTO = mapper.readValue(notification.getMessage(), OrderDTO.class);

                if (orderDTO.getId() == null) {
                    System.out.println("Mensagem ignorada: Order id é null");
                    continue;
                }

                System.out.println("Pedido recebido na Lambda: " + orderDTO);

                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().n(orderDTO.getId().toString()).build());
                item.put("status", AttributeValue.builder().s(orderDTO.getStatus()).build());

                dynamoDbClient.putItem(builder -> builder.tableName("Order").item(item));
            }

            return "OK";
        } catch (Exception e) {
            System.out.println("Erro ao processar a Lambda: " + e.getMessage());
            return "ERROR";
        }
    }
}
