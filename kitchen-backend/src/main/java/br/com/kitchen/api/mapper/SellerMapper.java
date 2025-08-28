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

    public static Seller toEntity(SellerDTO sellerDTO){
        Seller seller = new Seller();
        seller.setId(sellerDTO.getId());
        seller.setStoreName(sellerDTO.getStoreName());
        return seller;
    }
}
