package br.com.kitchen.api.producer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqsProducer<T> {

    private static final Logger logger = LoggerFactory.getLogger(SqsProducer.class);

    private final SqsTemplate sqsTemplate;
    private final String queueName;

    public SqsProducer(SqsTemplate sqsTemplate, String queueName) {
        this.sqsTemplate = sqsTemplate;
        this.queueName = queueName;
    }

    public void sendNotification(T data) {
        logger.info("Enviando mensagem para fila [{}]: {}", queueName, data);
        sqsTemplate.send(queueName, data);
    }
}
