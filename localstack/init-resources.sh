#!/bin/bash
set -e
set -x

echo ">> Criando filas SQS no LocalStack..."
awslocal sqs create-queue --queue-name order-queue
awslocal sqs create-queue --queue-name order-lambda-queue
awslocal sqs create-queue --queue-name order-notification-queue

awslocal sqs create-queue --queue-name stock-queue
awslocal sqs create-queue --queue-name stock-lambda-queue

awslocal sqs create-queue --queue-name payment-queue
awslocal sqs create-queue --queue-name payment-lambda-queue

awslocal sqs create-queue --queue-name product-indexation-DLQ
awslocal sqs create-queue --queue-name product-indexation-queue
awslocal sqs create-queue --queue-name wallet-transaction-queue

QUEUE_URL_ORDER=$(awslocal sqs get-queue-url --queue-name order-queue --query "QueueUrl" --output text)
QUEUE_URL_ORDER_LAMBDA=$(awslocal sqs get-queue-url --queue-name order-lambda-queue --query "QueueUrl" --output text)
QUEUE_URL_ORDER_NOTIFICATION=$(awslocal sqs get-queue-url --queue-name order-notification-queue --query "QueueUrl" --output text)

QUEUE_URL_STOCK=$(awslocal sqs get-queue-url --queue-name stock-queue --query "QueueUrl" --output text)
QUEUE_URL_STOCK_LAMBDA=$(awslocal sqs get-queue-url --queue-name stock-lambda-queue --query "QueueUrl" --output text)

QUEUE_URL_PAYMENT=$(awslocal sqs get-queue-url --queue-name payment-queue --query "QueueUrl" --output text)
QUEUE_URL_PAYMENT_LAMBDA=$(awslocal sqs get-queue-url --queue-name payment-lambda-queue --query "QueueUrl" --output text)

QUEUE_URL_PRODUCT=$(awslocal sqs get-queue-url --queue-name product-indexation-queue --query "QueueUrl" --output text)
QUEUE_URL_PRODUCT_DLQ=$(awslocal sqs get-queue-url --queue-name product-indexation-DLQ --query "QueueUrl" --output text)
QUEUE_URL_WALLET_TRANSACTION=$(awslocal sqs get-queue-url --queue-name wallet-transaction-queue --query "QueueUrl" --output text)

QUEUE_ARN_ORDER=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_ORDER_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_ORDER_NOTIFICATION=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_ORDER_NOTIFICATION  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

QUEUE_ARN_STOCK=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_STOCK  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_STOCK_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_STOCK_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

QUEUE_ARN_PAYMENT=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_PAYMENT  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_PAYMENT_LAMBDA=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_PAYMENT_LAMBDA  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

QUEUE_ARN_PRODUCT=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_PRODUCT  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_PRODUCT_DLQ=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_PRODUCT_DLQ  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)
QUEUE_ARN_WALLET_TRANSACTION=$(awslocal sqs get-queue-attributes --queue-url $QUEUE_URL_WALLET_TRANSACTION  --attribute-names QueueArn --query "Attributes.QueueArn" --output text)

awslocal sqs set-queue-attributes --queue-url $QUEUE_URL_PRODUCT --attributes "{\"RedrivePolicy\": \"{\\\"deadLetterTargetArn\\\":\\\"$QUEUE_ARN_PRODUCT_DLQ\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}"

echo ">> Criando tópico SNS..."
TOPIC_ARN_ORDER=$(awslocal sns create-topic --name order-events --query "TopicArn" --output text)
TOPIC_ARN_STOCK=$(awslocal sns create-topic --name stock-events --query "TopicArn" --output text)
TOPIC_ARN_PAYMENT=$(awslocal sns create-topic --name payment-events --query "TopicArn" --output text)
TOPIC_ARN_PRODUCT=$(awslocal sns create-topic --name product-events --query "TopicArn" --output text)
TOPIC_ARN_WALLET_TRANSACTION=$(awslocal sns create-topic --name wallet-transaction-events --query "TopicArn" --output text)

awslocal sns subscribe --topic-arn $TOPIC_ARN_ORDER --protocol sqs --notification-endpoint $QUEUE_ARN_ORDER
awslocal sns subscribe --topic-arn $TOPIC_ARN_ORDER --protocol sqs --notification-endpoint $QUEUE_ARN_ORDER_LAMBDA
awslocal sns subscribe --topic-arn $TOPIC_ARN_ORDER --protocol sqs --notification-endpoint $QUEUE_ARN_ORDER_NOTIFICATION

awslocal sns subscribe --topic-arn $TOPIC_ARN_STOCK --protocol sqs --notification-endpoint $QUEUE_ARN_STOCK
awslocal sns subscribe --topic-arn $TOPIC_ARN_STOCK --protocol sqs --notification-endpoint $QUEUE_ARN_STOCK_LAMBDA

awslocal sns subscribe --topic-arn $TOPIC_ARN_PAYMENT --protocol sqs --notification-endpoint $QUEUE_ARN_PAYMENT
awslocal sns subscribe --topic-arn $TOPIC_ARN_PAYMENT --protocol sqs --notification-endpoint $QUEUE_ARN_PAYMENT_LAMBDA

