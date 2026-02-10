package org.xuanfeng.idphotosbackend.service;

import com.github.pagehelper.PageInfo;
import org.xuanfeng.idphotosbackend.model.bo.PointTypeAggBO;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.request.AdminPointHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;

import java.util.Date;
import java.util.List;

public interface PointHistoryService {

    /**
     * 获取用户积分历史
     *
     * @param request 查询request
     * @return 积分历史记录列表
     */
    PageInfo<PointHistory> getPageByUserId(HistoryByPageRequest request);

    /**
     * 管理端分页查询积分历史（支持按用户、类型筛选）
     *
     * @param request 查询参数
     * @return 分页结果
     */
    PageInfo<PointHistory> getPageByCondition(AdminPointHistoryByPageRequest request);

    /**
     * 插入积分记录
     *
     * @param pointHistory 积分记录
     * @return 数量
     */
    Integer insert(PointHistory pointHistory);

    /**
     * 查询指定时间区间内积分流水
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 积分流水
     */
    List<PointHistory> listByCreateTimeRange(Date startTime, Date endTime);

    /**
     * 查询全部有效积分流水
     *
     * @return 积分流水
     */
    List<PointHistory> listAllValid();

    /**
     * 按类型聚合积分变动（绝对值求和）
     *
     * @return 聚合结果
     */
    List<PointTypeAggBO> sumAbsChangeAmountGroupByType();
}
