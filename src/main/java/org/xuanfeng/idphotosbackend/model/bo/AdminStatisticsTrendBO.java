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
public class AdminStatisticsTrendBO {

    /**
     * 日期列表（yyyy-MM-dd）
     */
    private List<String> dateList;

    /**
     * 每日制作量
     */
    private List<Long> makeCountList;

    /**
     * 每日注册量
     */
    private List<Long> registerCountList;
}
