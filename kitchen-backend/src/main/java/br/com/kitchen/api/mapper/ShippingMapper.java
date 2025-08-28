package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.ShippingDTO;
import br.com.kitchen.api.model.Shipping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShippingMapper {

    public static ShippingDTO toDTO(Shipping shipping) {
        if (shipping == null) return null;

        return ShippingDTO.builder()
                .id(shipping.getId())
                .carrier(shipping.getCarrier())
                .method(shipping.getMethod())
                .cost(shipping.getCost())
                .estimatedDays(shipping.getEstimatedDays())
                .seller(shipping.getSeller() != null ? SellerMapper.toDTO(shipping.getSeller()) : null)
                .build();
    }

    public static Set<ShippingDTO> toDTOList(Set<Shipping> shippingList) {
        return shippingList.stream()
                .map(ShippingMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public static Shipping toEntity(ShippingDTO shippingDTO) {
        Shipping ship = new Shipping();
        ship.setCarrier(shippingDTO.getCarrier());
        ship.setMethod(shippingDTO.getMethod());
        ship.setCost(shippingDTO.getCost());
        ship.setEstimatedDays(shippingDTO.getEstimatedDays());
        ship.setSeller(SellerMapper.toEntity(shippingDTO.getSeller()));
        return ship;
    }

    public static Set<Shipping> toEntityList(Set<ShippingDTO> shippingDTOList) {
        if(shippingDTOList.isEmpty()) return Collections.emptySet();
        return shippingDTOList.stream()
                .map(ShippingMapper::toEntity)
                .collect(Collectors.toSet());
    }
}
