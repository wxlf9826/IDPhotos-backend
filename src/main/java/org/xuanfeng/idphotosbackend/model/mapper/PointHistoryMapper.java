package org.xuanfeng.idphotosbackend.model.mapper;

import org.apache.ibatis.annotations.Select;
import org.xuanfeng.idphotosbackend.model.bo.PointTypeAggBO;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PointHistoryMapper extends Mapper<PointHistory> {

    /**
     * 按积分类型聚合（全量有效数据）
     *
     * @return 聚合结果
     */
    @Select("SELECT type, COALESCE(SUM(ABS(change_amount)), 0) AS amount FROM point_history WHERE state = 1 GROUP BY type")
    List<PointTypeAggBO> sumAbsChangeAmountGroupByType();
}
