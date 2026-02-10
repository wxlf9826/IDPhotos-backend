package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsSummaryBO {

    /**
     * 今日新增用户数
     */
    private Long todayNewUserCount;

    /**
     * 累计用户数
     */
    private Long totalUserCount;

    /**
     * 今日制作量
     */
    private Long todayMakeCount;

    /**
     * 累计制作量
     */
    private Long totalMakeCount;

    /**
     * 今日积分消耗
     */
    private Long todayPointConsume;

    /**
     * 人均制作数
     */
    private Double avgMakePerUser;
}
