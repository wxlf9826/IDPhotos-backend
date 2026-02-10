package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PicSizeCreateRequest {
    /**
     * 名称
     */
    @NotNull(message = "名称不能为空")
    private String name;

    /**
     * 像素高度
     */
    @NotNull(message = "高度不能为空")
    private Integer heightPx;

    /**
     * 像素宽度
     */
    @NotNull(message = "宽度不能为空")
    private Integer widthPx;
}
