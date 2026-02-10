package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SystemConfigUpdateRequest {
    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 配置名称
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置说明
     */
    private String description;
}
