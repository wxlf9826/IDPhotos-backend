package org.xuanfeng.idphotosbackend.proxy;

import org.xuanfeng.idphotosbackend.model.dto.OpenIdDTO;
import org.xuanfeng.idphotosbackend.model.request.OpenIdRequest;

public interface WxProxy {

    /**
     * 获取微信用户的唯一标识 OpenID ,关联自己的业务
     * @param openIdRequest request请求
     * @return openId信息
     */
    OpenIdDTO getOpenId(OpenIdRequest openIdRequest);
}
