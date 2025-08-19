#!/bin/bash
echo ">> Criando filas SQS no LocalStack..."
awslocal sqs create-queue --queue-name order-events
awslocal sqs create-queue --queue-name payment-events
echo ">> Filas criadas com sucesso!"

