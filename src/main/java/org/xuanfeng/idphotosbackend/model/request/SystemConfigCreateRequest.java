package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SystemConfigCreateRequest {
    /**
     * 配置名称
     */
    @NotNull(message = "配置键不能为空")
    private String configKey;

    /**
     * 配置值
     */
    @NotNull(message = "配置值不能为空")
    private String configValue;

    /**
     * 配置说明
     */
    @NotNull(message = "配置说明不能为空")
    private String description;
}
