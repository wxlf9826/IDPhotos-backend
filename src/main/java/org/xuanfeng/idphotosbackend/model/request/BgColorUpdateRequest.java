package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BgColorUpdateRequest {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 颜色值（十六进制）
     */
    private String colorValue;
}