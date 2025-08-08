package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.ShippingDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Seller;
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
                shipping.getSeller() != null ? shipping.getSeller().getId() : null
        );
    }

    public static Shipping toEntity(ShippingDTO dto, Seller seller, Cart cart) {
        if (dto == null || seller == null || cart == null) return null;

        Shipping shipping = new Shipping();
        shipping.setId(dto.getId());
        shipping.setSeller(seller);
        shipping.setCart(cart);
        shipping.setCost(dto.getCost());
        shipping.setMethod(dto.getMethod());
        shipping.setEstimatedDays(dto.getEstimatedDays());
        return shipping;
    }

    public static List<ShippingDTO> toDTOList(List<Shipping> shippings) {
        return shippings.stream()
                .map(ShippingMapper::toDTO)
                .collect(Collectors.toList());
    }
}
