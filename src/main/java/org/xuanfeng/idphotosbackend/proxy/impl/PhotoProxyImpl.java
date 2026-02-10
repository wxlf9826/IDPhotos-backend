package org.xuanfeng.idphotosbackend.proxy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.dto.IdPhotoCommonDTO;
import org.xuanfeng.idphotosbackend.model.request.*;
import org.xuanfeng.idphotosbackend.proxy.PhotoProxy;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class PhotoProxyImpl implements PhotoProxy {

    @Resource
    private OkHttpClient okHttpClient;

    @Value("${idphoto.host}")
    private String idphotoHost;

    @Override
    public IdPhotoCommonDTO idPhotoInference(IdPhotoInferenceRequest request) {
        Preconditions.checkArgument(request != null, "Request cannot be null");

        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());

        addFormDataIfPresent(builder, "height", request.getHeight());
        addFormDataIfPresent(builder, "width", request.getWidth());
        addFormDataIfPresent(builder, "human_matting_model", request.getHumanMattingModel());
        addFormDataIfPresent(builder, "face_detect_model", request.getFaceDetectModel());
        addFormDataIfPresent(builder, "hd", request.getHd());
        addFormDataIfPresent(builder, "dpi", request.getDpi());
        addFormDataIfPresent(builder, "face_align", request.getFaceAlign());
        addFormDataIfPresent(builder, "whitening_strength", request.getWhiteningStrength());
        addFormDataIfPresent(builder, "brightness_strength", request.getBrightnessStrength());
        addFormDataIfPresent(builder, "contrast_strength", request.getContrastStrength());
        addFormDataIfPresent(builder, "sharpen_strength", request.getSharpenStrength());
        addFormDataIfPresent(builder, "saturation_strength", request.getSaturationStrength());
        addFormDataIfPresent(builder, "head_measure_ratio", request.getHeadMeasureRatio());
        addFormDataIfPresent(builder, "head_height_ratio", request.getHeadHeightRatio());
        addFormDataIfPresent(builder, "top_distance_max", request.getHeadHeightRatio());
        addFormDataIfPresent(builder, "top_distance_min", request.getTopDistanceMin());

        return postAndParse("/idphoto", builder.build());
    }

    @Override
    public IdPhotoCommonDTO humanMatting(HumanMattingRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());
        addFormDataIfPresent(builder, "human_matting_model", request.getHumanMattingModel());
        addFormDataIfPresent(builder, "dpi", request.getDpi());

        return postAndParse("/human_matting", builder.build());
    }

    @Override
    public IdPhotoCommonDTO addBackground(AddBackgroundRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());
        addFormDataIfPresent(builder, "color", request.getColor());
        addFormDataIfPresent(builder, "kb", request.getKb());
        addFormDataIfPresent(builder, "dpi", request.getDpi());
        addFormDataIfPresent(builder, "render", request.getRender());

        return postAndParse("/add_background", builder.build());
    }

    @Override
    public IdPhotoCommonDTO generateLayout(GenerateLayoutRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());
        addFormDataIfPresent(builder, "height", request.getHeight());
        addFormDataIfPresent(builder, "width", request.getWidth());
        addFormDataIfPresent(builder, "kb", request.getKb());
        addFormDataIfPresent(builder, "dpi", request.getDpi());

        return postAndParse("/generate_layout_photos", builder.build());
    }

    @Override
    public IdPhotoCommonDTO watermark(WatermarkRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());
        addFormDataIfPresent(builder, "text", request.getText());
        addFormDataIfPresent(builder, "size", request.getSize());
        addFormDataIfPresent(builder, "opacity", request.getOpacity());
        addFormDataIfPresent(builder, "angle", request.getAngle());
        addFormDataIfPresent(builder, "color", request.getColor());
        addFormDataIfPresent(builder, "space", request.getSpace());
        addFormDataIfPresent(builder, "kb", request.getKb());
        addFormDataIfPresent(builder, "dpi", request.getDpi());

        return postAndParse("/watermark", builder.build());
    }

    @Override
    public IdPhotoCommonDTO setKb(SetKbRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());
        addFormDataIfPresent(builder, "dpi", request.getDpi());
        addFormDataIfPresent(builder, "kb", request.getKb());

        return postAndParse("/set_kb", builder.build());
    }

    @Override
    public IdPhotoCommonDTO idPhotoCrop(IdPhotoCropRequest request) {
        MultipartBody.Builder builder = createBaseBuilder(request.getInputImage(), request.getInputImageBase64());

        // 尺寸参数
        addFormDataIfPresent(builder, "height", request.getHeight());
        addFormDataIfPresent(builder, "width", request.getWidth());

        // 模型与配置
        addFormDataIfPresent(builder, "face_detect_model", request.getFaceDetectModel());
        addFormDataIfPresent(builder, "hd", request.getHd());
        addFormDataIfPresent(builder, "dpi", request.getDpi());

        // 比例控制
        addFormDataIfPresent(builder, "head_measure_ratio", request.getHeadMeasureRatio());
        addFormDataIfPresent(builder, "head_height_ratio", request.getHeadHeightRatio());
        addFormDataIfPresent(builder, "top_distance_max", request.getTopDistanceMax());
        addFormDataIfPresent(builder, "top_distance_min", request.getTopDistanceMin());

        return postAndParse("/idphoto_crop", builder.build());
    }

    /**
     * 初始化 MultipartBuilder 并注入图片
     *
     * @param file   文件
     * @param base64 base64文件
     * @return 表单
     */
    private MultipartBody.Builder createBaseBuilder(MultipartFile file, String base64) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        try {
            if (file != null && !file.isEmpty()) {
                RequestBody fileBody = RequestBody.create(file.getBytes(),
                        MediaType.parse(file.getContentType() != null ? file.getContentType() : "image/jpeg"));
                builder.addFormDataPart("input_image", file.getOriginalFilename(), fileBody);
            } else if (StringUtils.isNotBlank(base64)) {
                builder.addFormDataPart("input_image_base64", base64);
            } else {
                throw new CommonException(ResultCodeEnum.PARAM_ERROR, "No image provided");
            }
        } catch (IOException e) {
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "Image data read error");
        }
        return builder;
    }

    /**
     * 仅在值不为空时添加表单字段
     *
     * @param builder 表单
     * @param key     表单key
     * @param value   表单value
     */
    private void addFormDataIfPresent(MultipartBody.Builder builder, String key, Object value) {
        if (value != null) {
            String str = String.valueOf(value).trim();
            if (!str.isEmpty()) {
                builder.addFormDataPart(key, str);
            }
        }
    }

    /**
     * 发送 POST 请求并统一解析
     *
     * @param path        路径
     * @param requestBody 请求体
     * @return 结果
     */
    private IdPhotoCommonDTO postAndParse(String path, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(idphotoHost + path)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() == 400){
                    if (body.contains("Part exceeded maximum size of")) {
                        throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "图片大小不能超过1024KB，可在旁边开启");
                    }
                }
                log.error("API Request Failed. Path: {}, Status: {}, Body: {}", path, response.code(), body);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "未检测到人脸或者人脸过多");
            }
            IdPhotoCommonDTO dto = JSON.parseObject(body, IdPhotoCommonDTO.class);

            // 业务逻辑校验
            if (dto == null || Boolean.FALSE.equals(dto.getStatus())) {
                log.error("IdPhoto business fail. Path: {}, Request: {}, Response: {}", path, JSON.toJSONString(request), body);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "未检测到人脸或者人脸过多");
            }

            return dto;
        } catch (IOException e) {
            log.error("Photo API Network Error. Path: {}", path, e);
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "网络连接失败");
        }
    }
}
