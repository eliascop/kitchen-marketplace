package br.com.kitchen.notification.infrastructure.messaging.sns;

import br.com.kitchen.notification.infrastructure.messaging.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsEventPublisher implements EventPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void publish(String topicArn, T payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            snsClient.publish(request);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to publish SNS event", e);
        }
    }
}