package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.WalletTransactionDTO;
import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.producer.SnsProducer;
import br.com.kitchen.api.repository.jpa.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final SnsProducer snsProducer;
    private final ObjectMapper objectMapper;

    public OutboxProcessor(OutboxRepository outboxRepository,
                           SnsProducer snsProducer,
                           ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.snsProducer = snsProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 10000)
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(EventStatus.PENDING);
        for (OutboxEvent event: events) {
            try {
                if ("ORDER".equals(event.getAggregateType()) && "ORDER_CONFIRMED".equals(event.getEventType())) {
                    OrderDTO dto = objectMapper.readValue(event.getPayload(), OrderDTO.class);
                    snsProducer.sendOrderNotification(dto);
                }
                if ("STOCK".equals(event.getAggregateType())) {
                    StockDTO dto = objectMapper.readValue(event.getPayload(), StockDTO.class);
                    snsProducer.sendStockNotification(dto);
                }
                if ("PRODUCT".equals(event.getAggregateType())) {
                    ProductDTO dto = objectMapper.readValue(event.getPayload(), ProductDTO.class);
                    snsProducer.sendProductNotification(dto);
                }
                if ("WALLET-TRANSACTION".equals(event.getAggregateType())) {
                    WalletTransactionDTO dto = objectMapper.readValue(event.getPayload(), WalletTransactionDTO.class);
                    snsProducer.sendWalletTransactionNotification(dto);
                }

                event.setStatus(EventStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                outboxRepository.save(event);

            } catch (Exception e) {
                log.error("An error occurred on save message {}" , e.getMessage());
                event.setStatus(EventStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}

