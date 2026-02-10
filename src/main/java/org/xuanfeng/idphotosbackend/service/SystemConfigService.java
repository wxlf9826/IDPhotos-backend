package org.xuanfeng.idphotosbackend.service;

import org.xuanfeng.idphotosbackend.model.po.SystemConfig;
import org.xuanfeng.idphotosbackend.model.bo.SystemConfigBO;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {

    /**
     * 获取全部配置 Map形式
     *
     * @return 配置键值对
     */
    Map<String, String> getAllConfig();

    /**
     * 获取全部配置 List形式
     *
     * @return 配置列表
     */
    List<SystemConfig> getSystemConfigList();

    /**
     * 添加配置
     *
     * @param systemConfig 配置对象
     * @return 是否成功
     */
    boolean add(SystemConfig systemConfig);

    /**
     * 更新配置 (根据ID更新)
     *
     * @param systemConfig 配置对象
     * @return 是否成功
     */
    boolean update(SystemConfig systemConfig);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 获取单个配置
     *
     * @param key 配置key
     * @return value
     */
    String getConfigValue(String key);
}
