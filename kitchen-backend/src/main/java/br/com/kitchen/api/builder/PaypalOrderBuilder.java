package br.com.kitchen.api.builder;

import br.com.kitchen.api.dto.PaypalItemDTO;
import br.com.kitchen.api.dto.PaypalOrderDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaypalOrderBuilder {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${paypal.service-route}")
    private String serviceRote;

    public String buildOrderJson(Cart cart) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        List<PaypalItemDTO> items = cart.getCartItems().stream().map(item -> {
            Product product = item.getProductSku().getProduct();
            return new PaypalItemDTO(
                    product.getName(),
                    product.getDescription(),
                    String.valueOf(item.getQuantity()),
                    new PaypalItemDTO.UnitAmountDTO("BRL", scale(item.getItemValue()))
            );
        }).toList();

        PaypalOrderDTO paypalOrderDTO = new PaypalOrderDTO();
        paypalOrderDTO.setIntent("CAPTURE");

        PaypalOrderDTO.Amount amount = new PaypalOrderDTO.Amount();
        amount.setCurrencyCode("BRL");
        amount.setValue(scale(cart.getCartTotal()));
        amount.setBreakdown(new PaypalOrderDTO.Breakdown(
            new PaypalOrderDTO.ItemTotal("BRL", scale(cart.getCartTotal()))
        ));

        String invoiceId = "PAY-"+cart.getId() + "-" + UUID.randomUUID();
        paypalOrderDTO.setPurchaseUnits(List.of(new PaypalOrderDTO.PurchaseUnit(
            "Kitchen order payment",
            amount,
            invoiceId,
            items
        )));

        String successUrl = serviceRote + "status=success&cartId=" + cart.getId() + "&secureToken=" + cart.getPayment().getSecureToken();
        String cancelUrl = serviceRote + "status=cancelled&cartId=" + cart.getId() + "&secureToken=" + cart.getPayment().getSecureToken();

        paypalOrderDTO.setApplicationContext(new PaypalOrderDTO.ApplicationContext(
            "KitchenWeb Marketplace",
            "LOGIN",
            "CONTINUE",
            successUrl,
            cancelUrl
        ));

        try {
            return mapper.writeValueAsString(paypalOrderDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("An error has occurred while PaypalOrderDTO serialization:",e);
        }
    }

    private String scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
