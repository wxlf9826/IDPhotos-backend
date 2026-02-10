package org.xuanfeng.idphotosbackend.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.model.bo.SizeBO;
import org.xuanfeng.idphotosbackend.model.po.PicSize;
import org.xuanfeng.idphotosbackend.service.biz.MenuBizService;
import org.xuanfeng.idphotosbackend.service.PicSizeService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuBizServiceImpl implements MenuBizService {

    @Resource
    private PicSizeService picSizeService;

    @Override
    public List<SizeBO> getAllSize() {
        List<PicSize> sizeList = picSizeService.getAllSize();

        return sizeList.stream().map(e -> SizeBO.builder()
                .name(e.getName())
                .heightPx(e.getHeightPx())
                .widthPx(e.getWidthPx())
                .build()).collect(Collectors.toList());
    }
}
