package org.xuanfeng.idphotosbackend.service.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.constant.CommonConstants;
import org.xuanfeng.idphotosbackend.core.enums.PointTypeEnum;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.core.enums.WxGrantTypeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.bo.PointHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.UserHistoryBO;
import org.xuanfeng.idphotosbackend.model.bo.WxLoginBO;
import org.xuanfeng.idphotosbackend.model.bo.WxUserInfoBO;
import org.xuanfeng.idphotosbackend.model.dto.OpenIdDTO;
import org.xuanfeng.idphotosbackend.model.po.AppUser;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.request.HistoryByPageRequest;
import org.xuanfeng.idphotosbackend.model.request.OpenIdRequest;
import org.xuanfeng.idphotosbackend.proxy.WxProxy;
import org.xuanfeng.idphotosbackend.service.AppUserHistoryService;
import org.xuanfeng.idphotosbackend.service.AppUserService;
import org.xuanfeng.idphotosbackend.service.PointHistoryService;
import org.xuanfeng.idphotosbackend.service.S3Service;
import org.xuanfeng.idphotosbackend.service.biz.UserBizService;
import org.xuanfeng.idphotosbackend.service.provider.ConfigProvider;
import org.xuanfeng.idphotosbackend.utils.CoverUtils;
import org.xuanfeng.idphotosbackend.utils.TimeUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBizServiceImpl implements UserBizService {

    @Resource
    private ConfigProvider configProvider;

    @Resource
    private WxProxy wxProxy;

    @Resource
    private AppUserService appUserService;

    @Resource
    private S3Service s3Service;

    @Resource
    private AppUserHistoryService appUserHistoryService;

    @Resource
    private PointHistoryService pointHistoryService;

    @Override
    public WxLoginBO wxLogin(String code) {
        // 获取系统信息
        String appId = configProvider.getAppId();
        String appSecret = configProvider.getAppSecret();
        String adUnitId = configProvider.getAdUnitId();
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            final String errorMsg = "no appId or appSecret provided";
            log.error(errorMsg);
            throw new CommonException(ResultCodeEnum.SYSTEM_INFO_ERROR);
        }

        // 请求微信获取登录信息
        OpenIdDTO openIdDTO = wxProxy.getOpenId(OpenIdRequest.builder()
                .appId(appId)
                .secret(appSecret)
                .jsCode(code)
                .grantType(WxGrantTypeEnum.AUTHORIZATION_CODE.getType())
                .build());

        if (openIdDTO == null) {
            log.error("wxLogin fail, openIdDTO is null, code:{}", code);
            throw new CommonException(ResultCodeEnum.WX_LOGIN_FAIL);
        }

        // 查询数据库该用户
        AppUser appUser = appUserService.getUserByOpenId(openIdDTO.getOpenId());
        // 新用户
        if (appUser == null) {
            appUser = AppUser.builder()
                    .openId(openIdDTO.getOpenId())
                    .points(configProvider.getDefaultPoints())
                    .build();
            appUserService.insert(appUser);
        } else {
            if (appUser.getStatus().equals(2)) {
                log.error("用户已注销, appUser:{}", JSON.toJSONString(appUser));
                throw new CommonException(ResultCodeEnum.WX_USER_DELETED);
            }
        }

        // 放到stpUtil中
        StpUtil.login(appUser.getId());

        //封装信息返回前端
        return WxLoginBO.builder()
                .openId(appUser.getOpenId())
                .token(StpUtil.getTokenInfo().getTokenValue())
                .adUnitId(adUnitId)
                .build();

    }

    @Override
    public void updateUserInfo(MultipartFile file, String nickName, Long userId) {
        Preconditions.checkArgument(userId != null, "userId can not be null");

        String key = null;
        if (file != null) {
            key = uploadAvatar(file, userId);
        }

        if (StringUtils.isBlank(nickName)) {
            nickName = "用户" + userId;
        }

        AppUser updateParam = AppUser.builder()
                .id(userId)
                .avatarKey(key)
                .nickName(nickName)
                .build();

        if (appUserService.updateById(updateParam) != 1) {
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "用户信息更新失败");
        }
    }

    @Override
    public WxUserInfoBO getUserInfo(Long userId) {
        AppUser user = appUserService.getById(userId);
        if (user == null) {
            throw new CommonException(ResultCodeEnum.WX_USER_USER_NOT_EXIST);
        }

        String fullAvatarUrl = null;
        // 这里需要把数据库存的 avatarKey 转为可访问的 URL
        if (StringUtils.isNotBlank(user.getAvatarKey())) {
            fullAvatarUrl = s3Service.getPresignedUrl(user.getAvatarKey());
        }

        return WxUserInfoBO.builder()
                .nickName(StringUtils.isBlank(user.getNickName()) ? getDefaultNickName(userId) : user.getNickName())
                .avatarUrl(fullAvatarUrl)
                .createTime(DateFormatUtils.format(user.getCreateTime(), "yyyy-MM-dd HH:mm:ss"))
                .points(user.getPoints())
                .build();
    }

    @Override
    public PageInfo<UserHistoryBO> getUserHistory(HistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");

        PageInfo<AppUserHistory> pageInfo = appUserHistoryService.getPageByUserId(request);
        PageInfo<UserHistoryBO> result = CoverUtils.convertPageInfo(pageInfo, UserHistoryBO.class);
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return result;
        }

        // PO 转为 BO
        result.setList(
                pageInfo.getList().stream()
                        .map(
                                po -> {
                                    UserHistoryBO bo = new UserHistoryBO();
                                    BeanUtils.copyProperties(po, bo);
                                    bo.setDate(TimeUtils.dateToStr(po.getCreateTime(), TimeUtils.YMD_PATTERN));
                                    bo.setImageUrl(StringUtils.isNotBlank(po.getImageKey()) ? s3Service.getPresignedUrl(po.getImageKey()) : null);
                                    bo.setCreateTime(TimeUtils.dateToStr(po.getCreateTime(), TimeUtils.DEFAULT_PATTERN));
                                    return bo;
                                }
                        )
                        .collect(Collectors.toList())
        );

        return result;
    }

    @Override
    public Integer deleteHistory(Long historyId) {

        AppUserHistory appUserHistory = AppUserHistory.builder()
                .id(historyId)
                .state(StateEnum.INVALID.getCode())
                .build();
        return appUserHistoryService.deleteById(appUserHistory);
    }

    @Override
    public PageInfo<PointHistoryBO> getUserPointHistory(HistoryByPageRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");

        PageInfo<PointHistory> pageInfo = pointHistoryService.getPageByUserId(request);
        PageInfo<PointHistoryBO> result = CoverUtils.convertPageInfo(pageInfo, PointHistoryBO.class);
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return result;
        }

        // PO 转为 BO
        result.setList(
                pageInfo.getList().stream()
                        .map(
                                po -> {
                                    PointHistoryBO bo = new PointHistoryBO();
                                    BeanUtils.copyProperties(po, bo);
                                    bo.setChangeAmount(po.getChangeAmount());
                                    bo.setChangeType(Objects.requireNonNull(PointTypeEnum.getByType(po.getType())).getDesc());
                                    bo.setTime(TimeUtils.dateToStr(po.getCreateTime(), TimeUtils.DEFAULT_PATTERN));
                                    bo.setTotalPoints(po.getTotalPoints());
                                    return bo;
                                }
                        )
                        .collect(Collectors.toList())
        );

        return result;
    }

    private String uploadAvatar(MultipartFile file, Long userId) {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "上传文件不能为空");
        }

        // 2. 校验图片格式（通过后缀名和MIME类型双重校验）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "仅支持图片格式文件上传");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // 允许的后缀白名单
        List<String> allowSuffix = Arrays.asList(".jpg", ".jpeg", ".png");
        if (!allowSuffix.contains(suffix)) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "仅支持jpg，jpeg，png图片");
        }

        // 3. 校验图片大小 (例如限制 5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new CommonException(ResultCodeEnum.PARAM_ERROR, "头像图片不能超过 5MB");
        }

        // 优化：文件名包含用户ID，方便追踪
        String key = String.format(CommonConstants.S3_AVATARS_KEY, UUID.randomUUID());

        try (InputStream is = file.getInputStream()) { // 使用 try-with-resources
            return s3Service.uploadFile(is, key, file.getContentType());
        } catch (IOException e) {
            log.error("用户 {} 头像上传读取失败", userId, e);
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "图片处理异常");
        }
    }

    /**
     * 获取默认名称
     *
     * @param userId userId
     * @return 默认名称
     */
    private String getDefaultNickName(Long userId) {
        return "用户" + userId;
    }
}
