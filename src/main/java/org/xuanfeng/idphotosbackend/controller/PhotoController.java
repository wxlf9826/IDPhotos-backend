package org.xuanfeng.idphotosbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.model.bo.PhotoCreateBO;
import org.xuanfeng.idphotosbackend.model.request.PhotoCreateRequest;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;
import org.xuanfeng.idphotosbackend.model.vo.PhotoCreateVO;
import org.xuanfeng.idphotosbackend.service.biz.PhotoBizService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Resource
    private PhotoBizService photoBizService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult<PhotoCreateVO> createPhoto(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String request) {
        Long userId = StpUtil.getLoginIdAsLong();
        PhotoCreateRequest photoCreateRequest = JSON.parseObject(request, PhotoCreateRequest.class);
        photoCreateRequest.setUserId(userId);
        PhotoCreateBO bo = photoBizService.createPhoto(file, photoCreateRequest);
        return ResponseResult.success(PhotoCreateVO.builder()
                .imageKey(bo.getImageKey())
                .imageUrl(bo.getImageUrl())
                .build());
    }
}
