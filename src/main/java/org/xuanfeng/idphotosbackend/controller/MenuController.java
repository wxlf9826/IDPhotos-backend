package org.xuanfeng.idphotosbackend.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xuanfeng.idphotosbackend.model.bo.BgColorBO;
import org.xuanfeng.idphotosbackend.model.bo.SizeBO;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;
import org.xuanfeng.idphotosbackend.model.vo.BgColorVO;
import org.xuanfeng.idphotosbackend.model.vo.SizeVO;
import org.xuanfeng.idphotosbackend.service.biz.MenuBizService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private MenuBizService menuBizService;

    @GetMapping("/size/list")
    public ResponseResult<List<SizeVO>> getSizeList() {
        List<SizeBO> allSize = menuBizService.getAllSize();

        return ResponseResult.success(allSize.stream().map(e -> {
            SizeVO sizeVO = new SizeVO();
            BeanUtils.copyProperties(e, sizeVO);
            return sizeVO;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/bgColor/list")
    public ResponseResult<List<BgColorVO>> getBgColorList() {
        List<BgColorBO> allBgColor = menuBizService.getAllBgColor();

        return ResponseResult.success(allBgColor.stream().map(e -> {
            BgColorVO bgColorVO = new BgColorVO();
            BeanUtils.copyProperties(e, bgColorVO);
            return bgColorVO;
        }).collect(Collectors.toList()));
    }
}
