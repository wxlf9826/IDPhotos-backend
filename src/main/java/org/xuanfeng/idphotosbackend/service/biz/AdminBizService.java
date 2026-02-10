package org.xuanfeng.idphotosbackend.service.biz;

import com.github.pagehelper.PageInfo;
import org.xuanfeng.idphotosbackend.model.bo.AdminPicSizeBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminPointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsDistributionBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsSummaryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsTrendBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserBO;
import org.xuanfeng.idphotosbackend.model.bo.SystemConfigBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.request.*;

import java.util.List;

public interface AdminBizService {

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录结果
     */
    WxLoginBO login(AdminLoginRequest request);

    /**
     * 获取所有尺寸
     *
     * @return 尺寸列表
     */
    List<AdminPicSizeBO> getPicSizeList();

    /**
     * 添加尺寸
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean addPicSize(PicSizeCreateRequest request);

    /**
     * 更新尺寸
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean updatePicSize(PicSizeUpdateRequest request);

    /**
     * 删除尺寸
     *
     * @param id 尺寸ID
     * @return 是否成功
     */
    boolean deletePicSize(Long id);

    /**
     * 获取所有系统配置
     *
     * @return 系统配置列表
     */
    List<SystemConfigBO> getSystemConfigList();

    /**
     * 添加系统配置
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean addSystemConfig(SystemConfigCreateRequest request);

    /**
     * 更新系统配置
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean updateSystemConfig(SystemConfigUpdateRequest request);

    /**
     * 删除系统配置
     *
     * @param id 系统配置ID
     * @return 是否成功
     */
    boolean deleteSystemConfig(Long id);

    /**
     * 分页查询用户列表
     *
     * @param request 请求参数
     * @return 分页结果
     */
    PageInfo<AdminUserBO> getUserPage(AdminUserByPageRequest request);

    /**
     * 给用户赠送积分
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean rewardUserPoints(AdminUserRewardPointsRequest request);

    /**
     * 更新用户状态（封禁/解封）
     *
     * @param request 请求参数
     * @return 是否成功
     */
    boolean updateUserStatus(AdminUserStatusUpdateRequest request);

    /**
     * 管理端分页查询制作历史
     *
     * @param request 请求参数
     * @return 分页结果
     */
    PageInfo<AdminUserHistoryBO> getUserHistoryPage(AdminUserHistoryByPageRequest request);

    /**
     * 管理端分页查询积分历史（支持按用户、类型筛选）
     *
     * @param request 查询参数
     * @return 分页结果
     */
    PageInfo<AdminPointHistoryBO> getPointHistoryPage(AdminPointHistoryByPageRequest request);

    /**
     * 统计总览
     *
     * @return 总览数据
     */
    AdminStatisticsSummaryBO getStatisticsSummary();

    /**
     * 近7日趋势统计
     *
     * @return 趋势数据
     */
    AdminStatisticsTrendBO getStatisticsTrend();

    /**
     * 近7日分布统计
     *
     * @return 分布数据
     */
    AdminStatisticsDistributionBO getStatisticsDistribution();
}
