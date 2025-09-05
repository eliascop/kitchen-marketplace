package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.repository.OutboxRepository;
import br.com.kitchen.api.util.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    public void publishProductCreated(Product product) {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("PRODUCT")
                .aggregateId(product.getId())
                .eventType("PRODUCT_CREATED")
                .payload(JsonUtils.toJson(ProductDTO.builder().id(product.getId()).build()))
                .build();

        outboxRepository.save(event);
    }

}
