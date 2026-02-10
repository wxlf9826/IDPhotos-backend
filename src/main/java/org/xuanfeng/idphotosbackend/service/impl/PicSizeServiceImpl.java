package org.xuanfeng.idphotosbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.constant.CacheConstant;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.mapper.PicSizeMapper;
import org.xuanfeng.idphotosbackend.model.po.PicSize;
import org.xuanfeng.idphotosbackend.service.PicSizeService;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PicSizeServiceImpl implements PicSizeService {

    @Resource
    private PicSizeMapper picSizeMapper;

    @Cacheable(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allSize'")
    @Override
    public List<PicSize> getAllSize() {
        Weekend<PicSize> weekend = new Weekend<>(PicSize.class);
        WeekendCriteria<PicSize, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(PicSize::getState, StateEnum.VALID.getCode());

        return picSizeMapper.selectByExample(weekend);
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allSize'")
    @Override
    public boolean add(PicSize picSize) {
        picSize.setState(StateEnum.VALID.getCode());
        picSize.setCreateTime(new Date());
        picSize.setUpdateTime(new Date());
        return picSizeMapper.insert(picSize) > 0;
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allSize'")
    @Override
    public boolean update(PicSize picSize) {
        picSize.setUpdateTime(new Date());
        return picSizeMapper.updateByPrimaryKeySelective(picSize) > 0;
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allSize'")
    @Override
    public boolean delete(Long id) {
        PicSize picSize = new PicSize();
        picSize.setId(id);
        picSize.setState(StateEnum.INVALID.getCode());
        picSize.setUpdateTime(new Date());
        return picSizeMapper.updateByPrimaryKeySelective(picSize) > 0;
    }
}
