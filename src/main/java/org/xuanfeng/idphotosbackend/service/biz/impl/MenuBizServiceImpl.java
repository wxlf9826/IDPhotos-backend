package org.xuanfeng.idphotosbackend.service.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.model.bo.BgColorBO;
import org.xuanfeng.idphotosbackend.model.bo.SizeBO;
import org.xuanfeng.idphotosbackend.model.po.PicBgColor;
import org.xuanfeng.idphotosbackend.model.po.PicSize;
import org.xuanfeng.idphotosbackend.service.biz.MenuBizService;
import org.xuanfeng.idphotosbackend.service.PicBgColorService;
import org.xuanfeng.idphotosbackend.service.PicSizeService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuBizServiceImpl implements MenuBizService {

    @Resource
    private PicSizeService picSizeService;

    @Resource
    private PicBgColorService picBgColorService;

    @Override
    public List<SizeBO> getAllSize() {
        List<PicSize> sizeList = picSizeService.getAllSize();

        return sizeList.stream().map(e -> SizeBO.builder()
                .name(e.getName())
                .heightPx(e.getHeightPx())
                .widthPx(e.getWidthPx())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<BgColorBO> getAllBgColor() {
        List<PicBgColor> bgColorList = picBgColorService.getAllBgColor();
        return bgColorList.stream().map(e -> BgColorBO.builder()
                .id(e.getId())
                .name(e.getName())
                .colorValue(e.getColorValue())
                .build()).collect(Collectors.toList());
    }
}
