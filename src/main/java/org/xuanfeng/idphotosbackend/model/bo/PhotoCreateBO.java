package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoCreateBO {

    /**
     * 生成好的图片key
     */
    private String imageKey;

    /**
     * 生成好的图片url
     */
    private String imageUrl;
}