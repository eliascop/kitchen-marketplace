package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.enumerations.AggregateType;
import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.enumerations.EventType;
import br.com.kitchen.api.mapper.OrderMapper;
import br.com.kitchen.api.mapper.ProductMapper;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.WalletTransaction;
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
        ProductDTO dto = ProductMapper.toProductResponseDTO(product);
        String eType = type.equals(EventType.Created) ? "PRODUCT_CREATED" : "PRODUCT_UPDATED";

        saveEvent(AggregateType.PRODUCT, product.getId(), eType, dto);
    }

    public void createOrderEvent(Order orderSaved, String eventType) {
        OrderDTO dto = OrderMapper.toDTO(orderSaved);
        saveEvent(AggregateType.ORDER, orderSaved.getId(), eventType, dto);
    }

    public void createStockEvent(StockDTO stockUpdated) {
        saveEvent(AggregateType.STOCK, stockUpdated.getId(),stockUpdated.getStockAction(), stockUpdated);
    }

    public void publishWalletTransactionEvent(WalletTransaction tx, String eventType) {
        saveEvent(AggregateType.WALLET_TRANSACTION, tx.getId(), eventType, tx);
    }

    private void saveEvent(AggregateType aggregateType, Long aggregateId, String eventType, Object payload) {
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
