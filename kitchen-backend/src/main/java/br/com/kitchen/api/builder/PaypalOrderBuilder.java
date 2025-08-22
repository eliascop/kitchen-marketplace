package br.com.kitchen.api.builder;

import br.com.kitchen.api.dto.PaypalItemDTO;
import br.com.kitchen.api.dto.PaypalOrderDTO;
import br.com.kitchen.api.model.Cart;
import br.com.kitchen.api.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.RoundingMode;
import java.util.List;

public class PaypalOrderBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String baseUrl = System.getenv("paypal.base.url");
    private static final String serviceRote = "http://localhost/cart?status=";

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String buildOrderJson(Cart cart) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        List<PaypalItemDTO> items = cart.getCartItems().stream().map(item -> {
            Product product = item.getProduct();
            return new PaypalItemDTO(
                    product.getName(),
                    product.getDescription(),
                    String.valueOf(item.getQuantity()),
                    new PaypalItemDTO.UnitAmountDTO("BRL", item.getItemValue().setScale(2, RoundingMode.HALF_UP).toString())
            );
        }).toList();

        PaypalOrderDTO paypalOrderDTO = new PaypalOrderDTO();
        paypalOrderDTO.setIntent("CAPTURE");

        PaypalOrderDTO.Amount amount = new PaypalOrderDTO.Amount();
        amount.setCurrency_code("BRL");
        amount.setValue(cart.getCartTotal().setScale(2, RoundingMode.HALF_UP).toString());
        amount.setBreakdown(new PaypalOrderDTO.Breakdown(
                new PaypalOrderDTO.ItemTotal("BRL", cart.getCartTotal().setScale(2,RoundingMode.HALF_UP).toString())
        ));

        paypalOrderDTO.setPurchase_units(List.of(new PaypalOrderDTO.PurchaseUnit(
                "Pedido da KitchenApp",
                amount,
                items
        )));

        paypalOrderDTO.setApplication_context(new PaypalOrderDTO.ApplicationContext(
                serviceRote + "success&cartId=" + cart.getId() + "&secureToken=" + cart.getPayment().getSecureToken(),
                serviceRote + "cancelled&cartId=" + cart.getId() + "&secureToken=" + cart.getPayment().getSecureToken()
        ));

        try {
            return mapper.writeValueAsString(paypalOrderDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String scale(java.math.BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
