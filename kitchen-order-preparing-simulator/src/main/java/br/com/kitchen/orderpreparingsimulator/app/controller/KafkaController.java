package br.com.kitchen.orderpreparingsimulator.app.controller;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.producer.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final OrderProducer<OrderDTO> orderKafkaProducer;

    @Autowired
    public KafkaController(OrderProducer<OrderDTO> orderKafkaProducer) {
        this.orderKafkaProducer = orderKafkaProducer;
    }

    @PostMapping("/publish/order-notification")
    public String publishOrderNotification(@RequestBody OrderDTO notification) {
        orderKafkaProducer.sendNotification(notification);
        return "Notificação de novo pedido com ID " + notification.getId() + " publicada no Kafka!";
    }
}
