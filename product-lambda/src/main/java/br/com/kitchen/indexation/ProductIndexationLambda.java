package br.com.kitchen.indexation;

import br.com.kitchen.indexation.application.IndexProductUseCase;
import br.com.kitchen.indexation.dto.ProductDTO;
import br.com.kitchen.indexation.dto.SnsNotificationDTO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductIndexationLambda implements RequestHandler<SQSEvent, Void> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final IndexProductUseCase indexProductUseCase;

    public ProductIndexationLambda() {
        this.indexProductUseCase = new IndexProductUseCase();
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        log.info("################ Lambda acionada ################");

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            ProductDTO product = extractProduct(msg);
            if (product == null) continue;
            indexProductUseCase.execute(product);
        }
        return null;
    }

    private ProductDTO extractProduct(SQSEvent.SQSMessage msg) {
        try {
            SnsNotificationDTO notification =
                    MAPPER.readValue(msg.getBody(), SnsNotificationDTO.class);

            ProductDTO product =
                    MAPPER.readValue(notification.getMessage(), ProductDTO.class);

            if (product == null) {
                log.error("Product not mapped. Skipping...");
                return null;
            }

            log.info("Processando produto {}", product.getId());
            return product;

        } catch (Exception e) {
            log.error("Erro ao deserializar produto da mensagem SQS", e);
            return null;
        }
    }
}