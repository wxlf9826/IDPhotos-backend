package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageSecurityCheckVO {

    /**
     * 上传后的图片key
     */
    private String imageKey;

    /**
     * 上传后的图片url
     */
    private String imageUrl;

    /**
     * 安全检测是否通过
     */
    private Boolean passed;

    /**
     * 不通过的原因
     */
    private String rejectReason;
}
