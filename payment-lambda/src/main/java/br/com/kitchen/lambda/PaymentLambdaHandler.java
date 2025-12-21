package br.com.kitchen.lambda;

import br.com.kitchen.dto.PayPalWebhookDTO;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PaymentLambdaHandler implements RequestHandler<SQSEvent, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DynamoDbClient dynamoDbClient;

    public PaymentLambdaHandler() {

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
        try {
            for (SQSEvent.SQSMessage msg: event.getRecords()) {

                SnsNotificationDTO notification = mapper.readValue(msg.getBody(), SnsNotificationDTO.class);
                PayPalWebhookDTO webhookDTO = mapper.readValue(notification.getMessage(), PayPalWebhookDTO.class);
                if (webhookDTO.getProviderId() == null) {
                    log.error("Message was ignored: PaymentUpdate id é null");
                    continue;
                }

                log.info("PaymentHistory received: {}", webhookDTO);
                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().s(webhookDTO.getProviderId()).build());
                item.put("createTime", AttributeValue.builder().s(webhookDTO.getCreateTime()).build());
                item.put("status", AttributeValue.builder().s(webhookDTO.getStatus()).build());
                item.put("cartId", AttributeValue.builder().s(webhookDTO.getCartId()).build());
                item.put("paymentAmount", AttributeValue.builder().s(webhookDTO.getPaymentAmount()).build());
                item.put("paymentCurrency", AttributeValue.builder().s(webhookDTO.getPaymentCurrency()).build());
                item.put("payerId", AttributeValue.builder().s(webhookDTO.getPayerId()).build());
                item.put("payerEmail", AttributeValue.builder().s(webhookDTO.getPayerEmail()).build());

                item.put("receiveAt", AttributeValue.builder().s(LocalDateTime.now().toString()).build());

                dynamoDbClient.putItem(builder -> builder
                        .tableName("PaymentHistory")
                        .item(item));

                log.info("PaymentHistory stored: {}", item);
            }

            return "OK";
        } catch (Exception e) {
            log.error("An error occurred on processing Lambda: {} ", e.getMessage());
            return "ERROR";
        }
    }
}
