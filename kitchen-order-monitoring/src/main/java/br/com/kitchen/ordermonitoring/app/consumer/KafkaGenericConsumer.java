package br.com.kitchen.ordermonitoring.app.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public abstract class KafkaGenericConsumer<T> {

    private final Class<T> type;
    private final Consumer<T> messageHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected KafkaGenericConsumer(Class<T> type, Consumer<T> messageHandler) {
        this.type = type;
        this.messageHandler = messageHandler;
    }

    public void consume(String rawMessage) {
        try {
            T message = objectMapper.readValue(rawMessage, type);
            if (isValid(message)) {
                log.info("New order received: {}",rawMessage);
                messageHandler.accept(message);
            } else {
                log.error("Invalid message received: {}", rawMessage);
            }
        } catch (Exception e) {
            log.error("Failed to parse message: {}", rawMessage);
        }
    }

    protected boolean isValid(T message) {
        return true;
    }
}
