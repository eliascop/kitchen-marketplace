package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.OrderItemsDTO;
import br.com.kitchen.api.dto.OrderDTO;
import br.com.kitchen.api.model.Order;
import br.com.kitchen.api.model.OrderItems;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreation(order.getCreation());
        dto.setStatus(order.getStatus().toString());
        dto.setTotal(order.getTotal());
        dto.setCustomerId(order.getUser().getId());
        dto.setOrderItems(toItemDTOList(order.getOrderItems()));
        dto.setPayment(PaymentMapper.toDTO(order.getPayment()));
        return dto;
    }

    public static OrderItemsDTO itemToDTO(OrderItems orderItems){
        OrderItemsDTO dto = new OrderItemsDTO();
        dto.setId(orderItems.getId());
        dto.setSku(orderItems.getProductSku().getSku());
        dto.setPrice(orderItems.getProductSku().getPrice());
        dto.setProductName(orderItems.getProductSku().getProduct().getName());
        dto.setQuantity(orderItems.getQuantity());
        dto.setItemValue(orderItems.getItemValue());
        dto.setShipping(ShippingMapper.toDTO(orderItems.getSellerOrder().getShipping()));
        return dto;
    }

    public static List<OrderItemsDTO> toItemDTOList(List<OrderItems> items) {
        return items.stream()
                .map(OrderMapper::itemToDTO)
                .collect(Collectors.toList());
    }

    public static List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
}