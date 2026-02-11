package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BgColorCreateRequest {

    /**
     * 名称
     */
    @NotNull(message = "名称不能为空")
    private String name;

    /**
     * 颜色值（十六进制）
     */
    @NotNull(message = "颜色值不能为空")
    private String colorValue;
}