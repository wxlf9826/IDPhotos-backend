package org.xuanfeng.idphotosbackend.utils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * S3 存储迁移工具
 * <p>
 * 用于将一个 S3（或兼容 S3 协议的存储，如 MinIO、Backblaze B2 等）的所有文件迁移到另一个 S3 存储。
 * <p>
 * 使用方式：直接运行 main 方法，修改下方的源/目标配置即可。
 * <p>
 * 支持功能：
 * - 列出源 bucket 所有对象并逐个迁移
 * - 保留原始 key（路径）
 * - 支持指定 key 前缀过滤（只迁移部分文件）
 * - 迁移进度日志输出
 * - 失败文件记录，不中断整体迁移
 */
public class S3MigrationTool {

    // ==================== 源 S3 配置 ====================
    private static final String SOURCE_ENDPOINT = "https://xxx";
    private static final String SOURCE_ACCESS_KEY = "your-target-access-key";
    private static final String SOURCE_SECRET_KEY = "your-target-secret-key";
    private static final String SOURCE_REGION = "us-west-004";
    private static final String SOURCE_BUCKET = "xx";

    // ==================== 目标 S3 配置 ====================
    private static final String TARGET_ENDPOINT = "http://xxx";
    private static final String TARGET_ACCESS_KEY = "your-target-access-key";
    private static final String TARGET_SECRET_KEY = "your-target-secret-key";
    private static final String TARGET_REGION = "us-east-1";
    private static final String TARGET_BUCKET = "xx";

    // ==================== 迁移选项 ====================
    /**
     * 只迁移指定前缀的文件，为空则迁移全部
     */
    private static final String KEY_PREFIX = "";

    /**
     * 是否开启 dry-run 模式（只列出文件不实际迁移）
     */
    private static final boolean DRY_RUN = false;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       S3 存储迁移工具 启动");
        System.out.println("========================================");
        System.out.println("源: " + SOURCE_ENDPOINT + " / " + SOURCE_BUCKET);
        System.out.println("目标: " + TARGET_ENDPOINT + " / " + TARGET_BUCKET);
        System.out.println("前缀过滤: " + (KEY_PREFIX.isEmpty() ? "无（迁移全部）" : KEY_PREFIX));
        System.out.println("Dry-Run: " + DRY_RUN);
        System.out.println("========================================");

        S3Client sourceClient = buildS3Client(SOURCE_ENDPOINT, SOURCE_ACCESS_KEY, SOURCE_SECRET_KEY, SOURCE_REGION);
        S3Client targetClient = buildS3Client(TARGET_ENDPOINT, TARGET_ACCESS_KEY, TARGET_SECRET_KEY, TARGET_REGION);

        try {
            migrate(sourceClient, targetClient);
        } finally {
            sourceClient.close();
            targetClient.close();
        }

        System.out.println("========================================");
        System.out.println("       S3 存储迁移工具 完成");
        System.out.println("========================================");
    }

    private static S3Client buildS3Client(String endpoint, String accessKey, String secretKey, String region) {
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .serviceConfiguration(s3Configuration)
                .build();
    }

    private static void migrate(S3Client sourceClient, S3Client targetClient) {
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger skipCount = new AtomicInteger(0);

        String continuationToken = null;
        boolean hasMore = true;

        while (hasMore) {
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(SOURCE_BUCKET)
                    .maxKeys(1000);

            if (!KEY_PREFIX.isEmpty()) {
                requestBuilder.prefix(KEY_PREFIX);
            }
            if (continuationToken != null) {
                requestBuilder.continuationToken(continuationToken);
            }

            ListObjectsV2Response response = sourceClient.listObjectsV2(requestBuilder.build());

            for (S3Object s3Object : response.contents()) {
                totalCount.incrementAndGet();
                String key = s3Object.key();
                long size = s3Object.size();

                // 跳过目录标记（size=0 且以 / 结尾）
                if (size == 0 && key.endsWith("/")) {
                    System.out.println("[跳过] 目录标记: " + key);
                    skipCount.incrementAndGet();
                    continue;
                }

                if (DRY_RUN) {
                    System.out.printf("[Dry-Run] 将迁移: %s (%.2f KB)%n", key, size / 1024.0);
                    successCount.incrementAndGet();
                    continue;
                }

                try {
                    migrateObject(sourceClient, targetClient, key, size);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.err.printf("[失败] %s, 错误: %s%n", key, e.getMessage());
                }
            }

            hasMore = response.isTruncated();
            continuationToken = response.nextContinuationToken();
        }

        System.out.println();
        System.out.println("========== 迁移统计 ==========");
        System.out.println("总文件数: " + totalCount.get());
        System.out.println("成功: " + successCount.get());
        System.out.println("失败: " + failCount.get());
        System.out.println("跳过: " + skipCount.get());
    }

    private static void migrateObject(S3Client sourceClient, S3Client targetClient, String key, long size) {
        System.out.printf("[迁移中] %s (%.2f KB) ... ", key, size / 1024.0);

        // 1. 先获取源对象的元数据（content-type等）
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(SOURCE_BUCKET)
                .key(key)
                .build();
        HeadObjectResponse headResponse = sourceClient.headObject(headRequest);
        String contentType = headResponse.contentType();

        // 2. 从源下载
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(SOURCE_BUCKET)
                .key(key)
                .build();

        try (InputStream inputStream = sourceClient.getObject(getRequest)) {
            // 3. 上传到目标
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(TARGET_BUCKET)
                    .key(key)
                    .contentType(contentType)
                    .build();

            targetClient.putObject(putRequest, RequestBody.fromInputStream(inputStream, size));
            System.out.println("成功");
        } catch (Exception e) {
            throw new RuntimeException("迁移文件失败: " + key, e);
        }
    }
}
