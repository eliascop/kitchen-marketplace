#!/bin/bash
set -e
set -x

echo ">> Criando filas SQS no LocalStack..."
awslocal sqs create-queue --queue-name order-lambda-queue
awslocal sqs create-queue --queue-name stock-lambda-queue
awslocal sqs create-queue --queue-name order-queue
awslocal sqs create-queue --queue-name stock-queue
awslocal sqs create-queue --queue-name payment-queue

QUEUE_URL_ORDER_LAMBDA=$(awslocal sqs get-queue-url --queue-name order-lambda-queue --query "QueueUrl" --output text)
QUEUE_URL_STOCK_LAMBDA=$(awslocal sqs get-queue-url --queue-name stock-lambda-queue --query "QueueUrl" --output text)
QUEUE_URL_ORDER=$(awslocal sqs get-queue-url --queue-name order-queue --query "QueueUrl" --output text)
QUEUE_URL_STOCK=$(awslocal sqs get-queue-url --queue-name stock-queue --query "QueueUrl" --output text)

QUEUE_ARN_ORDER_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_STOCK_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_STOCK_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_ORDER=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_STOCK=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_STOCK  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

echo ">> Criando tópico SNS..."
TOPIC_ARN_ORDER=$(awslocal sns create-topic --name order-events --query "TopicArn" --output text)
TOPIC_ARN_STOCK=$(awslocal sns create-topic --name stock-events --query "TopicArn" --output text)

awslocal sns subscribe --topic-arn $TOPIC_ARN_ORDER --protocol sqs --notification-endpoint $QUEUE_ARN_ORDER_LAMBDA
awslocal sns subscribe --topic-arn $TOPIC_ARN_ORDER --protocol sqs --notification-endpoint $QUEUE_ARN_ORDER
awslocal sns subscribe --topic-arn $TOPIC_ARN_STOCK --protocol sqs --notification-endpoint $QUEUE_ARN_STOCK_LAMBDA
awslocal sns subscribe --topic-arn $TOPIC_ARN_STOCK --protocol sqs --notification-endpoint $QUEUE_ARN_STOCK

echo ">> Criando tabela DynamoDB: StockHistory"
awslocal dynamodb create-table \
  --table-name StockHistory \
  --attribute-definitions \
	AttributeName=id,AttributeType=N \
	AttributeName=createdAt,AttributeType=S \
  --key-schema \
	AttributeName=id,KeyType=HASH \
	AttributeName=createdAt,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST || true

echo ">> Criando tabela DynamoDB: Order"
awslocal dynamodb create-table \
  --table-name Order \
  --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST || true

echo ">> Criando Order-Lambda function..."
awslocal lambda create-function \
  --function-name order-lambda \
  --runtime java17 \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --handler br.com.kitchen.lambda.OrderLambdaHandler::handleRequest \
  --zip-file fileb:///tmp/order-lambda.jar

echo ">> Criando Stock-Lambda function..."
awslocal lambda create-function \
  --function-name stock-lambda \
  --runtime java17 \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --handler br.com.kitchen.lambda.StockLambdaHandler::handleRequest \
  --zip-file fileb:///tmp/stock-lambda.jar

echo ">> Criando Event Source Mapping Lambda"
awslocal lambda create-event-source-mapping \
  --function-name order-lambda \
  --batch-size 1 \
  --event-source-arn $QUEUE_ARN_ORDER_LAMBDA

awslocal lambda create-event-source-mapping \
  --function-name stock-lambda \
  --batch-size 1 \
  --event-source-arn $QUEUE_ARN_STOCK_LAMBDA