awslocal sns subscribe --topic-arn $TOPIC_ARN_PRODUCT --protocol sqs --notification-endpoint $QUEUE_ARN_PRODUCT
awslocal sns subscribe --topic-arn $TOPIC_ARN_WALLET_TRANSACTION --protocol sqs --notification-endpoint $QUEUE_ARN_WALLET_TRANSACTION

echo ">> Criando tabela DynamoDB: StockHistory"
awslocal dynamodb create-table --table-name StockHistory --attribute-definitions AttributeName=id,AttributeType=N AttributeName=createdAt,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH AttributeName=createdAt,KeyType=RANGE --billing-mode PAY_PER_REQUEST || true

echo ">> Criando tabela DynamoDB: Order"
awslocal dynamodb create-table --table-name Order --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH --billing-mode PAY_PER_REQUEST || true

echo ">> Criando tabela DynamoDB: Payment"
awslocal dynamodb create-table --table-name PaymentHistory --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH --billing-mode PAY_PER_REQUEST || true

echo ">> Criando bucket para lambdas..."
awslocal s3 mb s3://lambda-artifacts || true

echo ">> Subindo artifacts das lambdas para o S3..."
awslocal s3 cp /artifacts/order-lambda.jar s3://lambda-artifacts/order-lambda.jar
awslocal s3 cp /artifacts/stock-lambda.jar s3://lambda-artifacts/stock-lambda.jar
awslocal s3 cp /artifacts/payment-lambda.jar s3://lambda-artifacts/payment-lambda.jar
awslocal s3 cp /artifacts/product-lambda.jar s3://lambda-artifacts/product-lambda.jar

echo ">> Criando Order-Lambda function (S3)..."
awslocal lambda create-function --function-name order-lambda --runtime java17 --role arn:aws:iam::000000000000:role/lambda-role --handler br.com.kitchen.lambda.OrderLambdaHandler::handleRequest \
  --code S3Bucket=lambda-artifacts,S3Key=order-lambda.jar || awslocal lambda update-function-code --function-name order-lambda --s3-bucket lambda-artifacts --s3-key order-lambda.jar

echo ">> Criando Stock-Lambda function (S3)..."
awslocal lambda create-function --function-name stock-lambda --runtime java17 --role arn:aws:iam::000000000000:role/lambda-role --handler br.com.kitchen.lambda.StockLambdaHandler::handleRequest \
  --code S3Bucket=lambda-artifacts,S3Key=stock-lambda.jar || awslocal lambda update-function-code --function-name stock-lambda --s3-bucket lambda-artifacts --s3-key stock-lambda.jar

echo ">> Criando Payment-Lambda function (S3)..."
awslocal lambda create-function --function-name payment-lambda --runtime java17 --role arn:aws:iam::000000000000:role/lambda-role --handler br.com.kitchen.lambda.PaymentLambdaHandler::handleRequest \
  --code S3Bucket=lambda-artifacts,S3Key=payment-lambda.jar || awslocal lambda update-function-code --function-name payment-lambda --s3-bucket lambda-artifacts --s3-key payment-lambda.jar

echo ">> Criando Product-Lambda function (S3)..."
awslocal lambda create-function --function-name product-lambda --runtime java17 --role arn:aws:iam::000000000000:role/lambda-role --handler br.com.kitchen.indexation.ProductIndexationLambda::handleRequest \
  --timeout 60 --memory-size 512 \
  --code S3Bucket=lambda-artifacts,S3Key=product-lambda.jar || awslocal lambda update-function-code --function-name product-lambda --s3-bucket lambda-artifacts --s3-key product-lambda.jar

echo ">> Settando para 5 execucoes de Product-Lambda em simultaneo"
awslocal lambda put-function-concurrency --function-name product-lambda --reserved-concurrent-executions 5

ensure_mapping() {
  local FUNCTION_NAME="$1"
  local EVENT_SOURCE_ARN="$2"

  local UUID
  UUID=$(awslocal lambda list-event-source-mappings \
    --function-name "$FUNCTION_NAME" \
    --query "EventSourceMappings[?EventSourceArn=='${EVENT_SOURCE_ARN}'].UUID" \
    --output text 2>/dev/null || true)

  if [ -n "$UUID" ] && [ "$UUID" != "None" ]; then
    echo ">> Removendo mapping existente ($UUID) de $FUNCTION_NAME"
    awslocal lambda delete-event-source-mapping --uuid "$UUID" || true
  fi

  echo ">> Criando mapping para $FUNCTION_NAME"
  awslocal lambda create-event-source-mapping \
    --function-name "$FUNCTION_NAME" \
    --batch-size 5 \
    --event-source-arn "$EVENT_SOURCE_ARN" \
    --maximum-batching-window-in-seconds 1
}

ensure_mapping "order-lambda" "$QUEUE_ARN_ORDER_LAMBDA"

ensure_mapping "product-lambda" "$QUEUE_ARN_PRODUCT"

ensure_mapping "stock-lambda" "$QUEUE_ARN_STOCK_LAMBDA"

ensure_mapping "payment-lambda" "$QUEUE_ARN_PAYMENT_LAMBDA"
