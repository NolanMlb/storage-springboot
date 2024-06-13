package com.nextu.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

    @Value("${s3.access_key}")
    private String awsAccessKey;

    @Value("${s3.secret_access_key}")
    private String awsSecretKey;

    @Value("${s3.region}")
    private String awsRegion;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(awsRegion))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}