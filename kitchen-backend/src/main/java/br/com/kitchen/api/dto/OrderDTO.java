package br.com.kitchen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long id;
    private String status;

    public OrderDTO(Long id){
        this.id = id;
    }
    @Override
    public String toString() {
        return "OrderDTO{id=" + id + ", status='" + status + "'}";
    }
}