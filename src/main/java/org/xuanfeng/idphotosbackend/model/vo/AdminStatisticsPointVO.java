package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsPointVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 数值
     */
    private Long value;
}
