#!/bin/bash
set -e
set -x

echo ">> Criando filas SQS no LocalStack..."
awslocal sqs create-queue --queue-name lambda-queue
awslocal sqs create-queue --queue-name order-queue
awslocal sqs create-queue --queue-name payment-queue

echo ">> Criando tópico SNS..."
awslocal sns create-topic --name order-events

TOPIC_ARN=$(awslocal sns list-topics --query "Topics[0].TopicArn" --output text)

# --- Subscrição SNS -> order-queue (serviço consome)
QUEUE_URL_ORDER=$(awslocal sqs get-queue-url --queue-name order-queue --query "QueueUrl" --output text)
QUEUE_ARN_ORDER=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

awslocal sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $QUEUE_ARN_ORDER

# --- Subscrição SNS -> lambda-queue (lambda consome)
QUEUE_URL_LAMBDA=$(awslocal sqs get-queue-url --queue-name lambda-queue --query "QueueUrl" --output text)
QUEUE_ARN_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

awslocal sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $QUEUE_ARN_LAMBDA

echo ">> Criando tabela DynamoDB: Stock"
awslocal dynamodb create-table \
  --table-name Stock \
  --attribute-definitions AttributeName=productId,AttributeType=S \
  --key-schema AttributeName=productId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST || true

echo ">> Criando tabela DynamoDB: Order"
awslocal dynamodb create-table \
  --table-name Order \
  --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST || true

echo ">> Criando Lambda function..."
awslocal lambda create-function \
  --function-name order-lambda \
  --runtime java17 \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --handler br.com.kitchen.lambda.OrderLambdaHandler::handleRequest \
  --zip-file fileb:///tmp/order-lambda.jar

echo ">> Criando Event Source Mapping Lambda -> lambda-queue"
awslocal lambda create-event-source-mapping \
  --function-name order-lambda \
  --batch-size 1 \
  --event-source-arn $QUEUE_ARN_LAMBDA

echo ">> Recursos criados com sucesso:"
echo "TOPIC ARN: $TOPIC_ARN"
echo "ORDER QUEUE URL: $QUEUE_URL_ORDER"
echo "LAMBDA QUEUE URL: $QUEUE_URL_LAMBDA"
