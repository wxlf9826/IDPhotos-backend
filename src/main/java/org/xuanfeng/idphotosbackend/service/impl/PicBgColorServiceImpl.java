package org.xuanfeng.idphotosbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.constant.CacheConstant;
import org.xuanfeng.idphotosbackend.core.enums.StateEnum;
import org.xuanfeng.idphotosbackend.model.mapper.PicBgColorMapper;
import org.xuanfeng.idphotosbackend.model.po.PicBgColor;
import org.xuanfeng.idphotosbackend.service.PicBgColorService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PicBgColorServiceImpl implements PicBgColorService {

    @Resource
    private PicBgColorMapper picBgColorMapper;

    @Cacheable(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allBgColor'")
    @Override
    public List<PicBgColor> getAllBgColor() {
        Weekend<PicBgColor> weekend = new Weekend<>(PicBgColor.class);
        WeekendCriteria<PicBgColor, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(PicBgColor::getState, StateEnum.VALID.getCode());

        return picBgColorMapper.selectByExample(weekend);
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allBgColor'")
    @Override
    public boolean add(PicBgColor picBgColor) {
        picBgColor.setState(StateEnum.VALID.getCode());
        picBgColor.setCreateTime(new Date());
        picBgColor.setUpdateTime(new Date());
        return picBgColorMapper.insert(picBgColor) > 0;
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allBgColor'")
    @Override
    public boolean update(PicBgColor picBgColor) {
        picBgColor.setUpdateTime(new Date());
        return picBgColorMapper.updateByPrimaryKeySelective(picBgColor) > 0;
    }

    @CacheEvict(cacheManager = CacheConstant.REDIS_CACHE_MANAGER, value = CacheConstant.ONE_DAY_CACHE, key = "'allBgColor'")
    @Override
    public boolean delete(Long id) {
        PicBgColor picBgColor = new PicBgColor();
        picBgColor.setId(id);
        picBgColor.setState(StateEnum.INVALID.getCode());
        picBgColor.setUpdateTime(new Date());
        return picBgColorMapper.updateByPrimaryKeySelective(picBgColor) > 0;
    }
}