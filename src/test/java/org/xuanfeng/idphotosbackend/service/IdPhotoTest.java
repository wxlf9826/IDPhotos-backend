package org.xuanfeng.idphotosbackend.service;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.model.dto.IdPhotoCommonDTO;
import org.xuanfeng.idphotosbackend.model.request.IdPhotoInferenceRequest;
import org.xuanfeng.idphotosbackend.proxy.PhotoProxy;

import javax.annotation.Resource;
import java.io.*;

@SpringBootTest
@ActiveProfiles("test")
public class IdPhotoTest {

    @Resource
    private PhotoProxy photoProxy;

    @Test
    void testIdPhotoInference() throws IOException {

        // 1. 指定本地文件路径
        File file = new File("/Users/liufeng/Documents/people.jpeg");

        // 2. 转为 MultipartFile
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                input
        );

        IdPhotoCommonDTO idPhotoCommonDTO = photoProxy.idPhotoInference(IdPhotoInferenceRequest
                .builder()
                .inputImage(multipartFile)
                .build());
        System.out.println(JSON.toJSONString(idPhotoCommonDTO.getImageBase64Hd()));
    }
}
