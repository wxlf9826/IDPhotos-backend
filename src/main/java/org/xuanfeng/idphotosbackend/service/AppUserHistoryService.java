package org.xuanfeng.idphotosbackend.service;

import com.github.pagehelper.PageInfo;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.request.AdminUserHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;

import java.util.Date;
import java.util.List;

public interface AppUserHistoryService {

    /**
     * 获取用户的历史
     *
     * @param request 查询request
     * @return 历史记录列表
     */
    PageInfo<AppUserHistory> getPageByUserId(HistoryByPageRequest request);

    /**
     * 管理端按条件分页查询制作历史
     *
     * @param request 查询请求
     * @return 分页数据
     */
    PageInfo<AppUserHistory> getPageByCondition(AdminUserHistoryByPageRequest request);

    /**
     * 插入新的记录
     *
     * @param appUserHistory 记录
     * @return 条数
     */
    Integer insert(AppUserHistory appUserHistory);

    /**
     * 删除特定日期之前的数据
     *
     * @param date           时间参数
     * @param appUserHistory 删除条件和参数
     * @return 删除数量
     */
    Integer deleteByLessTime(Date date, AppUserHistory appUserHistory);

    /**
     * 根据id删除
     *
     * @param appUserHistory 历史记录
     * @return 删除数量
     */
    Integer deleteById(AppUserHistory appUserHistory);

    /**
     * 统计有效制作总数
     *
     * @return 制作总数
     */
    Long countTotalValidHistory();

    /**
     * 统计指定时间区间内制作数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 制作数量
     */
    Long countByCreateTimeRange(Date startTime, Date endTime);

    /**
     * 查询指定时间区间内制作记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 制作记录
     */
    List<AppUserHistory> listByCreateTimeRange(Date startTime, Date endTime);
}
