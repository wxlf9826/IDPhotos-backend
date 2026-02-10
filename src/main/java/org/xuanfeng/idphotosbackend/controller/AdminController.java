package org.xuanfeng.idphotosbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.xuanfeng.idphotosbackend.model.bo.AdminPicSizeBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminPointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsDistributionBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsPointBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsSummaryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsTrendBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserBO;
import org.xuanfeng.idphotosbackend.model.bo.SystemConfigBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.qo.AdminUserHistoryByPageQO;
import org.xuanfeng.idphotosbackend.model.qo.AdminPointHistoryByPageQO;
import org.xuanfeng.idphotosbackend.model.qo.AdminUserByPageQO;
import org.xuanfeng.idphotosbackend.model.request.*;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;
import org.xuanfeng.idphotosbackend.model.vo.AdminUserHistoryVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminPointHistoryVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminUserVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminLoginVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminPicSizeVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminStatisticsDistributionVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminStatisticsPointVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminStatisticsSummaryVO;
import org.xuanfeng.idphotosbackend.model.vo.AdminStatisticsTrendVO;
import org.xuanfeng.idphotosbackend.model.vo.PageListVO;
import org.xuanfeng.idphotosbackend.model.vo.SystemConfigVO;
import org.xuanfeng.idphotosbackend.core.enums.OrderEnum;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.service.biz.AdminBizService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminBizService adminBizService;

    @PostMapping("/login")
    public ResponseResult<AdminLoginVO> login(@RequestBody @Valid AdminLoginRequest request) {
        WxLoginBO loginBO = adminBizService.login(request);
        return ResponseResult.success(AdminLoginVO.builder()
                .token(loginBO.getToken())
                .build());
    }

    @GetMapping("/checkLogin")
    public ResponseResult<Boolean> checkLogin() {
        return ResponseResult.success(StpUtil.isLogin());
    }

    @GetMapping("/size/list")
    public ResponseResult<List<AdminPicSizeVO>> getPicSizeList() {
        List<AdminPicSizeBO> boList = adminBizService.getPicSizeList();

        return ResponseResult.success( boList.stream().map(bo -> {
            AdminPicSizeVO vo = new AdminPicSizeVO();
            BeanUtils.copyProperties(bo, vo);
            return vo;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/size/add")
    public ResponseResult<Boolean> addPicSize(@RequestBody @Valid PicSizeCreateRequest request) {
        return ResponseResult.success(adminBizService.addPicSize(request));
    }

    @PostMapping("/size/update")
    public ResponseResult<Boolean> updatePicSize(@RequestBody @Valid PicSizeUpdateRequest request) {
        return ResponseResult.success(adminBizService.updatePicSize(request));
    }

    @GetMapping("/size/delete")
    public ResponseResult<Boolean> deletePicSize(@RequestParam Long id) {
        return ResponseResult.success(adminBizService.deletePicSize(id));
    }

    @GetMapping("/systemConfig/list")
    public ResponseResult<List<SystemConfigVO>> getSystemConfigList() {
        List<SystemConfigBO> boList = adminBizService.getSystemConfigList();
        return ResponseResult.success(
                boList.stream().map(bo -> {
                    SystemConfigVO vo = new SystemConfigVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList())
        );
    }

    @PostMapping("/systemConfig/add")
    public ResponseResult<Boolean> addSystemConfig(@RequestBody @Valid SystemConfigCreateRequest request) {
        return ResponseResult.success(adminBizService.addSystemConfig(request));
    }

    @PostMapping("/systemConfig/update")
    public ResponseResult<Boolean> updateSystemConfig(@RequestBody @Valid SystemConfigUpdateRequest request) {
        return ResponseResult.success(adminBizService.updateSystemConfig(request));
    }

    @GetMapping("/systemConfig/delete")
    public ResponseResult<Boolean> deleteSystemConfig(@RequestParam Long id) {
        return ResponseResult.success(adminBizService.deleteSystemConfig(id));
    }

    @PostMapping("/user/list")
    public ResponseResult<PageListVO<AdminUserVO>> getUserList(@RequestBody @Valid AdminUserByPageQO qo) {
        OrderEnum orderEnum = OrderEnum.getByOrder(StringUtils.lowerCase(qo.getOrder()));
        if (orderEnum == null) {
            orderEnum = OrderEnum.DESC;
        }

        PageInfo<AdminUserBO> pageInfo = adminBizService.getUserPage(AdminUserByPageRequest.builder()
                .pageNum(qo.getPageNum())
                .pageSize(qo.getPageSize())
                .nickName(qo.getNickName())
                .status(qo.getStatus())
                .sortField(qo.getSortField())
                .needCount(true)
                .orderEnum(orderEnum)
                .build());

        return ResponseResult.success(PageListVO.<AdminUserVO>builder()
                .hasMore(pageInfo.isHasNextPage())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .total(pageInfo.getTotal())
                .list(pageInfo.getList().stream().map(bo -> {
                    AdminUserVO vo = new AdminUserVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList()))
                .build());
    }

    @PostMapping("/user/points/reward")
    public ResponseResult<Boolean> rewardUserPoints(@RequestBody @Valid AdminUserRewardPointsRequest request) {
        return ResponseResult.success(adminBizService.rewardUserPoints(request));
    }

    @PostMapping("/user/status/update")
    public ResponseResult<Boolean> updateUserStatus(@RequestBody @Valid AdminUserStatusUpdateRequest request) {
        if (request.getStatus() == null || (request.getStatus() != 1 && request.getStatus() != 2)) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "status 仅支持 1(解封)/2(封禁)");
        }
        return ResponseResult.success(adminBizService.updateUserStatus(request));
    }

    @PostMapping("/user/history/list")
    public ResponseResult<PageListVO<AdminUserHistoryVO>> getUserHistoryPage(@RequestBody @Valid AdminUserHistoryByPageQO qo) {
        Date startTime = parseDate(qo.getStartDate(), false);
        Date endTime = parseDate(qo.getEndDate(), true);
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "startDate 不能大于 endDate");
        }

        PageInfo<AdminUserHistoryBO> pageInfo = adminBizService.getUserHistoryPage(AdminUserHistoryByPageRequest.builder()
                .userId(qo.getUserId())
                .startTime(startTime)
                .endTime(endTime)
                .pageNum(qo.getPageNum())
                .pageSize(qo.getPageSize())
                .needCount(true)
                .orderEnum(OrderEnum.DESC)
                .build());

        return ResponseResult.success(PageListVO.<AdminUserHistoryVO>builder()
                .hasMore(pageInfo.isHasNextPage())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .total(pageInfo.getTotal())
                .list(pageInfo.getList().stream().map(bo -> {
                    AdminUserHistoryVO vo = new AdminUserHistoryVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList()))
                .build());
    }

    @PostMapping("/user/point/history/list")
    public ResponseResult<PageListVO<AdminPointHistoryVO>> getPointHistoryPage(@RequestBody @Valid AdminPointHistoryByPageQO qo) {
        PageInfo<AdminPointHistoryBO> pageInfo = adminBizService.getPointHistoryPage(AdminPointHistoryByPageRequest.builder()
                .userId(qo.getUserId())
                .type(qo.getType())
                .pageNum(qo.getPageNum())
                .pageSize(qo.getPageSize())
                .needCount(true)
                .orderEnum(OrderEnum.DESC)
                .build());

        return ResponseResult.success(PageListVO.<AdminPointHistoryVO>builder()
                .hasMore(pageInfo.isHasNextPage())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .total(pageInfo.getTotal())
                .list(pageInfo.getList().stream().map(bo -> {
                    AdminPointHistoryVO vo = new AdminPointHistoryVO();
                    BeanUtils.copyProperties(bo, vo);
                    return vo;
                }).collect(Collectors.toList()))
                .build());
    }

    @GetMapping("/statistics/summary")
    public ResponseResult<AdminStatisticsSummaryVO> getStatisticsSummary() {
        AdminStatisticsSummaryBO bo = adminBizService.getStatisticsSummary();
        AdminStatisticsSummaryVO vo = new AdminStatisticsSummaryVO();
        BeanUtils.copyProperties(bo, vo);
        return ResponseResult.success(vo);
    }

    @GetMapping("/statistics/trend")
    public ResponseResult<AdminStatisticsTrendVO> getStatisticsTrend() {
        AdminStatisticsTrendBO bo = adminBizService.getStatisticsTrend();
        AdminStatisticsTrendVO vo = new AdminStatisticsTrendVO();
        BeanUtils.copyProperties(bo, vo);
        return ResponseResult.success(vo);
    }

    @GetMapping("/statistics/distribution")
    public ResponseResult<AdminStatisticsDistributionVO> getStatisticsDistribution() {
        AdminStatisticsDistributionBO bo = adminBizService.getStatisticsDistribution();

        AdminStatisticsDistributionVO vo = new AdminStatisticsDistributionVO();
        vo.setPointChangePie(convertPointList(bo.getPointChangePie()));

        return ResponseResult.success(vo);
    }

    private List<AdminStatisticsPointVO> convertPointList(List<AdminStatisticsPointBO> boList) {
        if (boList == null || boList.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return boList.stream().map(bo -> {
            AdminStatisticsPointVO vo = new AdminStatisticsPointVO();
            BeanUtils.copyProperties(bo, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private Date parseDate(String dateStr, boolean endOfDay) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }

        String fullDate = endOfDay ? dateStr + " 23:59:59" : dateStr + " 00:00:00";
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fullDate);
        } catch (ParseException e) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "日期格式错误，需为 yyyy-MM-dd");
        }
    }
}
