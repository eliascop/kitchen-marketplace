package br.com.kitchen.notification.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

@Configuration
public class SnsClientConfig {

    @Value("${spring.cloud.aws.sns.endpoint}")
    private String snsUri;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretAccessKeyId;

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKeyId)
                        )
                )
                .region(Region.SA_EAST_1)
                .endpointOverride(URI.create(snsUri))
                .build();
    }
}