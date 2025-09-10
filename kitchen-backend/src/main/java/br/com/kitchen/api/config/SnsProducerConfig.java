package br.com.kitchen.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsProducerConfig {

    @Value("${spring.cloud.aws.sns.endpoint}")
    String snsUri;

    @Value("${spring.cloud.aws.credentials.access-key}")
    String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    String secretAccessKeyId;

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKeyId))
                )
                .region(Region.US_EAST_1)
                .endpointOverride(java.net.URI.create(snsUri))
                .build();
    }
}
