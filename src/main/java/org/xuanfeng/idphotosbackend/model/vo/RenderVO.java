package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenderVO {

    /**
     * 渲染类型
     */
    private String type;

    /**
     * 渲染序号
     */
    private Integer number;
}