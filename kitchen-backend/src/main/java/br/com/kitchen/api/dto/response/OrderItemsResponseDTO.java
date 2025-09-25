package br.com.kitchen.api.dto.response;

import br.com.kitchen.api.dto.ProductSkuDTO;
import br.com.kitchen.api.dto.SellerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemsResponseDTO {
    private Long id;
    private ProductSkuDTO productSkuDTO;
    public int quantity;
    private BigDecimal itemValue;
    private SellerDTO seller;
}
