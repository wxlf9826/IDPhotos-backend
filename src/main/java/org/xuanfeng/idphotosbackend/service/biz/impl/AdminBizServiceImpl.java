package org.xuanfeng.idphotosbackend.service.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xuanfeng.idphotosbackend.core.enums.PointTypeEnum;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.springframework.beans.BeanUtils;
import org.xuanfeng.idphotosbackend.model.bo.AdminPicSizeBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminPointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsDistributionBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsPointBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsSummaryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminStatisticsTrendBO;
import org.xuanfeng.idphotosbackend.model.bo.PointTypeAggBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.AdminUserBO;
import org.xuanfeng.idphotosbackend.model.bo.SystemConfigBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.po.AppUser;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.po.PicSize;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.po.SystemConfig;
import org.xuanfeng.idphotosbackend.model.request.AdminUserByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.AdminUserHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.AdminPointHistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.AdminLoginRequest;
import org.xuanfeng.idphotosbackend.model.request.AdminUserRewardPointsRequest;
import org.xuanfeng.idphotosbackend.model.request.AdminUserStatusUpdateRequest;
import org.xuanfeng.idphotosbackend.model.request.PicSizeCreateRequest;
import org.xuanfeng.idphotosbackend.model.request.PicSizeUpdateRequest;
import org.xuanfeng.idphotosbackend.model.request.SystemConfigCreateRequest;
import org.xuanfeng.idphotosbackend.model.request.SystemConfigUpdateRequest;
import org.xuanfeng.idphotosbackend.service.AppUserService;
import org.xuanfeng.idphotosbackend.service.AppUserHistoryService;
import org.xuanfeng.idphotosbackend.service.PicSizeService;
import org.xuanfeng.idphotosbackend.service.PointHistoryService;
import org.xuanfeng.idphotosbackend.service.S3Service;
import org.xuanfeng.idphotosbackend.service.SystemConfigService;
import org.xuanfeng.idphotosbackend.service.biz.AdminBizService;
import org.xuanfeng.idphotosbackend.service.provider.ConfigProvider;
import org.xuanfeng.idphotosbackend.utils.TimeUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminBizServiceImpl implements AdminBizService {

    @Resource
    private ConfigProvider configProvider;

    @Resource
    private PicSizeService picSizeService;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private S3Service s3Service;

    @Resource
    private AppUserHistoryService appUserHistoryService;

    @Resource
    private PointHistoryService pointHistoryService;

    @Override
    public WxLoginBO login(AdminLoginRequest request) {
        String adminUsername = configProvider.getAdminUsername();
        String adminPassword = configProvider.getAdminPassword();

        if (StringUtils.isBlank(adminUsername) || StringUtils.isBlank(adminPassword)) {
            log.error("管理员账号或密码未配置");
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "管理员配置异常");
        }

        if (adminUsername.equals(request.getUsername()) && adminPassword.equals(request.getPassword())) {
            // 管理员登录，使用特殊的前缀或者ID来区分管理员
            StpUtil.login("admin_" + adminUsername);
            return WxLoginBO.builder()
                    .token(StpUtil.getTokenValue())
                    .build();
        }

        throw new CommonException(ResultCodeEnum.WX_LOGIN_FAIL, "管理员用户名或密码错误");
    }

    @Override
    public List<AdminPicSizeBO> getPicSizeList() {
        List<PicSize> allSize = picSizeService.getAllSize();
        return allSize.stream().map(po -> {
            AdminPicSizeBO bo = new AdminPicSizeBO();
            BeanUtils.copyProperties(po, bo);
            bo.setCreateTime(TimeUtils.dateToStr(po.getCreateTime()));
            return bo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean addPicSize(PicSizeCreateRequest request) {
        PicSize picSize = new PicSize();
        BeanUtils.copyProperties(request, picSize);
        return picSizeService.add(picSize);
    }

    @Override
    public boolean updatePicSize(PicSizeUpdateRequest request) {
        PicSize picSize = new PicSize();
        BeanUtils.copyProperties(request, picSize);
        return picSizeService.update(picSize);
    }

    @Override
    public boolean deletePicSize(Long id) {
        return picSizeService.delete(id);
    }

    @Override
    public List<SystemConfigBO> getSystemConfigList() {
        List<SystemConfig> systemConfigList = systemConfigService.getSystemConfigList();

        return systemConfigList.stream().map(po -> SystemConfigBO.builder()
                .id(po.getId())
                .configKey(po.getConfigKey())
                .configValue(po.getConfigValue())
                .description(po.getDescription())
                .createTime(TimeUtils.dateToStr(po.getCreateTime()))
                .updateTime(TimeUtils.dateToStr(po.getUpdateTime()))
                .build()).collect(Collectors.toList());
    }

    @Override
    public boolean addSystemConfig(SystemConfigCreateRequest request) {
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(request, systemConfig);
        return systemConfigService.add(systemConfig);
    }

    @Override
    public boolean updateSystemConfig(SystemConfigUpdateRequest request) {
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(request, systemConfig);
        return systemConfigService.update(systemConfig);
    }

    @Override
    public boolean deleteSystemConfig(Long id) {
        return systemConfigService.delete(id);
    }

    @Override
    public PageInfo<AdminUserBO> getUserPage(AdminUserByPageRequest request) {
        log.info("admin getUserPage request:{}", JSON.toJSONString(request));
        PageInfo<AppUser> pageInfo = appUserService.getPage(request);
        PageInfo<AdminUserBO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);

        result.setList(pageInfo.getList().stream().map(po -> AdminUserBO.builder()
                .id(po.getId())
                .nickName(StringUtils.isBlank(po.getNickName()) ? "用户" + po.getId() : po.getNickName())
                .avatarUrl(StringUtils.isNotBlank(po.getAvatarKey()) ? s3Service.getPresignedUrl(po.getAvatarKey()) : null)
                .points(po.getPoints())
                .status(po.getStatus())
                .createTime(TimeUtils.dateToStr(po.getCreateTime()))
                .build()).collect(Collectors.toList()));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean rewardUserPoints(AdminUserRewardPointsRequest request) {
        AppUser user = appUserService.getById(request.getUserId());
        if (user == null) {
            throw new CommonException(ResultCodeEnum.WX_USER_USER_NOT_EXIST);
        }

        int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
        int totalPoints = currentPoints + request.getPoints();

        Integer updateCount = appUserService.updateById(AppUser.builder()
                .id(user.getId())
                .points(totalPoints)
                .build());
        if (updateCount == null || updateCount != 1) {
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "更新用户积分失败");
        }

        Integer insertCount = pointHistoryService.insert(PointHistory.builder()
                .userId(user.getId())
                .changeAmount(request.getPoints())
                .type(PointTypeEnum.REWARD.getType())
                .totalPoints(totalPoints)
                .build());
        if (insertCount == null || insertCount != 1) {
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "新增积分流水失败");
        }
        return true;
    }

    @Override
    public boolean updateUserStatus(AdminUserStatusUpdateRequest request) {
        Integer updateCount = appUserService.updateById(AppUser.builder()
                .id(request.getUserId())
                .status(request.getStatus())
                .build());
        return updateCount != null && updateCount == 1;
    }

    @Override
    public PageInfo<AdminUserHistoryBO> getUserHistoryPage(AdminUserHistoryByPageRequest request) {
        PageInfo<AppUserHistory> pageInfo = appUserHistoryService.getPageByCondition(request);
        PageInfo<AdminUserHistoryBO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);

        List<Long> userIdList = pageInfo.getList().stream()
                .map(AppUserHistory::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, AppUser> appUserMap = userIdList.isEmpty()
                ? Collections.emptyMap()
                : appUserService.getByIdList(userIdList).stream()
                .collect(Collectors.toMap(AppUser::getId, user -> user));

        result.setList(pageInfo.getList().stream().map(po -> {
            AppUser appUser = appUserMap.get(po.getUserId());
            String nickName = appUser == null || StringUtils.isBlank(appUser.getNickName())
                    ? "用户" + po.getUserId()
                    : appUser.getNickName();

            return AdminUserHistoryBO.builder()
                    .id(po.getId())
                    .userId(po.getUserId())
                    .nickName(nickName)
                    .imageKey(po.getImageKey())
                    .imageUrl(StringUtils.isNotBlank(po.getImageKey()) ? s3Service.getPresignedUrl(po.getImageKey()) : null)
                    .date(po.getTime())
                    .createTime(TimeUtils.dateToStr(po.getCreateTime()))
                    .sizeName(po.getSizeName())
                    .heightPx(po.getHeightPx())
                    .widthPx(po.getWidthPx())
                    .bgColor(po.getBgColor())
                    .renderMode(po.getRenderMode())
                    .beautyConfig(JSON.toJSONString(po.getBeautyConfig()))
                    .watermarkConfig(JSON.toJSONString(po.getWatermarkConfig()))
                    .otherConfig(JSON.toJSONString(po.getOtherConfig()))
                    .build();
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public PageInfo<AdminPointHistoryBO> getPointHistoryPage(AdminPointHistoryByPageRequest request) {
        PageInfo<PointHistory> pageInfo = pointHistoryService.getPageByCondition(request);
        PageInfo<AdminPointHistoryBO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);

        List<Long> userIdList = pageInfo.getList().stream()
                .map(PointHistory::getUserId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, AppUser> appUserMap = userIdList.isEmpty()
                ? Collections.emptyMap()
                : appUserService.getByIdList(userIdList).stream()
                .collect(Collectors.toMap(AppUser::getId, user -> user));

        result.setList(pageInfo.getList().stream().map(po -> {
            AppUser appUser = appUserMap.get(po.getUserId());
            String nickName = appUser == null || StringUtils.isBlank(appUser.getNickName())
                    ? "用户" + po.getUserId()
                    : appUser.getNickName();

            PointTypeEnum pointTypeEnum = PointTypeEnum.getByType(po.getType());
            String changeType = pointTypeEnum == null ? po.getType() : pointTypeEnum.getDesc();

            return AdminPointHistoryBO.builder()
                    .id(po.getId())
                    .userId(po.getUserId())
                    .nickName(nickName)
                    .type(po.getType())
                    .changeType(changeType)
                    .changeAmount(po.getChangeAmount())
                    .totalPoints(po.getTotalPoints())
                    .createTime(TimeUtils.dateToStr(po.getCreateTime()))
                    .build();
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public AdminStatisticsSummaryBO getStatisticsSummary() {
        Date dayStart = getDayStart(new Date());
        Date dayEnd = getDayEnd(new Date());

        Long todayNewUserCount = defaultLong(appUserService.countByCreateTimeRange(dayStart, dayEnd));
        Long totalUserCount = defaultLong(appUserService.countTotalValidUser());
        Long todayMakeCount = defaultLong(appUserHistoryService.countByCreateTimeRange(dayStart, dayEnd));
        Long totalMakeCount = defaultLong(appUserHistoryService.countTotalValidHistory());

        Long todayPointConsume = getPointConsume(dayStart, dayEnd);

        Double avgMakePerUser = 0D;
        if (totalUserCount > 0) {
            avgMakePerUser = BigDecimal.valueOf(totalMakeCount)
                    .divide(BigDecimal.valueOf(totalUserCount), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return AdminStatisticsSummaryBO.builder()
                .todayNewUserCount(todayNewUserCount)
                .totalUserCount(totalUserCount)
                .todayMakeCount(todayMakeCount)
                .totalMakeCount(totalMakeCount)
                .todayPointConsume(todayPointConsume)
                .avgMakePerUser(avgMakePerUser)
                .build();
    }

    @Override
    public AdminStatisticsTrendBO getStatisticsTrend() {
        List<String> dateList = new ArrayList<>();
        List<Long> makeCountList = new ArrayList<>();
        List<Long> registerCountList = new ArrayList<>();

        Date now = new Date();
        Date baseDate = getDayStart(addDays(now, -6));
        for (int i = 0; i < 7; i++) {
            Date currentDate = addDays(baseDate, i);
            Date startTime = getDayStart(currentDate);
            Date endTime = getDayEnd(currentDate);

            dateList.add(TimeUtils.dateToStr(currentDate, TimeUtils.YMD_PATTERN));
            makeCountList.add(defaultLong(appUserHistoryService.countByCreateTimeRange(startTime, endTime)));
            registerCountList.add(defaultLong(appUserService.countByCreateTimeRange(startTime, endTime)));
        }

        return AdminStatisticsTrendBO.builder()
                .dateList(dateList)
                .makeCountList(makeCountList)
                .registerCountList(registerCountList)
                .build();
    }

    @Override
    public AdminStatisticsDistributionBO getStatisticsDistribution() {
        List<PointTypeAggBO> pointTypeAggList = pointHistoryService.sumAbsChangeAmountGroupByType();
        List<AdminStatisticsPointBO> pointChangePie = buildPointChangePie(pointTypeAggList);

        return AdminStatisticsDistributionBO.builder()
                .pointChangePie(pointChangePie)
                .build();
    }

    private List<AdminStatisticsPointBO> buildPointChangePie(List<PointTypeAggBO> pointTypeAggList) {
        Map<String, Long> pointMap = new LinkedHashMap<>();
        for (PointTypeEnum pointTypeEnum : PointTypeEnum.values()) {
            pointMap.put(pointTypeEnum.getDesc(), 0L);
        }

        for (PointTypeAggBO pointTypeAggBO : pointTypeAggList) {
            if (pointTypeAggBO == null || StringUtils.isBlank(pointTypeAggBO.getType())) {
                continue;
            }
            PointTypeEnum typeEnum = PointTypeEnum.getByType(pointTypeAggBO.getType());
            String key = typeEnum == null ? pointTypeAggBO.getType() : typeEnum.getDesc();
            pointMap.put(key, pointMap.getOrDefault(key, 0L) + defaultLong(pointTypeAggBO.getAmount()));
        }

        return pointMap.entrySet().stream()
                .map(entry -> AdminStatisticsPointBO.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private Long getPointConsume(Date startTime, Date endTime) {
        List<PointHistory> pointHistoryList = pointHistoryService.listByCreateTimeRange(startTime, endTime);
        long consume = 0L;
        for (PointHistory pointHistory : pointHistoryList) {
            if (pointHistory == null) {
                continue;
            }
            if (!StringUtils.equals(PointTypeEnum.MAKE.getType(), pointHistory.getType())) {
                continue;
            }
            Integer changeAmount = pointHistory.getChangeAmount();
            if (changeAmount == null) {
                continue;
            }
            if (changeAmount < 0) {
                consume += -changeAmount;
            }
        }
        return consume;
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    private Date getDayStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
