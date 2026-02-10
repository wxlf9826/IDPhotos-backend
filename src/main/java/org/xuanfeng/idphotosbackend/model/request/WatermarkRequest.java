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
public class WatermarkRequest {
    private MultipartFile inputImage;
    private String inputImageBase64;
    private String text;
    private Integer size;
    private Float opacity;
    private Integer angle;
    private String color;
    private Integer space;
    private Integer kb;
    private Integer dpi;
}