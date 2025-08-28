#!/bin/bash
echo ">> Criando filas SQS no LocalStack..."
awslocal sqs create-queue --queue-name order-events
awslocal sqs create-queue --queue-name payment-events
echo ">> Filas criadas com sucesso!"

awslocal sns create-topic --name order-topic
echo ">> Tópico SNS criado com sucesso!"

TOPIC_ARN=$(awslocal sns list-topics --query "Topics[0].TopicArn" --output text)
QUEUE_ARN=$(awslocal sqs get-queue-attributes --queue-url http://localhost:4566/000000000000/order-events --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

awslocal sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $QUEUE_ARN

echo "TOPIC_ARN: $TOPIC_ARN"
echo "QUEUE_ARN: $QUEUE_ARN"
