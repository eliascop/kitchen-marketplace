package br.com.kitchen.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private LocalDateTime creation;
    private String status;
    private BigDecimal total;
    private Long customerId;
    private List<OrderItemsDTO> items = new ArrayList<>();
    private PaymentDTO payment;

}
