package br.com.kitchen.ordermonitoring.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private String status;

    @Override
    public String toString() {
        return "OrderDTO{id=" + id + ", status='" + status + "'}";
    }
}