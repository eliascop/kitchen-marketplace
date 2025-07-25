package br.com.kitchen.orderpreparingsimulator.app.service;

import br.com.kitchen.orderpreparingsimulator.app.dto.OrderDTO;
import br.com.kitchen.orderpreparingsimulator.app.producer.OrderProducer;
import br.com.kitchen.orderpreparingsimulator.app.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class OrderPreparingService {

    private final OrderProducer<OrderDTO> producer;
    private final OrderRepository orderRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Garante 1 por vez
    private final BlockingQueue<OrderDTO> queue = new LinkedBlockingQueue<>();
    private final Random random = new Random();

    @Autowired
    public OrderPreparingService(OrderProducer<OrderDTO> kafkaProducer, OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.producer = kafkaProducer;
    }

    @PostConstruct
    public void startOrderProcessor() {
        executor.submit(() -> {
            while (true) {
                try {
                    OrderDTO order = queue.take();
                    processOrder(order);
                } catch (InterruptedException e) {
                    System.out.println("erro: "+e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void prepare(OrderDTO order) {
        queue.offer(order);
    }

    private void processOrder(OrderDTO order) {
        try {
            Thread.sleep(5000L);

            order.setStatus("PREPARING");
            updateOrderStatus(order);

            int delaySeconds = 10 + random.nextInt(31);
            Thread.sleep(delaySeconds * 1000L);

            order.setStatus("PREPARED");
            updateOrderStatus(order);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    public void updateOrderStatus(OrderDTO orderDTO) {
        try {
            orderRepository.findById(orderDTO.getId()).ifPresent(order -> {
                order.setStatus(orderDTO.getStatus());
                orderRepository.save(order);
            });
            publishToKafka(orderDTO);
        } catch (Exception e) {
            System.out.println("Erro ao atualizar pedido " + orderDTO.getId() + ": " + e.getMessage());
        }
    }

    private void publishToKafka(OrderDTO orderDTO) {
        producer.sendNotification(orderDTO);
    }
}
