package br.com.kitchen.api.dto.response;

import br.com.kitchen.api.enumerations.OrderStatus;
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
public class OrderResponseDTO {

    private Long id;
    private LocalDateTime creation;
    private OrderStatus status;
    private BigDecimal total;
    @JsonProperty("items")
    private List<OrderItemsResponseDTO> orderItems = new ArrayList<>();

}
