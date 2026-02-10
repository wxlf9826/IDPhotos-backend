package org.xuanfeng.idphotosbackend.proxy;

import org.xuanfeng.idphotosbackend.model.dto.IdPhotoCommonDTO;
import org.xuanfeng.idphotosbackend.model.request.*;

public interface PhotoProxy {

    /**
     * 证件照智能制作接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO idPhotoInference(IdPhotoInferenceRequest request);

    /**
     * 人像抠图接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO humanMatting(HumanMattingRequest request);

    /**
     * 透明图像添加纯色背景接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO addBackground(AddBackgroundRequest request);

    /**
     * 六寸排版照生成接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO generateLayout(GenerateLayoutRequest request);

    /**
     * 透明图像添加水印接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO watermark(WatermarkRequest request);

    /**
     * 设置照片KB值接口(RGB图)
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO setKb(SetKbRequest request);

    /**
     * 证件照智能裁剪接口
     *
     * @param request 请求参数
     * @return 结果
     */
    IdPhotoCommonDTO idPhotoCrop(IdPhotoCropRequest request);

}
