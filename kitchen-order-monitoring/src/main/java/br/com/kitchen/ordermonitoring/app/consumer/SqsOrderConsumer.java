package br.com.kitchen.ordermonitoring.app.consumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsOrderConsumer {

    @SqsListener("order-events")
    public void listenNewOrder(String message) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> Pedido recebido: "+message);
    }

}
