package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.SellerDTO;
import br.com.kitchen.api.model.Seller;

public class SellerMapper {

    public static SellerDTO toDTO(Seller seller) {
        return new SellerDTO(
                seller.getId(),
                seller.getStoreName()
        );
    }
}
