package br.com.kitchen.api.config;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.WalletTransactionDTO;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    private final ProducerFactory<String, ProductDTO> producerProductFactory;
    private final ProducerFactory<String, WalletTransactionDTO> producerWalletTransactionFactory;

    public KafkaProducerConfig(ProducerFactory<String, ProductDTO> producerProductFactory,
                               ProducerFactory<String, WalletTransactionDTO> producerWalletTransactionFactory) {
        this.producerProductFactory = producerProductFactory;
        this.producerWalletTransactionFactory = producerWalletTransactionFactory;
    }

    @Bean
    @Qualifier("productProducer")
    public KafkaProducer<ProductDTO> productKafkaProducer() {
        KafkaTemplate<String, ProductDTO> template = new KafkaTemplate<>(producerProductFactory);
        return new KafkaProducer<>(template, "new-product");
    }

    @Bean
    @Qualifier("walletKafkaProducer")
    public KafkaProducer<WalletTransactionDTO> walletKafkaProducer() {
        KafkaTemplate<String, WalletTransactionDTO> template = new KafkaTemplate<>(producerWalletTransactionFactory);
        return new KafkaProducer<>(template, "new-transaction");
    }
}
