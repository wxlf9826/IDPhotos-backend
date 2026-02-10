package org.xuanfeng.idphotosbackend.service;

import com.github.pagehelper.PageInfo;
import org.xuanfeng.idphotosbackend.model.request.AdminUserByPageRequest;
import org.xuanfeng.idphotosbackend.model.po.AppUser;

import java.util.Date;
import java.util.List;

public interface AppUserService {

    /**
     * 根据openId获取用户
     *
     * @param openId openId
     * @return 用户
     */
    AppUser getUserByOpenId(String openId);

    /**
     * 插入新用户
     *
     * @param appUser 用户
     * @return 插入的数量
     */
    Integer insert(AppUser appUser);

    /**
     * 根据id更新user
     *
     * @param appUser appUser
     * @return 更新数量
     */
    Integer updateById(AppUser appUser);

    /**
     * 根据id获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    AppUser getById(Long userId);

    /**
     * 批量根据id获取用户信息
     *
     * @param userIdList 用户id列表
     * @return 用户列表
     */
    List<AppUser> getByIdList(List<Long> userIdList);

    /**
     * 分页查询用户列表
     *
     * @param request 请求参数
     * @return 分页结果
     */
    PageInfo<AppUser> getPage(AdminUserByPageRequest request);

    /**
     * 统计有效用户总数
     *
     * @return 用户总数
     */
    Long countTotalValidUser();

    /**
     * 统计指定时间区间内新增用户数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户数
     */
    Long countByCreateTimeRange(Date startTime, Date endTime);
}
