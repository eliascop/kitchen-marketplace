FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM public.ecr.aws/lambda/java:17
WORKDIR /app
COPY --from=build /app/target/product-lambda-1.0-SNAPSHOT.jar product-lambda.jar
CMD ["br.com.kitchen.indexation.ProductIndexationLambda::handleRequest"]