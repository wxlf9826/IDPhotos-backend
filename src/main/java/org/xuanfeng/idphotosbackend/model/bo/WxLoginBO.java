package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxLoginBO {

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