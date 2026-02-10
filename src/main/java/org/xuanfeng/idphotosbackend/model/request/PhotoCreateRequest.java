package org.xuanfeng.idphotosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoCreateRequest {
    private Long userId;
    private Integer points;
    private String sizeName;
    private Integer widthPx;
    private Integer heightPx;
    private String bgColor;
    private String renderMode;
    private BeautyConfig beautyConfig;
    private WatermarkConfig watermarkConfig;
    private OtherConfig otherConfig;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeautyConfig {
        private Boolean enable;
        private Integer whitening;
        private Integer brightness;
        private Integer contrast;
        private Integer saturation;
        private Integer sharpening;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WatermarkConfig {
        private Boolean enable;
        private String text;
        private String color;
        private Integer size;
        private Double opacity;
        private Integer angle;
        private Integer spacing;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OtherConfig {
        private Boolean enable;
        private Double faceRatio;
        private Double topDistance;
        private Boolean kbEnable;
        private Integer kbSize;
        private Boolean dpiEnable;
        private Integer dpiValue;
    }

}
