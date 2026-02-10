package org.xuanfeng.idphotosbackend.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PicSizeUpdateRequest {
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
     * 像素高度
     */
    private Integer heightPx;

    /**
     * 像素宽度
     */
    private Integer widthPx;
}
