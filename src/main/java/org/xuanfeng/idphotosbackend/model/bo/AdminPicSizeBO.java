package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPicSizeBO {

    /**
     * id
     */
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

    /**
     * 逻辑删除标志 0-无效，1-有效
     */
    private Integer state;

    /**
     * 创建时间
     */
    private String createTime;
}
