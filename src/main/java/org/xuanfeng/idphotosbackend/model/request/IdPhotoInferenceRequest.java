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
public class IdPhotoInferenceRequest {

    // input_image: UploadFile
    private MultipartFile inputImage;

    // input_image_base64: str
    private String inputImageBase64;

    // 尺寸参数
    private Integer height; // 默认 413
    private Integer width;  // 默认 295

    // 模型选择
    private String humanMattingModel; // 默认 "modnet_photographic_portrait_matting"
    private String faceDetectModel;    // 默认 "mtcnn"

    // 功能开关与设置
    private Boolean hd;     // 默认 true
    private Integer dpi;    // 默认 300
    private Boolean faceAlign; // 默认 false

    // 图像增强 (美颜/调整)
    private Integer whiteningStrength;   // 默认 0
    private Float brightnessStrength;    // 默认 0
    private Float contrastStrength;      // 默认 0
    private Float sharpenStrength;       // 默认 0
    private Float saturationStrength;    // 默认 0

    // 证件照布局比例控制
    private Float headMeasureRatio;  // 默认 0.2
    private Float headHeightRatio;   // 默认 0.45
    private Float topDistanceMax;    // 默认 0.12
    private Float topDistanceMin;    // 默认 0.10
}