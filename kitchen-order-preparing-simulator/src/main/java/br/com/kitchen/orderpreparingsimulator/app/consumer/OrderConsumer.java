package br.com.kitchen.orderpreparingsimulator.app.consumer;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.service.OrderPreparingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer extends KafkaGenericConsumer<OrderDTO> {

    public OrderConsumer(OrderPreparingService service) {
        super(OrderDTO.class, service::prepare);
    }

    @KafkaListener(topics = "new-order")
    public void listen(String message) {consume(message);}

    @Override
    protected boolean isValid(OrderDTO orderDTO) {
        return orderDTO.getId() != null && orderDTO.getStatus() != null && !orderDTO.getStatus().isEmpty();
    }

}
