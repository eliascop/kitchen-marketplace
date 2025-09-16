package br.com.kitchen.orderpreparingsimulator.app.dto;

import br.com.kitchen.orderpreparingsimulator.app.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    private Long id;
    private String status;

}
