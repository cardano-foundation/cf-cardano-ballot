package org.cardano.foundation.voting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AmazonSNSConfig {

    @Value("${aws.sns.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.sns.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.sns.region}")
    private String snsRegion;

    @Bean
    public SnsClient amazonSNSClient() {
        return SnsClient.builder()
                .region(Region.of(snsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

}
