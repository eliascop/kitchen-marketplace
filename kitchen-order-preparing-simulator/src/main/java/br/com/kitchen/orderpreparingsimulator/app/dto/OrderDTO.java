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

    public static OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        return dto;
    }

    @Override
    public String toString() {
        return "OrderDTO{id=" + id + ", status='" + status + "'}";
    }
}
