package org.xuanfeng.idphotosbackend.service.biz;

import org.xuanfeng.idphotosbackend.model.bo.BgColorBO;
import org.xuanfeng.idphotosbackend.model.bo.SizeBO;

import java.util.List;

public interface MenuBizService {

    /**
     * 获取所有尺寸
     *
     * @return 尺寸列表
     */
    List<SizeBO> getAllSize();

    /**
     * 获取所有背景颜色
     *
     * @return 背景颜色列表
     */
    List<BgColorBO> getAllBgColor();
}
