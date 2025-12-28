package br.com.kitchen.lambda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime creation;
    private String status;
    private BigDecimal total;
    private Long customerId;
    private List<OrderItemsDTO> items = new ArrayList<>();
    private PaymentDTO payment;
}
