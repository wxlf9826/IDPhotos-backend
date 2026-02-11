package org.xuanfeng.idphotosbackend.service;

import org.xuanfeng.idphotosbackend.model.po.PicBgColor;

import java.util.List;

public interface PicBgColorService {

    /**
     * 获取所有背景颜色
     *
     * @return 背景颜色列表
     */
    List<PicBgColor> getAllBgColor();

    /**
     * 添加背景颜色
     *
     * @param picBgColor 背景颜色对象
     * @return 是否成功
     */
    boolean add(PicBgColor picBgColor);

    /**
     * 更新背景颜色
     *
     * @param picBgColor 背景颜色对象
     * @return 是否成功
     */
    boolean update(PicBgColor picBgColor);

    /**
     * 删除背景颜色
     *
     * @param id 背景颜色ID
     * @return 是否成功
     */
    boolean delete(Long id);
}