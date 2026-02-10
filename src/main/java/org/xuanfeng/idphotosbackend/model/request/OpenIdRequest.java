package org.xuanfeng.idphotosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdRequest {

    /**
     * appid
     */
    private String appId;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 登录时获取的code
     */
    private String jsCode;

    /**
     * 授权类型
     */
    private String grantType;
}
