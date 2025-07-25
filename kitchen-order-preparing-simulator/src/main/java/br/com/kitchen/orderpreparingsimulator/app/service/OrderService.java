package br.com.kitchen.orderpreparingsimulator.app.service;

import br.com.kitchen.orderpreparingsimulator.app.model.Order;
import br.com.kitchen.orderpreparingsimulator.app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
