package br.com.kitchen.orderpreparingsimulator.app.config;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.producer.OrderProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    @Value("${app.kafka.topic.order-status-updates}")
    private String orderStatusTopic;

    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public OrderProducer<OrderDTO> kafkaProducer() {
        return new OrderProducer<>(kafkaTemplate, orderStatusTopic);
    }
}