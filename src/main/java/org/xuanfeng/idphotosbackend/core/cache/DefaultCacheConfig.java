package org.xuanfeng.idphotosbackend.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xuanfeng.idphotosbackend.constant.CacheConstant;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableCaching(order = 1, proxyTargetClass = true)
public class DefaultCacheConfig {

	@Resource
	private RedissonClient redissonClient;

	@Bean(name = CacheConstant.REDIS_CACHE_MANAGER)
	public CacheManager cacheManager() {
		log.info("初始化 RedissonCacheManager...");
		return new RedissonSpringCacheManager(redissonClient, getCacheConfigMap());
	}

	private Map<String, CacheConfig> getCacheConfigMap() {
		Map<String, CacheConfig> configs = new HashMap<>();

		configs.put(CacheConstant.ONE_SECOND_CACHE, createConfig(1));
		configs.put(CacheConstant.TEN_SECONDS_CACHE, createConfig(10));
		configs.put(CacheConstant.THIRTY_SECONDS_CACHE, createConfig(30));
		configs.put(CacheConstant.ONE_MINUTE_CACHE, createConfig(60));
		configs.put(CacheConstant.TEN_MINUTES_CACHE, createConfig(10 * 60));
		configs.put(CacheConstant.THIRTY_MINUTES_CACHE, createConfig(30 * 60));
		configs.put(CacheConstant.ONE_DAY_CACHE, createConfig(24 * 60 * 60));

		return configs;
	}

	private CacheConfig createConfig(long second) {
		// Redisson 的 TTL 单位是毫秒
		CacheConfig config = new CacheConfig();
		config.setTTL(second * 1000);
		config.setMaxIdleTime(second * 1000);
		return config;
	}
}