package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.enumerations.EventStatus;
import br.com.kitchen.api.model.OutboxEvent;
import br.com.kitchen.api.producer.KafkaProducer;
import br.com.kitchen.api.producer.SnsProducer;
import br.com.kitchen.api.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final SnsProducer orderProducer;
    private final KafkaProducer<ProductDTO> productProducer;
    private final ObjectMapper objectMapper;

    public OutboxProcessor(OutboxRepository outboxRepository,
                           SnsProducer orderProducer,
                           @Qualifier("productProducer") KafkaProducer<ProductDTO> productProducer,
                           ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.orderProducer = orderProducer;
        this.productProducer = productProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(EventStatus.PENDING);
        for (OutboxEvent event : events) {
            try {
                if ("ORDER".equals(event.getAggregateType()) && "ORDER_CONFIRMED".equals(event.getEventType())) {
                    OrderDTO dto = objectMapper.readValue(event.getPayload(), OrderDTO.class);
                    orderProducer.sendNotification(dto.toString());
                }
                if ("PRODUCT".equals(event.getAggregateType())) {
                    ProductDTO dto = objectMapper.readValue(event.getPayload(), ProductDTO.class);
                    productProducer.sendNotification(dto);
                }

                event.setStatus(EventStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                outboxRepository.save(event);

            } catch (Exception e) {
                event.setStatus(EventStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}

