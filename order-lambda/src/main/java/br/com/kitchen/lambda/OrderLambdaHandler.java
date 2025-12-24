package br.com.kitchen.lambda;

import br.com.kitchen.lambda.application.IndexOrderUseCase;
import br.com.kitchen.lambda.dto.OrderDTO;
import br.com.kitchen.lambda.dto.SnsNotificationDTO;
import br.com.kitchen.lambda.factory.DynamoDbClientFactory;
import br.com.kitchen.lambda.repository.OrderRepository;
import br.com.kitchen.lambda.repository.impl.DynamoOrderRepository;
import br.com.kitchen.lambda.utils.JsonUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Slf4j
public class OrderLambdaHandler implements RequestHandler<SQSEvent, String> {
    
    private final IndexOrderUseCase indexOrderUseCase;
    
    private final OrderRepository orderRepository;

    public OrderLambdaHandler() {
        DynamoDbClient dynamoDbClient = DynamoDbClientFactory.create();
        this.orderRepository = new DynamoOrderRepository(dynamoDbClient);
        this.indexOrderUseCase = new IndexOrderUseCase();
    }

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        log.info("################## OrderLambda initialized ##################");

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            try {
                OrderDTO orderDTO = extractOrder(msg);

                if (orderDTO == null) {
                    log.warn("OrderDTO is null, skipping message {}", msg.getMessageId());
                    continue;
                }

                log.info("Order received in Lambda: {}", orderDTO);

                orderRepository.save(orderDTO);
                indexOrderUseCase.execute(orderDTO);

            } catch (Exception e) {
                log.error("Error processing SQS message {}", msg.getMessageId(), e);
            }
        }

        return "OK";
    }

    private OrderDTO extractOrder(SQSEvent.SQSMessage msg) {
        try {
            SnsNotificationDTO notification =
                    JsonUtils.MAPPER.readValue(msg.getBody(), SnsNotificationDTO.class);

            if (notification == null || notification.getMessage() == null) {
                log.error("Invalid SNS notification payload");
                return null;
            }

            OrderDTO order =
                    JsonUtils.MAPPER.readValue(notification.getMessage(), OrderDTO.class);

            log.info("Processando pedido {}", order.toString());
            return order;

        } catch (Exception e) {
            log.error("Erro ao deserializar o pedido da mensagem SQS", e);
            return null;
        }
    }
}