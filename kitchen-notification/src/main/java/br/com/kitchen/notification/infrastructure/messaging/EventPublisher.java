package br.com.kitchen.notification.infrastructure.messaging;

public interface EventPublisher {
    <T> void publish(String destination, T payload);
}