package org.xuanfeng.idphotosbackend.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class PhotoUtils {

    /**
     * 从base64中得到contentType和inputStream
     *
     * @param base64String base64
     * @return 结果
     */
    public static Pair<String, InputStream> base64ToInputStream(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("Base64 string must not be null or empty");
        }

        String contentType = "application/octet-stream"; // 默认类型
        String base64Data = base64String;

        // 1. 解析 Data URI 前缀 (例如: data:image/png;base64,xxxx)
        if (base64String.contains(",")) {
            String[] parts = base64String.split(",");
            String header = parts[0]; // data:image/png;base64
            base64Data = parts[1];    // 纯 base64 码

            // 提取 contentType: 截取 "data:" 到 ";" 之间的部分
            if (header.contains(":") && header.contains(";")) {
                contentType = header.substring(header.indexOf(":") + 1, header.indexOf(";"));
            }
        }

        // 2. 解码 Base64 字符串为字节数组
        // 技巧：.replace(" ", "+") 是为了防止 base64 在 URL 传输过程中加号变成空格
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data.replace(" ", "+"));

        // 3. 将字节数组转换为 InputStream
        InputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);

        // 4. 返回结果
        return Pair.of(contentType, byteArrayInputStream);
    }
}
