package org.xuanfeng.idphotosbackend.proxy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xuanfeng.idphotosbackend.core.exception.CommonException;
import org.xuanfeng.idphotosbackend.model.dto.OpenIdDTO;
import org.xuanfeng.idphotosbackend.model.request.OpenIdRequest;
import org.xuanfeng.idphotosbackend.proxy.WxProxy;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class WxProxyImpl implements WxProxy {

    @Resource
    private OkHttpClient okHttpClient;

    @Value("${wx.host}")
    private String WxHost;

    @Override
    public OpenIdDTO getOpenId(OpenIdRequest openIdRequest) {
        Preconditions.checkArgument(openIdRequest != null, "openIdRequest is null");

        OpenIdDTO openIdDTO;
        // 1. 处理 URL 参数
        HttpUrl url = HttpUrl.parse(WxHost + "/sns/jscode2session").newBuilder()
                .addQueryParameter("appid", openIdRequest.getAppId())
                .addQueryParameter("secret", openIdRequest.getSecret())
                .addQueryParameter("js_code", openIdRequest.getJsCode())
                .addQueryParameter("grant_type", openIdRequest.getGrantType())
                .build();

        // 2. 构建 Request
        Request request = new Request.Builder().url(url).get().build();

        // 3. 执行并获取响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                openIdDTO = JSON.parseObject(body, OpenIdDTO.class);

                if (openIdDTO.getErrCode() != null && openIdDTO.getErrCode() != 0) {
                    final String errorMsg = String.format("wxProxy getOpenId fail, errCode not 0, request:%s, response:%s", JSON.toJSONString(openIdRequest), JSON.toJSONString(openIdDTO));
                    log.error(errorMsg);
                    throw new CommonException(errorMsg);
                }
            } else {
                final String errorMsg = String.format("wxProxy getOpenId fail, request:%s, response:%s", JSON.toJSONString(openIdRequest), JSON.toJSONString(response));
                log.error(errorMsg);
                throw new CommonException(errorMsg);
            }
        } catch (IOException ioException) {
            log.error("wxProxy getOpenId ioException", ioException);
            throw new CommonException("wxProxy getOpenId ioException");
        } catch (Exception e) {
            log.error("wxProxy getOpenId Exception", e);
            throw new CommonException("wxProxy getOpenId Exception");
        }

        log.info("wxProxy getOpenId success, request:{}, response:{}", JSON.toJSONString(openIdRequest), JSON.toJSONString(openIdDTO));
        return openIdDTO;

    }
}
