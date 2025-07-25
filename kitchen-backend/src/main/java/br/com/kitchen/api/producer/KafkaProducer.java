package br.com.kitchen.api.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaProducer<T> {

    private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

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
