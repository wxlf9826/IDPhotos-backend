package org.xuanfeng.idphotosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBackgroundRequest {
    private MultipartFile inputImage;

    private String inputImageBase64;

    private String color; // 十六进制

    private Integer kb;

    private Integer dpi;

    private Integer render; // 0, 1, 2
}