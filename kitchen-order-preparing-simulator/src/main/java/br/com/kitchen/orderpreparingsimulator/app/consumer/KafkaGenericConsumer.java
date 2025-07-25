package br.com.kitchen.orderpreparingsimulator.app.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Consumer;

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
                System.out.println("New Order Received:"+rawMessage);
                messageHandler.accept(message);
            } else {
                System.err.println("Invalid message: " + rawMessage);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse message: " + rawMessage);
        }
    }

    protected boolean isValid(T message) {
        return true;
    }
}
