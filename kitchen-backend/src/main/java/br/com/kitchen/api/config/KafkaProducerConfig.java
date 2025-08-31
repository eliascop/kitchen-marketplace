package br.com.kitchen.api.config;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    private final ProducerFactory<String, ProductDTO> producerFactory;

    public KafkaProducerConfig(ProducerFactory<String, ProductDTO> producerFactory) {
        this.producerFactory = producerFactory;
    }

    @Bean
    @Qualifier("productProducer")
    public KafkaProducer<ProductDTO> productKafkaProducer() {
        KafkaTemplate<String, ProductDTO> template = new KafkaTemplate<>(producerFactory);
        return new KafkaProducer<>(template, "new-product");
    }
}
