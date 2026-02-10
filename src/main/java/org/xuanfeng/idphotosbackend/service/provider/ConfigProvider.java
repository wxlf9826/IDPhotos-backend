package org.xuanfeng.idphotosbackend.service.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.xuanfeng.idphotosbackend.constant.ConfigConstants;
import org.xuanfeng.idphotosbackend.service.SystemConfigService;

import javax.annotation.Resource;

@Component
public class ConfigProvider {

    @Resource
    private SystemConfigService configService;

    /**
     * 获取管理员用户名
     *
     * @return 管理员用户名
     */
    public String getAdminUsername() {
        return configService.getConfigValue(ConfigConstants.ADMIN_USERNAME);
    }

    /**
     * 获取管理员密码
     *
     * @return 管理员密码
     */
    public String getAdminPassword() {
        return configService.getConfigValue(ConfigConstants.ADMIN_PASSWORD);
    }

    /**
     * 获取appId
     *
     * @return appId
     */
    public String getAppId() {
        return configService.getConfigValue(ConfigConstants.APP_ID);
    }

    /**
     * 获取appSecret
     *
     * @return appSecret
     */
    public String getAppSecret() {
        return configService.getConfigValue(ConfigConstants.APP_SECRET);
    }

    /**
     * 获取adUnitId
     *
     * @return adUnitId
     */
    public String getAdUnitId() {
        return configService.getConfigValue(ConfigConstants.AD_UNIT_ID);
    }

    /**
     * 获取默认积分
     *
     * @return 默认积分
     */
    public Integer getDefaultPoints() {
        String val = configService.getConfigValue(ConfigConstants.DEFAULT_POINTS);
        return val != null ? Integer.parseInt(val) : 0;
    }

    /**
     * 是否开启图片安全检测
     * 数据库中配置值为 "true" 时开启，其他值或未配置时默认不开启
     *
     * @return true-开启 false-关闭
     */
    public boolean isImageSecurityCheckEnabled() {
        String val = configService.getConfigValue(ConfigConstants.IMAGE_SECURITY_CHECK_ENABLED);
        return "true".equalsIgnoreCase(val);
    }

}
