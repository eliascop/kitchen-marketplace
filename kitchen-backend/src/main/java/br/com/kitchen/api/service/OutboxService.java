package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.enumerations.EventType;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.repository.jpa.OutboxRepository;
import br.com.kitchen.api.util.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    public void publishProductEvent(Product product, EventType type) {
        ProductDTO dto = ProductDTO.builder()
                .id(product.getId())
                .productStatus(product.getProductStatus().toString())
                .build();

        String eType = type.equals(EventType.Created) ? "PRODUCT_CREATED" : "PRODUCT_UPDATED";

        saveEvent("PRODUCT", product.getId(), eType, dto);
    }

    public void createOrderEvent(Order orderSaved) {
        OrderDTO dto = OrderDTO.builder()
                .id(orderSaved.getId())
                .status(orderSaved.getStatus().toString())
                .build();

        saveEvent("ORDER", orderSaved.getId(), "ORDER_CONFIRMED", dto);
    }

    public void createStockEvent(StockDTO stockUpdated) {
        saveEvent("STOCK", stockUpdated.getId(),stockUpdated.getStockAction(), stockUpdated);
    }

    private void saveEvent(String aggregateType, Long aggregateId, String eventType, Object payload) {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(JsonUtils.toJson(payload))
                .status(EventStatus.PENDING)
                .build();

        outboxRepository.save(event);
    }
}
