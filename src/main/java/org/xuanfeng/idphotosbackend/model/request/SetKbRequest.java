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
public class SetKbRequest {
    private MultipartFile inputImage;

    private String inputImageBase64;

    private Integer dpi; // 默认 300

    private Integer kb;  // 默认 50

}