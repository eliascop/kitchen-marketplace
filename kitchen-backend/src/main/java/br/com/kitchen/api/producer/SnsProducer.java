package br.com.kitchen.api.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@RequiredArgsConstructor
public class SnsProducer {

    @Value("${order.events.topic}")
    private String topicOrderArn;

    @Value("${stock.events.topic}")
    private String topicStockArn;

    private final SnsClient snsClient;

    private final ObjectMapper objectMapper;

    public <T> void sendOrderNotification(T data) {
        publishToTopic(topicOrderArn, data);
    }

    public <T> void sendStockNotification(T data) {
        publishToTopic(topicStockArn, data);
    }

    private <T> void publishToTopic(String topicArn, T data) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(data);
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(jsonMessage)
                    .build();

            PublishResponse response = snsClient.publish(request);
            System.out.println("Mensagem enviada ao SNS (" + topicArn + "), ID: " + response.messageId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar a mensagem para JSON", e);
        }
    }
}
