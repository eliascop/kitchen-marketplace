package br.com.kitchen.orderpreparingsimulator.app.producer;

import org.springframework.kafka.core.KafkaTemplate;

public class OrderProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;

    public OrderProducer(KafkaTemplate<String, T> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendNotification(T data) {
        kafkaTemplate.send(topic, data);
    }
}
