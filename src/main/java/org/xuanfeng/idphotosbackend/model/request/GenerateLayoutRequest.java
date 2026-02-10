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
public class GenerateLayoutRequest {

    private MultipartFile inputImage;

    private String inputImageBase64;

    private Integer height;

    private Integer width;

    private Integer kb;

    private Integer dpi;

}