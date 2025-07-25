package br.com.kitchen.api.builder;

import br.com.kitchen.api.dto.PaypalItemDTO;
import br.com.kitchen.api.dto.PaypalOrderDTO;
import br.com.kitchen.api.model.WalletTransaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.RoundingMode;
import java.util.List;

public class PaypalOrderBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String baseUrl = System.getenv("APP_BASE_URL");
    private static final String serviceRote = "/api/payment/paypal";

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String buildOrderJson(WalletTransaction walletTx) {
        PaypalItemDTO item = new PaypalItemDTO(
                "CREDIT",
                walletTx.getDescription(),
                new PaypalItemDTO.UnitAmountDTO("BRL", scale(walletTx.getAmount())),
                "1"
        );

        PaypalOrderDTO.Amount amount = new PaypalOrderDTO.Amount(
                "BRL",
                scale(walletTx.getAmount()),
                new PaypalOrderDTO.Breakdown(
                        new PaypalOrderDTO.ItemTotal("BRL", scale(walletTx.getAmount()))
                )
        );

        PaypalOrderDTO.PurchaseUnit purchaseUnit = new PaypalOrderDTO.PurchaseUnit(
                "Crédito na conta KitchenApp",
                amount,
                List.of(item)
        );

        PaypalOrderDTO order = new PaypalOrderDTO();
        order.setIntent("CAPTURE");
        order.setPurchase_units(List.of(purchaseUnit));

        order.setApplication_context(new PaypalOrderDTO.ApplicationContext(
                baseUrl + serviceRote + "/success?walletTxId=" + walletTx.getId()+"&secureToken=" + walletTx.getSecureToken(),
                baseUrl + serviceRote + "/cancelled?walletTxId=" + walletTx.getId() +"&secureToken=" + walletTx.getSecureToken()));

        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar pedido PayPal", e);
        }
    }

    private static String scale(java.math.BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
