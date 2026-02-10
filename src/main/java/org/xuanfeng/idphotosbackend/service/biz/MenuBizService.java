package org.xuanfeng.idphotosbackend.service.biz;

import org.xuanfeng.idphotosbackend.model.bo.SizeBO;

import java.util.List;

public interface MenuBizService {

    /**
     * 获取所有尺寸
     *
     * @return 尺寸列表
     */
    List<SizeBO> getAllSize();
}
