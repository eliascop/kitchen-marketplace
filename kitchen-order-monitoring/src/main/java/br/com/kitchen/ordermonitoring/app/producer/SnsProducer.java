package br.com.kitchen.ordermonitoring.app.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnsProducer {

    @Value("${payment.events.topic}")
    private String topicArn;

    private final SnsClient snsClient;

    private final ObjectMapper objectMapper;

    public <T> void publishToTopic(T data) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(data);
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(jsonMessage)
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("Message sent to SNS. Topic: {}, messageId: {}", topicArn, response.messageId());
        } catch (JsonProcessingException e) {
            log.error("Error on public message to topic", e);
            throw new RuntimeException("Erro ao serializar a mensagem para JSON", e);
        }
    }
}
