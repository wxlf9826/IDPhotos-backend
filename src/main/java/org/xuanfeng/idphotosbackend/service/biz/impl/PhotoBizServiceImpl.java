package org.xuanfeng.idphotosbackend.service.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.core.enums.PointTypeEnum;
import org.xuanfeng.idphotosbackend.core.enums.RenderEnum;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.bo.PhotoCreateBO;
import org.xuanfeng.idphotosbackend.model.dto.IdPhotoCommonDTO;
import org.xuanfeng.idphotosbackend.model.po.AppUser;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.request.*;
import org.xuanfeng.idphotosbackend.proxy.PhotoProxy;
import org.xuanfeng.idphotosbackend.service.AppUserHistoryService;
import org.xuanfeng.idphotosbackend.service.AppUserService;
import org.xuanfeng.idphotosbackend.service.PointHistoryService;
import org.xuanfeng.idphotosbackend.service.S3Service;
import org.xuanfeng.idphotosbackend.service.biz.PhotoBizService;
import org.xuanfeng.idphotosbackend.utils.PhotoUtils;
import org.xuanfeng.idphotosbackend.utils.TimeUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class PhotoBizServiceImpl implements PhotoBizService {

    @Resource
    private S3Service s3Service;

    @Resource
    private AppUserService appUserService;

    @Resource
    private PhotoProxy photoProxy;

    @Resource
    private AppUserHistoryService appUserHistoryService;

    @Resource
    private PointHistoryService pointHistoryService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PhotoCreateBO createPhoto(MultipartFile file, PhotoCreateRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(request.getUserId() != null, "request.useId can not be null");
        Preconditions.checkArgument(request.getPoints() != null && request.getPoints() > 0, "request.points must > 0");

        Long userId = request.getUserId();
        // 判断积分是否足够
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            final String errMsg = "no user found:" + userId;
            log.error(errMsg);
            throw new CommonException(ResultCodeEnum.WX_USER_USER_NOT_EXIST);
        }

        // 积分不足
        if (appUser.getPoints() - request.getPoints() < 0) {
            final String errMsg = String.format("points not enough, userId:%s, currentPoints:%s, needPoints:%s", userId, appUser.getPoints(), request.getPoints());
            log.error(errMsg);
            throw new CommonException(ResultCodeEnum.POINTS_NOT_ENOUGH);
        }

        IdPhotoInferenceRequest idPhotoInferenceRequest = IdPhotoInferenceRequest.builder()
                .hd(false)
                .inputImage(file)
                .build();

        if (request.getHeightPx() != null && request.getWidthPx() != null) {
            idPhotoInferenceRequest.setHeight(request.getHeightPx());
            idPhotoInferenceRequest.setWidth(request.getWidthPx());
        }

        if (request.getBeautyConfig() != null && request.getBeautyConfig().getEnable()) {
            idPhotoInferenceRequest.setWhiteningStrength(request.getBeautyConfig().getWhitening());
            idPhotoInferenceRequest.setBrightnessStrength(request.getBeautyConfig().getBrightness().floatValue());
            idPhotoInferenceRequest.setContrastStrength(request.getBeautyConfig().getContrast().floatValue());
            idPhotoInferenceRequest.setSharpenStrength(request.getBeautyConfig().getSharpening().floatValue());
            idPhotoInferenceRequest.setSaturationStrength(request.getBeautyConfig().getSaturation().floatValue());
        }
        if (request.getOtherConfig() != null && request.getOtherConfig().getEnable()) {
            Double faceRatio = request.getOtherConfig().getFaceRatio();
            if (faceRatio != null) {
                idPhotoInferenceRequest.setHeadHeightRatio(faceRatio.floatValue());
            }
            Double topDistance = request.getOtherConfig().getTopDistance();
            if (topDistance != null) {
                idPhotoInferenceRequest.setTopDistanceMax(topDistance.floatValue());
                idPhotoInferenceRequest.setTopDistanceMin((float) (topDistance - 0.02));
            }
        }

        // 1. 智能证件照，尺寸、美颜、比例等调整
        IdPhotoCommonDTO idPhotoInferenceResult = photoProxy.idPhotoInference(idPhotoInferenceRequest);

        if (idPhotoInferenceResult == null || !idPhotoInferenceResult.getStatus()) {
            log.error("createPhoto idPhotoInference error, request:{}", idPhotoInferenceRequest);
            throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "处理图片失败");
        }

        String currentImage = idPhotoInferenceResult.getImageBase64Standard();

        // 2. 替换背景颜色
        if (StringUtils.isNotBlank(request.getBgColor())) {
            String renderMode = request.getRenderMode();
            Integer render = null;
            if (StringUtils.isNotBlank(renderMode)) {
                RenderEnum renderEnum = RenderEnum.getByType(renderMode);
                Preconditions.checkArgument(renderEnum != null, "renderModel is not support");
                render = renderEnum.getNumber();
            }
            AddBackgroundRequest addBackgroundRequest = AddBackgroundRequest.builder()
                    .inputImageBase64(currentImage)
                    .color(request.getBgColor())
                    .render(render)
                    .build();

            IdPhotoCommonDTO backgroundResult = photoProxy.addBackground(addBackgroundRequest);

            if (backgroundResult == null || !backgroundResult.getStatus()) {
                log.error("createPhoto addBackground error, request:{}", addBackgroundRequest);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "处理图片失败");
            }

            currentImage = backgroundResult.getImageBase64();
        }

        // 3. 替换水印
        if (request.getWatermarkConfig() != null && request.getWatermarkConfig().getEnable()) {
            WatermarkRequest watermarkRequest = WatermarkRequest.builder()
                    .inputImageBase64(currentImage)
                    .angle(request.getWatermarkConfig().getAngle())
                    .color(request.getWatermarkConfig().getColor())
                    .size(request.getWatermarkConfig().getSize())
                    .text(request.getWatermarkConfig().getText())
                    .opacity(request.getWatermarkConfig().getOpacity().floatValue())
                    .space(request.getWatermarkConfig().getSpacing())
                    .build();

            IdPhotoCommonDTO watermarkResult = photoProxy.watermark(watermarkRequest);

            if (watermarkResult == null || !watermarkResult.getStatus()) {
                log.error("createPhoto watermark error, request:{}", watermarkRequest);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "处理图片失败");
            }

            currentImage = watermarkResult.getImageBase64();
        }

        // 4. 其他设置
        if (request.getOtherConfig() != null && request.getOtherConfig().getEnable()) {
            SetKbRequest setKbRequest = SetKbRequest.builder()
                    .inputImageBase64(currentImage)
                    .kb(request.getOtherConfig().getKbEnable() ? request.getOtherConfig().getKbSize() : null)
                    .dpi(request.getOtherConfig().getDpiEnable() ? request.getOtherConfig().getDpiValue() : null)
                    .build();

            IdPhotoCommonDTO kbResult = photoProxy.setKb(setKbRequest);

            if (kbResult == null || !kbResult.getStatus()) {
                log.error("createPhoto kbResult error, request:{}", kbResult);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "处理图片失败");
            }

            currentImage = kbResult.getImageBase64();
        }

        // 上传到s3
        Pair<String, InputStream> pair = PhotoUtils.base64ToInputStream(currentImage);
        String key = String.format("photo/%s", UUID.randomUUID());

        s3Service.uploadFile(pair.getRight(), key, pair.getLeft());
        String url = s3Service.getPresignedUrl(key);

        int totalPoints = appUser.getPoints() - request.getPoints();
        // 扣除积分
        appUserService.updateById(AppUser.builder()
                .id(userId)
                .points(totalPoints)
                .build());

        // 保存到数据库
        appUserHistoryService.insert(AppUserHistory.builder()
                .userId(userId)
                .imageKey(key)
                .time(TimeUtils.dateToStr(new Date(), TimeUtils.YMD_PATTERN))
                .useParam(JSON.toJSONString(request))
                .build());

        // 保存到积分记录里
        pointHistoryService.insert(PointHistory.builder()
                .userId(userId)
                .changeAmount(-1)
                .type(PointTypeEnum.MAKE.getType())
                .totalPoints(totalPoints)
                .build());

        // 返回结果
        return PhotoCreateBO.builder()
                .imageKey(key)
                .imageUrl(url)
                .build();
    }

    @Override
    public Integer deletePhoto(Integer dayBefore) {
        // 获取 x 天前的日期对象
        Date sevenDaysAgo = TimeUtils.getOffsetDate(dayBefore);
        AppUserHistory appUserHistory = AppUserHistory.builder()
                .state(StateEnum.INVALID.getCode())
                .build();
        return appUserHistoryService.deleteByLessTime(sevenDaysAgo, appUserHistory);
    }
}
