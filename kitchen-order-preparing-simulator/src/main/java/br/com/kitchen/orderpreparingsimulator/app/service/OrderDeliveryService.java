package br.com.kitchen.orderpreparingsimulator.app.service;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.model.Order; // Supondo que exista uma classe Order
import br.com.kitchen.orderpreparingsimulator.app.producer.OrderProducer;
import br.com.kitchen.orderpreparingsimulator.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderDeliveryService {

    private final OrderRepository orderRepository;
    private final OrderProducer<OrderDTO> producer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Garante a retirada de um por vez
    private final Random random = new Random();

    @Autowired
    public OrderDeliveryService(OrderRepository orderRepository, OrderProducer<OrderDTO> producer) {
        this.orderRepository = orderRepository;
        this.producer = producer;
    }

    @PostConstruct
    public void startOrderDeliveryProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    List<Order> preparedOrders = orderRepository.findByStatus("PREPARED");

                    if (!preparedOrders.isEmpty()) {
                        int index = random.nextInt(preparedOrders.size());
                        Order order = preparedOrders.get(index);

                        int delaySeconds = 10 + random.nextInt(31);
                        Thread.sleep(delaySeconds * 1000L);

                        order.setStatus("DELIVERED");
                        orderRepository.save(order);
                        publishToKafka(order);
                    } else {
                        Thread.sleep(1000L);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Processamento de entrega interrompido: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println("Erro no processamento de entrega: " + e.getMessage());
                }
            }
        });
    }

    private void publishToKafka(Order order) {
        OrderDTO orderDTO = new OrderDTO(order.getId(),order.getStatus());
        producer.sendNotification(orderDTO);
    }
}
