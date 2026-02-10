package org.xuanfeng.idphotosbackend.service;

import org.xuanfeng.idphotosbackend.model.po.PicSize;

import java.util.List;

public interface PicSizeService {

    /**
     * 获取所有尺寸
     *
     * @return 尺寸列表
     */
    List<PicSize> getAllSize();

    /**
     * 添加尺寸
     *
     * @param picSize 尺寸对象
     * @return 是否成功
     */
    boolean add(PicSize picSize);

    /**
     * 更新尺寸
     *
     * @param picSize 尺寸对象
     * @return 是否成功
     */
    boolean update(PicSize picSize);

    /**
     * 删除尺寸
     *
     * @param id 尺寸ID
     * @return 是否成功
     */
    boolean delete(Long id);
}
