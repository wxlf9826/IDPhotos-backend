package org.xuanfeng.idphotosbackend.proxy;

import org.xuanfeng.idphotosbackend.model.dto.SightengineCheckDTO;
import org.xuanfeng.idphotosbackend.model.request.ImageSecurityCheckRequest;

public interface SightengineProxy {

    /**
     * 图片安全检测
     *
     * @param request 请求
     * @return 检测结果DTO
     */
    SightengineCheckDTO imageSecurityCheck(ImageSecurityCheckRequest request);
}
