package org.xuanfeng.idphotosbackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsDistributionVO {

    /**
     * 积分变动饼图
     */
    private List<AdminStatisticsPointVO> pointChangePie;
}
