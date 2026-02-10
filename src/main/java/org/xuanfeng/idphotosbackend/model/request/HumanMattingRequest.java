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
public class HumanMattingRequest {

    private MultipartFile inputImage;

    private String inputImageBase64;

    private String humanMattingModel; // 默认 hivision_modnet

    private Integer dpi; // 默认 300

}