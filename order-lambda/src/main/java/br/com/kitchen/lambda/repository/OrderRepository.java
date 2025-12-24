package br.com.kitchen.lambda.repository;

import br.com.kitchen.lambda.dto.OrderDTO;

public interface OrderRepository {
    void save(OrderDTO order);
}