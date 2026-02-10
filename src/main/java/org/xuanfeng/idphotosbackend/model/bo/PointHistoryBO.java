package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryBO {

    /**
     * 时间 如：2025-12-25 11:12:12
     */
    private String time;

    /**
     * 变动类型
     */
    private String changeType;

    /**
     * 变动分数
     */
    private Integer changeAmount;

    /**
     * 最新分数
     */
    private Integer totalPoints;

}
