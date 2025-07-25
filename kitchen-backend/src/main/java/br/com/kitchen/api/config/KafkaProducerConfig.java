package br.com.kitchen.api.config;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put("spring.json.trusted.packages", "br.com.kitchen.api.dto");
        return props;
    }

    private <T> KafkaTemplate<String, T> createTemplate(Class<T> clazz) {
        DefaultKafkaProducerFactory<String, T> factory =
                new DefaultKafkaProducerFactory<>(producerConfigs());
        return new KafkaTemplate<>(factory);
    }

    private <T> KafkaProducer<T> createProducer(Class<T> clazz, String topic) {
        return new KafkaProducer<>(createTemplate(clazz), topic);
    }

    @Bean
    public KafkaProducer<ProductDTO> productKafkaProducer() {
        return createProducer(ProductDTO.class, "new-product");
    }

    @Bean
    @Qualifier("orderKafkaProducer")
    public KafkaProducer<OrderDTO> orderKafkaProducer() {
        return createProducer(OrderDTO.class, "new-order");
    }

    @Bean
    @Qualifier("walletKafkaProducer")
    public KafkaProducer<WalletTransaction> walletKafkaProducer() {
        return createProducer(WalletTransaction.class, "new-transaction");
    }
}
