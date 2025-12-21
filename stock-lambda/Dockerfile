FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM public.ecr.aws/lambda/java:17
WORKDIR /app
COPY --from=build /app/target/stock-lambda-1.0-SNAPSHOT.jar stock-lambda.jar
CMD ["br.com.kitchen.lambda.StockLambdaHandler::handleRequest"]
