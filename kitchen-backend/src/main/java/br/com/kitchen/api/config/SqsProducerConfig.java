package br.com.kitchen.api.config;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.producer.SqsProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsProducerConfig {

    private final SqsTemplate sqsTemplate;

    public SqsProducerConfig(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    private <T> SqsProducer<T> createProducer(String queueName) {
        return new SqsProducer<>(sqsTemplate, queueName);
    }

    @Bean
    public SqsProducer<ProductDTO> productSqsProducer() {
        return createProducer("product-events");
    }

    @Bean
    @Qualifier("orderSqsProducer")
    public SqsProducer<OrderDTO> orderSqsProducer() {
        return createProducer("order-events");
    }

    @Bean
    @Qualifier("walletSqsProducer")
    public SqsProducer<WalletTransaction> walletSqsProducer() {
        return createProducer("payment-events");
    }
}
