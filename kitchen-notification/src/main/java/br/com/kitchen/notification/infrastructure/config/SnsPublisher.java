package br.com.kitchen.notification.infrastructure.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    public <T> void publish(String topicArn, T payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);

            log.info("SNS message published. Topic={}, messageId={}", topicArn, response.messageId());
        } catch (JsonProcessingException ex) {
            log.error("Error serializing SNS message", ex);
            throw new IllegalStateException("Failed to serialize SNS message", ex);
        }
    }
}