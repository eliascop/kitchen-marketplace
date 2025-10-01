package br.com.kitchen.api.producer;

import org.springframework.kafka.core.KafkaTemplate;

public class KafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;

    public KafkaProducer(KafkaTemplate<String, T> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendNotification(T data) {
        kafkaTemplate.send(topic, data);
    }
}
