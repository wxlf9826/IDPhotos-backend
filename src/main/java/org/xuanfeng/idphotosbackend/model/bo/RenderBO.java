package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RenderBO {

    /**
     * 渲染类型
     */
    private String type;

    /**
     * 渲染序号
     */
    private Integer number;
}