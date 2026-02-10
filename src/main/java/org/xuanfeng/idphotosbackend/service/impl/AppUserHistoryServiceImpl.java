package org.xuanfeng.idphotosbackend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.mapper.AppUserHistoryMapper;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.request.AdminUserHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;
import org.xuanfeng.idphotosbackend.service.AppUserHistoryService;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AppUserHistoryServiceImpl implements AppUserHistoryService {

    @Resource
    private AppUserHistoryMapper appUserHistoryMapper;

    @Override
    public PageInfo<AppUserHistory> getPageByUserId(HistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getUserId() != null, "request.userId can not be null");
        Preconditions.checkArgument(request.getPageNum() != null, "request.pageNum can not be null");
        Preconditions.checkArgument(request.getPageSize() != null, "request.pageSize can not be null");

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.isNeedCount());

        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();

        criteria.andEqualTo(AppUserHistory::getUserId, request.getUserId());
        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());

        // 默认降序
        if (OrderEnum.ASC.equals(request.getOrderEnum())) {
            weekend.orderBy("id").asc();
        } else {
            weekend.orderBy("id").desc();
        }

        List<AppUserHistory> list = appUserHistoryMapper.selectByExample(weekend);
        return new PageInfo<>(list);

    }

    @Override
    public PageInfo<AppUserHistory> getPageByCondition(AdminUserHistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getPageNum() != null, "request.pageNum can not be null");
        Preconditions.checkArgument(request.getPageSize() != null, "request.pageSize can not be null");

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.isNeedCount());

        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();

        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());

        if (request.getUserId() != null) {
            criteria.andEqualTo(AppUserHistory::getUserId, request.getUserId());
        }

        if (request.getStartTime() != null) {
            criteria.andGreaterThanOrEqualTo(AppUserHistory::getCreateTime, request.getStartTime());
        }

        if (request.getEndTime() != null) {
            criteria.andLessThanOrEqualTo(AppUserHistory::getCreateTime, request.getEndTime());
        }

        // 默认降序
        if (OrderEnum.ASC.equals(request.getOrderEnum())) {
            weekend.orderBy("id").asc();
        } else {
            weekend.orderBy("id").desc();
        }

        List<AppUserHistory> list = appUserHistoryMapper.selectByExample(weekend);
        return new PageInfo<>(list);
    }

    @Override
    public Integer insert(AppUserHistory appUserHistory) {
        return appUserHistoryMapper.insertSelective(appUserHistory);
    }

    @Override
    public Integer deleteByLessTime(Date date, AppUserHistory appUserHistory) {
        Preconditions.checkArgument(date != null, "date can not be null");
        Preconditions.checkArgument(appUserHistory != null, "appUserHistory can not be null");

        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andLessThan(AppUserHistory::getCreateTime, date);
        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());

        return appUserHistoryMapper.updateByExampleSelective(appUserHistory, weekend);
    }

    @Override
    public Integer deleteById(AppUserHistory appUserHistory) {
        Preconditions.checkArgument(appUserHistory != null, "appUserHistory can not be null");

        return appUserHistoryMapper.updateByPrimaryKeySelective(appUserHistory);
    }

    @Override
    public Long countTotalValidHistory() {
        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());
        return (long) appUserHistoryMapper.selectCountByExample(weekend);
    }

    @Override
    public Long countByCreateTimeRange(Date startTime, Date endTime) {
        Preconditions.checkArgument(startTime != null, "startTime can not be null");
        Preconditions.checkArgument(endTime != null, "endTime can not be null");

        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());
        criteria.andGreaterThanOrEqualTo(AppUserHistory::getCreateTime, startTime);
        criteria.andLessThanOrEqualTo(AppUserHistory::getCreateTime, endTime);
        return (long) appUserHistoryMapper.selectCountByExample(weekend);
    }

    @Override
    public List<AppUserHistory> listByCreateTimeRange(Date startTime, Date endTime) {
        Preconditions.checkArgument(startTime != null, "startTime can not be null");
        Preconditions.checkArgument(endTime != null, "endTime can not be null");

        Weekend<AppUserHistory> weekend = new Weekend<>(AppUserHistory.class);
        WeekendCriteria<AppUserHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(AppUserHistory::getState, StateEnum.VALID.getCode());
        criteria.andGreaterThanOrEqualTo(AppUserHistory::getCreateTime, startTime);
        criteria.andLessThanOrEqualTo(AppUserHistory::getCreateTime, endTime);
        return appUserHistoryMapper.selectByExample(weekend);
    }
}
