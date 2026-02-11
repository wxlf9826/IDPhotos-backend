package org.xuanfeng.idphotosbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BgColorVO implements Serializable {

    /**
     * id
     */
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