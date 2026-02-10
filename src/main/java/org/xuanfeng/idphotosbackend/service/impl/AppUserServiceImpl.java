package org.xuanfeng.idphotosbackend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.mapper.AppUserMapper;
import org.xuanfeng.idphotosbackend.model.po.AppUser;
import org.xuanfeng.idphotosbackend.model.request.AdminUserByPageRequest;
import org.xuanfeng.idphotosbackend.service.AppUserService;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

    @Resource
    private AppUserMapper appUserMapper;

    @Override
    public AppUser getUserByOpenId(String openId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(openId), "openId can not be blank");

        Weekend<AppUser> weekend = new Weekend<>(AppUser.class);
        WeekendCriteria<AppUser, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUser::getState, StateEnum.VALID.getCode());
        criteria.andEqualTo(AppUser::getOpenId, openId);

        return appUserMapper.selectOneByExample(weekend);
    }

    @Override
    public Integer insert(AppUser appUser) {
        Preconditions.checkArgument(appUser != null, "appUser can not be null");
        Preconditions.checkArgument(StringUtils.isNotBlank(appUser.getOpenId()), "appUser.openId can not be blank");

        return appUserMapper.insertSelective(appUser);
    }

    @Override
    public Integer updateById(AppUser appUser) {
        Preconditions.checkArgument(appUser != null, "appUser can not be null");
        Preconditions.checkArgument(appUser.getId() != null, "appUser.id can not be null");

        return appUserMapper.updateByPrimaryKeySelective(appUser);
    }

    @Override
    public AppUser getById(Long userId) {
        Preconditions.checkArgument(userId != null, "userId can not be null");

        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<AppUser> getByIdList(List<Long> userIdList) {
        Preconditions.checkArgument(userIdList != null, "userIdList can not be null");
        if (userIdList.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        Weekend<AppUser> weekend = new Weekend<>(AppUser.class);
        WeekendCriteria<AppUser, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUser::getState, StateEnum.VALID.getCode());
        criteria.andIn(AppUser::getId, userIdList);
        return appUserMapper.selectByExample(weekend);
    }

    @Override
    public PageInfo<AppUser> getPage(AdminUserByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getPageNum() != null, "request.pageNum can not be null");
        Preconditions.checkArgument(request.getPageSize() != null, "request.pageSize can not be null");

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.isNeedCount());

        Weekend<AppUser> weekend = new Weekend<>(AppUser.class);
        WeekendCriteria<AppUser, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUser::getState, StateEnum.VALID.getCode());

        if (StringUtils.isNotBlank(request.getNickName())) {
            criteria.andLike(AppUser::getNickName, "%" + request.getNickName().trim() + "%");
        }

        if (request.getStatus() != null) {
            criteria.andEqualTo(AppUser::getStatus, request.getStatus());
        }

        // 默认降序
        String sortField = StringUtils.equalsIgnoreCase(request.getSortField(), "points") ? "points" : "id";
        if (OrderEnum.ASC.equals(request.getOrderEnum())) {
            weekend.orderBy(sortField).asc();
        } else {
            weekend.orderBy(sortField).desc();
        }

        List<AppUser> list = appUserMapper.selectByExample(weekend);
        return new PageInfo<>(list);
    }

    @Override
    public Long countTotalValidUser() {
        Weekend<AppUser> weekend = new Weekend<>(AppUser.class);
        WeekendCriteria<AppUser, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUser::getState, StateEnum.VALID.getCode());
        return (long) appUserMapper.selectCountByExample(weekend);
    }

    @Override
    public Long countByCreateTimeRange(Date startTime, Date endTime) {
        Preconditions.checkArgument(startTime != null, "startTime can not be null");
        Preconditions.checkArgument(endTime != null, "endTime can not be null");

        Weekend<AppUser> weekend = new Weekend<>(AppUser.class);
        WeekendCriteria<AppUser, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUser::getState, StateEnum.VALID.getCode());
        criteria.andGreaterThanOrEqualTo(AppUser::getCreateTime, startTime);
        criteria.andLessThanOrEqualTo(AppUser::getCreateTime, endTime);
        return (long) appUserMapper.selectCountByExample(weekend);
    }
}
