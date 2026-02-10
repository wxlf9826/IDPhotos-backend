package org.xuanfeng.idphotosbackend.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.mapper.SystemConfigMapper;
import org.xuanfeng.idphotosbackend.model.po.SystemConfig;
import org.xuanfeng.idphotosbackend.service.SystemConfigService;
import tk.mybatis.mapper.weekend.Weekend;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemConfigServiceImpl implements SystemConfigService, ApplicationRunner {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Resource
    private RedissonClient redissonClient;

    // 所有配置存放在这一个 Redis Hash Key 中
    private static final String CONFIG_HASH_KEY = "sys_config_all";

    /**
     * 项目启动完成后执行，全量同步数据库到 Redis Hash
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info(">>> 开始初始化全量配置到 Redis Hash...");
        refreshAllConfigToCache();
        log.info(">>> 配置初始化完成");
    }

    @Override
    public Map<String, String> getAllConfig() {
        RMap<String, String> rMap = redissonClient.getMap(CONFIG_HASH_KEY);
        // 如果 Redis 中有数据，直接返回 Redis 全量数据
        if (!rMap.isEmpty()) {
            return rMap.readAllMap();
        }
        // 否则回库查询并刷新缓存
        return refreshAllConfigToCache();
    }

    @Override
    public String getConfigValue(String key) {
        Preconditions.checkArgument(StringUtils.isNotBlank(key), "key 不能为空");
        RMap<String, String> rMap = redissonClient.getMap(CONFIG_HASH_KEY);

        // 1. 优先从 Map 缓存取
        String value = rMap.get(key);
        if (value != null) {
            return value;
        }

        // 2. 缓存未命中（可能是新加的配置），回库查
        log.info("Redis Hash 命中失败，查库补位: {}", key);
        SystemConfig config = getConfigFromDb(key);
        if (config != null) {
            // 局部回写 Redis
            rMap.put(key, config.getConfigValue());
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SystemConfig systemConfig) {
        Preconditions.checkArgument(systemConfig != null, "配置对象不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(systemConfig.getConfigKey()), "configKey 不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(systemConfig.getConfigValue()), "configValue 不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(systemConfig.getDescription()), "description 不能为空");

        Date now = new Date();
        systemConfig.setState(StateEnum.VALID.getCode());
        systemConfig.setCreateTime(now);
        systemConfig.setUpdateTime(now);

        boolean success = systemConfigMapper.insertSelective(systemConfig) > 0;
        if (success) {
            syncToRedisAfterCommit(systemConfig.getConfigKey(), systemConfig.getConfigValue());
        }
        return success;
    }

    @Override
    public List<SystemConfig> getSystemConfigList() {
        Weekend<SystemConfig> weekend = new Weekend<>(SystemConfig.class);
        weekend.weekendCriteria().andEqualTo(SystemConfig::getState, StateEnum.VALID.getCode());
        return systemConfigMapper.selectByExample(weekend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SystemConfig systemConfig) {
        Preconditions.checkArgument(systemConfig != null, "配置对象不能为空");
        Preconditions.checkArgument(systemConfig.getId() != null, "id 不能为空");

        SystemConfig dbConfig = systemConfigMapper.selectByPrimaryKey(systemConfig.getId());
        Preconditions.checkArgument(dbConfig != null, "配置不存在");

        // 以传入值为准，缺失的字段回退到数据库原值
        String configKey = StringUtils.defaultIfBlank(systemConfig.getConfigKey(), dbConfig.getConfigKey());
        String configValue = systemConfig.getConfigValue() != null ? systemConfig.getConfigValue() : dbConfig.getConfigValue();
        Integer state = systemConfig.getState() != null ? systemConfig.getState() : dbConfig.getState();

        systemConfig.setUpdateTime(new Date());
        boolean success = systemConfigMapper.updateByPrimaryKeySelective(systemConfig) > 0;
        if (success) {
            if (StateEnum.VALID.getCode().equals(state)) {
                syncToRedisAfterCommit(configKey, configValue);
            } else {
                removeFromRedisAfterCommit(configKey);
            }
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Preconditions.checkArgument(id != null, "id 不能为空");

        SystemConfig dbConfig = systemConfigMapper.selectByPrimaryKey(id);
        if (dbConfig == null) {
            return false;
        }

        SystemConfig toUpdate = new SystemConfig();
        toUpdate.setId(id);
        toUpdate.setState(StateEnum.INVALID.getCode());
        toUpdate.setUpdateTime(new Date());

        boolean success = systemConfigMapper.updateByPrimaryKeySelective(toUpdate) > 0;
        if (success) {
            removeFromRedisAfterCommit(dbConfig.getConfigKey());
        }
        return success;
    }

    /**
     * 抽取：全量刷新逻辑
     */
    private Map<String, String> refreshAllConfigToCache() {
        Weekend<SystemConfig> weekend = new Weekend<>(SystemConfig.class);
        weekend.weekendCriteria().andEqualTo(SystemConfig::getState, StateEnum.VALID.getCode());
        List<SystemConfig> list = systemConfigMapper.selectByExample(weekend);

        Map<String, String> dbMap = new HashMap<>();
        list.forEach(c -> dbMap.put(c.getConfigKey(), c.getConfigValue()));

        if (!dbMap.isEmpty()) {
            RMap<String, String> rMap = redissonClient.getMap(CONFIG_HASH_KEY);
            // 这里虽然是 putAll，但 Redisson 会优化处理
            rMap.putAll(dbMap);
        }
        return dbMap;
    }

    private SystemConfig getConfigFromDb(String key) {
        Weekend<SystemConfig> weekend = new Weekend<>(SystemConfig.class);
        weekend.weekendCriteria()
                .andEqualTo(SystemConfig::getConfigKey, key)
                .andEqualTo(SystemConfig::getState, StateEnum.VALID.getCode());
        return systemConfigMapper.selectOneByExample(weekend);
    }

    private void syncToRedisAfterCommit(String key, String value) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 只更新这一个 Field，性能极高
                    redissonClient.getMap(CONFIG_HASH_KEY).put(key, value);
                    log.info("事务提交，Redis Hash 局部更新成功: {} = {}", key, value);
                }
            });
        } else {
            redissonClient.getMap(CONFIG_HASH_KEY).put(key, value);
        }
    }

    private void removeFromRedisAfterCommit(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redissonClient.getMap(CONFIG_HASH_KEY).remove(key);
                    log.info("事务提交，Redis Hash 删除配置: {}", key);
                }
            });
        } else {
            redissonClient.getMap(CONFIG_HASH_KEY).remove(key);
        }
    }
}