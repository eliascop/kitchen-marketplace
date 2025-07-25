package br.com.kitchen.orderpreparingsimulator.app.config;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.producer.OrderProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put("spring.json.add.type.headers", false);
        props.put("spring.json.trusted.packages", "br.com.kitchen.orderpreparingsimulator.app.dto");
        return props;
    }

    private <T> KafkaTemplate<String, T> createTemplate(Class<T> clazz) {
        DefaultKafkaProducerFactory<String, T> factory =
                new DefaultKafkaProducerFactory<>(producerConfigs());
        return new KafkaTemplate<>(factory);
    }

    private <T> OrderProducer<T> createProducer(Class<T> clazz, String topic) {
        return new OrderProducer<>(createTemplate(clazz), topic);
    }

    @Bean
    public OrderProducer<OrderDTO> kafkaProducer() {
        return createProducer(OrderDTO.class, "order-status-updates");
    }
}
