package org.xuanfeng.idphotosbackend.service.biz;

import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.model.bo.PhotoCreateBO;
import org.xuanfeng.idphotosbackend.model.request.PhotoCreateRequest;

public interface PhotoBizService {

    /**
     * 生成图片
     *
     * @param file    文件
     * @param request 请求
     * @return 结果
     */
    PhotoCreateBO createPhoto(MultipartFile file, PhotoCreateRequest request);

    /**
     * 清除x天以前的图片
     *
     * @return 数量
     */
    Integer deletePhoto(Integer dayBefore);
}
