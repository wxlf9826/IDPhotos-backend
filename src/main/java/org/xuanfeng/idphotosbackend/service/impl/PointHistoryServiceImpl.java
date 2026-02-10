package org.xuanfeng.idphotosbackend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.bo.PointTypeAggBO;
import org.xuanfeng.idphotosbackend.model.mapper.PointHistoryMapper;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.request.AdminPointHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;
import org.xuanfeng.idphotosbackend.service.PointHistoryService;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PointHistoryServiceImpl implements PointHistoryService {

    @Resource
    private PointHistoryMapper pointHistoryMapper;

    @Override
    public PageInfo<PointHistory> getPageByUserId(HistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getUserId() != null, "request.userId can not be null");
        Preconditions.checkArgument(request.getPageNum() != null, "request.pageNum can not be null");
        Preconditions.checkArgument(request.getPageSize() != null, "request.pageSize can not be null");

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.isNeedCount());

        Weekend<PointHistory> weekend = new Weekend<>(PointHistory.class);
        WeekendCriteria<PointHistory, Object> criteria = weekend.weekendCriteria();

        criteria.andEqualTo(PointHistory::getUserId, request.getUserId());
        criteria.andEqualTo(PointHistory::getState, StateEnum.VALID.getCode());

        // 默认降序
        if (OrderEnum.ASC.equals(request.getOrderEnum())) {
            weekend.orderBy("id").asc();
        } else {
            weekend.orderBy("id").desc();
        }

        List<PointHistory> list = pointHistoryMapper.selectByExample(weekend);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<PointHistory> getPageByCondition(AdminPointHistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getPageNum() != null, "request.pageNum can not be null");
        Preconditions.checkArgument(request.getPageSize() != null, "request.pageSize can not be null");

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.isNeedCount());

        Weekend<PointHistory> weekend = new Weekend<>(PointHistory.class);
        WeekendCriteria<PointHistory, Object> criteria = weekend.weekendCriteria();

        criteria.andEqualTo(PointHistory::getState, StateEnum.VALID.getCode());
        if (request.getUserId() != null) {
            criteria.andEqualTo(PointHistory::getUserId, request.getUserId());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getType())) {
            criteria.andEqualTo(PointHistory::getType, request.getType());
        }

        if (OrderEnum.ASC.equals(request.getOrderEnum())) {
            weekend.orderBy("id").asc();
        } else {
            weekend.orderBy("id").desc();
        }

        return new PageInfo<>(pointHistoryMapper.selectByExample(weekend));
    }

    @Override
    public Integer insert(PointHistory pointHistory) {
        return pointHistoryMapper.insertSelective(pointHistory);
    }

    @Override
    public List<PointHistory> listByCreateTimeRange(Date startTime, Date endTime) {
        Preconditions.checkArgument(startTime != null, "startTime can not be null");
        Preconditions.checkArgument(endTime != null, "endTime can not be null");

        Weekend<PointHistory> weekend = new Weekend<>(PointHistory.class);
        WeekendCriteria<PointHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(PointHistory::getState, StateEnum.VALID.getCode());
        criteria.andGreaterThanOrEqualTo(PointHistory::getCreateTime, startTime);
        criteria.andLessThanOrEqualTo(PointHistory::getCreateTime, endTime);
        return pointHistoryMapper.selectByExample(weekend);
    }

    @Override
    public List<PointHistory> listAllValid() {
        Weekend<PointHistory> weekend = new Weekend<>(PointHistory.class);
        WeekendCriteria<PointHistory, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(PointHistory::getState, StateEnum.VALID.getCode());
        return pointHistoryMapper.selectByExample(weekend);
    }

    @Override
    public List<PointTypeAggBO> sumAbsChangeAmountGroupByType() {
        return pointHistoryMapper.sumAbsChangeAmountGroupByType();
    }
}
