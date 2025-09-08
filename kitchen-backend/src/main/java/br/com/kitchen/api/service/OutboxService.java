package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.model.Stock;
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

    public void createOrderEvent(Order orderSaved) {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("ORDER")
                .aggregateId(orderSaved.getId())
                .eventType("ORDER_CONFIRMED")
                .payload(JsonUtils.toJson(
                        new OrderDTO(orderSaved.getId(), orderSaved.getStatus().toString())
                ))
                .status(EventStatus.PENDING)
                .build();
        outboxRepository.save(event);
    }

    public void createStockEvent(Stock stockUpdate) {
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("STOCK")
                .aggregateId(stockUpdate.getId())
                .eventType("STOCK_CONFIRMED")
                .payload(JsonUtils.toJson(
                        StockDTO.builder()
                                .id(stockUpdate.getId())
                                .sku(stockUpdate.getSku().getSku())
                                .sellerId(stockUpdate.getSeller().getId())
                                .soldQuantity(stockUpdate.getSoldQuantity())
                                .reservedQuantity(stockUpdate.getReservedQuantity())
                                .totalQuantity(stockUpdate.getTotalQuantity())
                                .build()

                ))
                .status(EventStatus.PENDING)
                .build();
        outboxRepository.save(event);
    }

}
