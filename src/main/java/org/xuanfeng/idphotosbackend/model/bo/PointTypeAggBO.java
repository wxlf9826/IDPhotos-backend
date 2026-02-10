package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointTypeAggBO {

    /**
     * 积分类型
     */
    private String type;

    /**
     * 聚合值（绝对值求和）
     */
    private Long amount;
}
