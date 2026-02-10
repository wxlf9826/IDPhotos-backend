package org.xuanfeng.idphotosbackend.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsDistributionBO {

    /**
     * 积分变动饼图
     */
    private List<AdminStatisticsPointBO> pointChangePie;
}
