package br.com.kitchen.lambda.factory;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class DynamoDbClientFactory {

    private DynamoDbClientFactory() {}

    public static DynamoDbClient create() {
        String lsHost = System.getenv()
                .getOrDefault("LOCALSTACK_HOSTNAME", "localstack");

        URI endpoint = URI.create("http://" + lsHost + ":4566");

        return DynamoDbClient.builder()
                .endpointOverride(endpoint)
                .region(Region.SA_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();
    }
}