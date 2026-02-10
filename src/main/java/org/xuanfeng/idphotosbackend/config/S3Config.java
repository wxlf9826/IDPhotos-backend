package org.xuanfeng.idphotosbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${s3.endpoint}")
    private String endpoint; // MinIO为ip:9000, 阿里云为oss-cn-hangzhou.aliyuncs.com

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.region:us-east-1}") // MinIO通常随便填，云厂商需对应
    private String region;

    @Bean
    public S3Client s3Client() {
        // 创建 S3 特定的配置对象
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true) // 开启路径风格访问
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .serviceConfiguration(s3Configuration) // 将配置注入
                .build();
    }

    // 负责生成预签名链接
    @Bean
    public S3Presigner s3Presigner() {
        // 创建 S3 特定的配置对象
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true) // 开启路径风格访问
                .build();

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .serviceConfiguration(s3Configuration)
                .build();
    }
}