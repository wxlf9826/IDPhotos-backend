package org.xuanfeng.idphotosbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.service.S3Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    @Resource
    private S3Client s3Client;

    @Resource
    private S3Presigner s3Presigner;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Override
    public String getPresignedUrl(String key) {
        // 创建获取对象的请求
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // 创建预签名请求，设置有效期为 60 分钟
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        // 生成链接
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    @Override
    public String uploadFile(InputStream inputStream, String key, String contentType){
        try {
            // 1. 创建上传请求
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType) // 必须设置，否则S3默认识别为二进制流
                    .build();

            // 2. 执行上传 (使用 available() 获取流长度，v2 SDK 必须知道长度或封装流)
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            log.info("文件上传成功: {}", key);
            return key;
        } catch (IOException ioException){
            log.error("S3上传io异常:", ioException);
            throw new CommonException(ResultCodeEnum.S3_ERROR, "S3上传io异常");
        } catch (Exception e) {
            log.error("S3上传异常: ", e);
            throw new CommonException(ResultCodeEnum.S3_ERROR);
        }
    }
}
