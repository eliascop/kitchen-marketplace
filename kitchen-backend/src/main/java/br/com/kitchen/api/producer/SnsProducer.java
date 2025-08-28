package br.com.kitchen.api.producer;

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
    private String topicArn;

    private final SnsClient snsClient;

    public void sendNotification(String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        PublishResponse response = snsClient.publish(request);
        String messageId = response.messageId();
        System.out.println("Mensagem enviada ao SNS, ID: " + messageId);
    }
}
