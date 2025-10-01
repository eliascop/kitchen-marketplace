package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.response.OrderItemsResponseDTO;
import br.com.kitchen.api.dto.response.OrderResponseDTO;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OrderItems;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderResponseDTO toDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCreation(order.getCreation());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setOrderItems(toItemDTOList(order.getOrderItems()));
        dto.setPayment(PaymentMapper.toDTO(order.getPayment()));
        return dto;
    }

    public static OrderItemsResponseDTO itemToDTO(OrderItems orderItems){
        OrderItemsResponseDTO dto = new OrderItemsResponseDTO();
        dto.setId(orderItems.getId());
        dto.setSku(orderItems.getProductSku().getSku());
        dto.setPrice(orderItems.getProductSku().getPrice());
        dto.setProductName(orderItems.getProductSku().getProduct().getName());
        dto.setQuantity(orderItems.getQuantity());
        dto.setItemValue(orderItems.getItemValue());
        dto.setStoreName(orderItems.getSeller().getStoreName());
        return dto;
    }

    public static List<OrderItemsResponseDTO> toItemDTOList(List<OrderItems> items) {
        return items.stream()
                .map(OrderMapper::itemToDTO)
                .collect(Collectors.toList());
    }

    public static List<OrderResponseDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
}