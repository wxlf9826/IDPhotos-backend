package org.xuanfeng.idphotosbackend.proxy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.enums.ResultCodeEnum;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.dto.SightengineCheckDTO;
import org.xuanfeng.idphotosbackend.model.request.ImageSecurityCheckRequest;
import org.xuanfeng.idphotosbackend.proxy.SightengineProxy;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class SightengineProxyImpl implements SightengineProxy {

    @Resource
    private OkHttpClient okHttpClient;

    @Value("${sightengine.host}")
    private String sightengineHost;

    @Value("${sightengine.models}")
    private String sightengineModels;

    @Value("${sightengine.api-user}")
    private String sightengineApiUser;

    @Value("${sightengine.api-secret}")
    private String sightengineApiSecret;

    @Override
    public SightengineCheckDTO imageSecurityCheck(ImageSecurityCheckRequest request) {
        Preconditions.checkArgument(request != null, "request can not be null");
        Preconditions.checkArgument(StringUtils.isNotBlank(request.getImageUrl()), "request.imageUrl can not be blank");

        HttpUrl url = HttpUrl.parse(sightengineHost + "/1.0/check.json").newBuilder()
                .addQueryParameter("models", sightengineModels)
                .addQueryParameter("api_user", sightengineApiUser)
                .addQueryParameter("api_secret", sightengineApiSecret)
                .addQueryParameter("url", request.getImageUrl())
                .build();

        Request httpRequest = new Request.Builder().url(url).get().build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (response.isSuccessful()) {
                SightengineCheckDTO result = JSON.parseObject(body, SightengineCheckDTO.class);
                if (result == null || !"success".equalsIgnoreCase(result.getStatus())) {
                    final String errorMsg = String.format("sightengine imageSecurityCheck fail, request:%s, response:%s", JSON.toJSONString(request), body);
                    log.error(errorMsg);
                    throw new CommonException(ResultCodeEnum.IMAGE_SECURITY_CHECK_FAIL, "图片安全检测失败");
                }
                log.info("sightengine imageSecurityCheck success, request:{}", JSON.toJSONString(request));
                return result;
            } else {
                final String errorMsg = String.format("sightengine imageSecurityCheck fail, request:%s, responseCode:%s, response:%s", JSON.toJSONString(request), response.code(), body);
                log.error(errorMsg);
                throw new CommonException(ResultCodeEnum.PHOTO_ERROR, "图片安全检测失败");
            }
        } catch (IOException ioException) {
            log.error("sightengine imageSecurityCheck ioException", ioException);
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "图片安全检测接口调用失败");
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            log.error("sightengine imageSecurityCheck Exception", e);
            throw new CommonException(ResultCodeEnum.SYSTEM_ERROR, "图片安全检测接口调用失败");
        }
    }
}
