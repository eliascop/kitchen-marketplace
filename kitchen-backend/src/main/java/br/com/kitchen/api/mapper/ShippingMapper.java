package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.ShippingDTO;
import br.com.kitchen.api.model.Shipping;

import java.util.List;
import java.util.stream.Collectors;

public class ShippingMapper {

    public static ShippingDTO toDTO(Shipping shipping) {
        if (shipping == null) return null;

        return new ShippingDTO(
                shipping.getId(),
                shipping.getMethod(),
                shipping.getCost(),
                shipping.getEstimatedDays(),
                shipping.getSeller() != null ? SellerMapper.toDTO(shipping.getSeller()) : null
        );
    }

    public static List<ShippingDTO> toDTOList(List<Shipping> shipping) {
        return shipping.stream()
                .map(ShippingMapper::toDTO)
                .collect(Collectors.toList());
    }
}
