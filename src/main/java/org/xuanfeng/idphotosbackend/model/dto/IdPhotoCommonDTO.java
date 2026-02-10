package org.xuanfeng.idphotosbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdPhotoCommonDTO {

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 错误时有
     */
    private String error;

    /**
     * 图片base64
     */
    @JsonProperty("image_base64")
    private String imageBase64;

    /**
     * 标准图片base64
     */
    @JsonProperty("image_base64_standard")
    private String imageBase64Standard;

    /**
     * 高清图片base64
     */
    @JsonProperty("image_base64_hd")
    private String imageBase64Hd;
}
