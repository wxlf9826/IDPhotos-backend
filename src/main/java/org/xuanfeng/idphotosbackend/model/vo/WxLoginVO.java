package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxLoginVO {

    /**
     * token
     */
    private String token;

    /**
     * wx的openid
     */
    private String openId;

    /**
     * 广告位id
     */
    private String adUnitId;
}