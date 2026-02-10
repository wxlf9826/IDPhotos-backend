package org.xuanfeng.idphotosbackend.service;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {

    /**
     * 获取预签名链接
     *
     * @param key key
     * @return 预签名链接
     */
    String getPresignedUrl(String key);

    /**
     * 上传文件并返回 Key
     *
     * @param inputStream
     * @param key
     * @param contentType
     * @return
     */
    String uploadFile(InputStream inputStream, String key, String contentType);
}
