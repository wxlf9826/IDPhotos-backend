package org.xuanfeng.idphotosbackend.service.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.constant.CommonConstants;
import org.xuanfeng.idphotosbackend.core.enums.PointTypeEnum;
import org.xuanfeng.idphotosbackend.core.enums.RenderEnum;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.bo.ImageSecurityCheckBO;
import org.xuanfeng.idphotosbackend.model.bo.PhotoCreateBO;
import org.xuanfeng.idphotosbackend.model.dto.IdPhotoCommonDTO;
import org.xuanfeng.idphotosbackend.model.dto.SightengineCheckDTO;
import org.xuanfeng.idphotosbackend.model.po.AppUser;
import org.xuanfeng.idphotosbackend.model.po.AppUserHistory;
import org.xuanfeng.idphotosbackend.model.po.PointHistory;
import org.xuanfeng.idphotosbackend.model.request.*;
import org.xuanfeng.idphotosbackend.proxy.PhotoProxy;
import org.xuanfeng.idphotosbackend.proxy.SightengineProxy;
import org.xuanfeng.idphotosbackend.service.AppUserHistoryService;
import org.xuanfeng.idphotosbackend.service.AppUserService;
import org.xuanfeng.idphotosbackend.service.PointHistoryService;
import org.xuanfeng.idphotosbackend.service.S3Service;
import org.xuanfeng.idphotosbackend.service.biz.PhotoBizService;
import org.xuanfeng.idphotosbackend.service.provider.ConfigProvider;
import org.xuanfeng.idphotosbackend.utils.PhotoUtils;
import org.xuanfeng.idphotosbackend.utils.TimeUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private SightengineProxy sightengineProxy;

    @Resource
    private AppUserHistoryService appUserHistoryService;

    @Resource
    private PointHistoryService pointHistoryService;

    @Resource
    private ConfigProvider configProvider;

    /**
     * 安全检测阈值 - 色情内容
     */
    @Value("${sightengine.threshold.nudity:0.5}")
    private Double nudityThreshold;

    /**
     * 安全检测阈值 - 武器
     */
    @Value("${sightengine.threshold.weapon:0.5}")
    private Double weaponThreshold;

    /**
     * 安全检测阈值 - 毒品
     */
    @Value("${sightengine.threshold.recreational-drug:0.5}")
    private Double recreationalDrugThreshold;

    /**
     * 安全检测阈值 - 医疗
     */
    @Value("${sightengine.threshold.medical:0.5}")
    private Double medicalThreshold;

    /**
     * 安全检测阈值 - 酒精
     */
    @Value("${sightengine.threshold.alcohol:0.5}")
    private Double alcoholThreshold;

    /**
     * 安全检测阈值 - 冒犯性内容
     */
    @Value("${sightengine.threshold.offensive:0.5}")
    private Double offensiveThreshold;

    /**
     * 安全检测阈值 - 血腥
     */
    @Value("${sightengine.threshold.gore:0.5}")
    private Double goreThreshold;

    /**
     * 安全检测阈值 - 烟草
     */
    @Value("${sightengine.threshold.tobacco:0.5}")
    private Double tobaccoThreshold;

    /**
     * 安全检测阈值 - 暴力
     */
    @Value("${sightengine.threshold.violence:0.5}")
    private Double violenceThreshold;

    /**
     * 安全检测阈值 - 自残
     */
    @Value("${sightengine.threshold.self-harm:0.5}")
    private Double selfHarmThreshold;

    /**
     * 安全检测阈值 - 赌博
     */
    @Value("${sightengine.threshold.gambling:0.5}")
    private Double gamblingThreshold;

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
        String key = String.format(CommonConstants.S3_PHOTO_KEY, TimeUtils.getTodayStr(), UUID.randomUUID());

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
                .sizeName(request.getSizeName())
                .heightPx(request.getHeightPx())
                .widthPx(request.getWidthPx())
                .bgColor(request.getBgColor())
                .renderMode(request.getRenderMode())
                .beautyConfig(JSON.toJSONString(request.getBeautyConfig()))
                .watermarkConfig(JSON.toJSONString(request.getWatermarkConfig()))
                .otherConfig(JSON.toJSONString(request.getOtherConfig()))
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
    public ImageSecurityCheckBO imageSecurityCheck(MultipartFile file) {
        Preconditions.checkArgument(file != null && !file.isEmpty(), "file can not be empty");

        String key = String.format(CommonConstants.S3_SECURITY_KEY, TimeUtils.getTodayStr(), UUID.randomUUID());
        String contentType = StringUtils.defaultIfBlank(file.getContentType(), "image/jpeg");

        try (InputStream inputStream = file.getInputStream()) {
            s3Service.uploadFile(inputStream, key, contentType);
        } catch (IOException ioException) {
            log.error("imageSecurityCheck read file ioException", ioException);
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "读取上传图片失败");
        }

        String imageUrl = s3Service.getPresignedUrl(key);

        // 判断是否开启图片安全检测
        if (!configProvider.isImageSecurityCheckEnabled()) {
            log.info("imageSecurityCheck is disabled, skip security check, imageKey:{}", key);
            return ImageSecurityCheckBO.builder()
                    .imageKey(key)
                    .imageUrl(imageUrl)
                    .passed(true)
                    .rejectReason(null)
                    .build();
        }

        // 调用 sightengine 接口获取检测结果 DTO
        SightengineCheckDTO checkResult = sightengineProxy.imageSecurityCheck(ImageSecurityCheckRequest.builder()
                .imageUrl(imageUrl)
                .build());

        // 在 biz 层进行安全规则判断
        List<String> rejectReasons = new ArrayList<>();
        checkSecurityRules(checkResult, rejectReasons);

        boolean passed = rejectReasons.isEmpty();
        String rejectReason = passed ? null : String.join("; ", rejectReasons);

        if (!passed) {
            log.warn("imageSecurityCheck not passed, imageKey:{}, reasons:{}", key, rejectReason);
        }

        return ImageSecurityCheckBO.builder()
                .imageKey(key)
                .imageUrl(imageUrl)
                .passed(passed)
                .rejectReason(rejectReason)
                .build();
    }

    /**
     * 安全规则检查，根据各项检测结果和阈值判断是否通过
     *
     * @param dto           sightengine检测结果
     * @param rejectReasons 不通过原因列表
     */
    private void checkSecurityRules(SightengineCheckDTO dto, List<String> rejectReasons) {
        if (dto == null) {
            rejectReasons.add("检测结果为空");
            return;
        }

        // 1. 色情内容检测
        if (dto.getNudity() != null) {
            SightengineCheckDTO.Nudity nudity = dto.getNudity();
            double maxNudityScore = getMaxValue(
                    nudity.getSexualActivity(),
                    nudity.getSexualDisplay(),
                    nudity.getErotica(),
                    nudity.getVerySuggestive()
            );
            if (maxNudityScore >= nudityThreshold) {
                rejectReasons.add(String.format("色情内容检测不通过(%.3f >= %.3f)", maxNudityScore, nudityThreshold));
            }
        }

        // 2. 武器检测
        if (dto.getWeapon() != null && dto.getWeapon().getClasses() != null) {
            SightengineCheckDTO.WeaponClasses weaponClasses = dto.getWeapon().getClasses();
            double maxWeaponScore = getMaxValue(
                    weaponClasses.getFirearm(),
                    weaponClasses.getFirearmGesture(),
                    weaponClasses.getKnife()
            );
            if (maxWeaponScore >= weaponThreshold) {
                rejectReasons.add(String.format("武器检测不通过(%.3f >= %.3f)", maxWeaponScore, weaponThreshold));
            }
        }

        // 3. 毒品检测
        if (dto.getRecreationalDrug() != null) {
            double drugProb = defaultValue(dto.getRecreationalDrug().getProb());
            if (drugProb >= recreationalDrugThreshold) {
                rejectReasons.add(String.format("毒品检测不通过(%.3f >= %.3f)", drugProb, recreationalDrugThreshold));
            }
        }

        // 4. 医疗检测
        if (dto.getMedical() != null) {
            double medicalProb = defaultValue(dto.getMedical().getProb());
            if (medicalProb >= medicalThreshold) {
                rejectReasons.add(String.format("医疗内容检测不通过(%.3f >= %.3f)", medicalProb, medicalThreshold));
            }
        }

        // 5. 酒精检测
        if (dto.getAlcohol() != null) {
            double alcoholProb = defaultValue(dto.getAlcohol().getProb());
            if (alcoholProb >= alcoholThreshold) {
                rejectReasons.add(String.format("酒精内容检测不通过(%.3f >= %.3f)", alcoholProb, alcoholThreshold));
            }
        }

        // 6. 冒犯性内容检测
        if (dto.getOffensive() != null) {
            SightengineCheckDTO.Offensive offensive = dto.getOffensive();
            double maxOffensiveScore = getMaxValue(
                    offensive.getNazi(),
                    offensive.getAsianSwastika(),
                    offensive.getConfederate(),
                    offensive.getSupremacist(),
                    offensive.getTerrorist(),
                    offensive.getMiddleFinger()
            );
            if (maxOffensiveScore >= offensiveThreshold) {
                rejectReasons.add(String.format("冒犯性内容检测不通过(%.3f >= %.3f)", maxOffensiveScore, offensiveThreshold));
            }
        }

        // 7. 血腥内容检测
        if (dto.getGore() != null) {
            double goreProb = defaultValue(dto.getGore().getProb());
            if (goreProb >= goreThreshold) {
                rejectReasons.add(String.format("血腥内容检测不通过(%.3f >= %.3f)", goreProb, goreThreshold));
            }
        }

        // 8. 烟草检测
        if (dto.getTobacco() != null) {
            double tobaccoProb = defaultValue(dto.getTobacco().getProb());
            if (tobaccoProb >= tobaccoThreshold) {
                rejectReasons.add(String.format("烟草内容检测不通过(%.3f >= %.3f)", tobaccoProb, tobaccoThreshold));
            }
        }

        // 9. 暴力检测
        if (dto.getViolence() != null) {
            double violenceProb = defaultValue(dto.getViolence().getProb());
            if (violenceProb >= violenceThreshold) {
                rejectReasons.add(String.format("暴力内容检测不通过(%.3f >= %.3f)", violenceProb, violenceThreshold));
            }
        }

        // 10. 自残检测
        if (dto.getSelfHarm() != null) {
            double selfHarmProb = defaultValue(dto.getSelfHarm().getProb());
            if (selfHarmProb >= selfHarmThreshold) {
                rejectReasons.add(String.format("自残内容检测不通过(%.3f >= %.3f)", selfHarmProb, selfHarmThreshold));
            }
        }

        // 11. 赌博检测
        if (dto.getGambling() != null) {
            double gamblingProb = defaultValue(dto.getGambling().getProb());
            if (gamblingProb >= gamblingThreshold) {
                rejectReasons.add(String.format("赌博内容检测不通过(%.3f >= %.3f)", gamblingProb, gamblingThreshold));
            }
        }
    }

    /**
     * 获取多个值中的最大值
     */
    private double getMaxValue(Double... values) {
        double max = 0.0;
        for (Double value : values) {
            if (value != null && value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 空值默认为0
     */
    private double defaultValue(Double value) {
        return value != null ? value : 0.0;
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
