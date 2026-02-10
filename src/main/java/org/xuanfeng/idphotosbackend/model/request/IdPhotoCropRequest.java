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
public class IdPhotoCropRequest {

    private MultipartFile inputImage;

    private String inputImageBase64;

    private Integer height; // 默认 413

    private Integer width;  // 默认 295

    private String faceDetectModel; // 默认 "mtcnn"

    private Boolean hd; // 默认 true

    private Integer dpi; // 默认 300

    private Float headMeasureRatio; // 默认 0.2

    private Float headHeightRatio;  // 默认 0.45

    private Float topDistanceMax;   // 默认 0.12

    private Float topDistanceMin;   // 默认 0.10

}