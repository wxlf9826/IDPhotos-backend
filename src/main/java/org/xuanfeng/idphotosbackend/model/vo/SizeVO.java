package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SizeVO {

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