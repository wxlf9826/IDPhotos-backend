package org.xuanfeng.idphotosbackend.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenIdDTO {

    /**
     * 错误码
     */
    @JSONField(name = "errcode")
    private Long errCode;

    /**
     * 错误信息
     */
    @JSONField(name = "errmsg")
    private String errMsg;

    /**
     * 用户唯一标识
     */
    @JSONField(name = "openid")
    private String openId;

    /**
     * 会话密钥 session_key 是对用户数据进行 加密签名 的密钥
     */
    @JSONField(name = "session_key")
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回
     */
    @JSONField(name = "unionid")
    private String unionId;

}
