package org.xuanfeng.idphotosbackend.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xuanfeng.idphotosbackend.model.response.ResponseResult;

@Configuration
@Slf4j
public class SaTokenConfigure {

    @Autowired
    private ObjectMapper objectMapper;

    // 定义统一的放行白名单，方便维护
    private static final String[] EXCLUDE_PATHS = {
            "/favicon.ico", "/user/login", "/menu/**", "/photo/**",
            "/item/itemList", "/admin/login", "/admin/checkLogin",
            "/admin/okLogin", "/otherApi/exploreCount", "/api/getWebGlow", "/api/getvideoUnit"
    };

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 1. 物理放行：直接不走过滤器逻辑
                .addInclude("/**")
                .addExclude(EXCLUDE_PATHS)

                // 2. 认证与鉴权
                .setAuth(obj -> {
                    // 登录校验：拦截所有（由于外层 addExclude 已过滤白名单，这里只需关注需要登录的）
                    SaRouter.match("/**").check(r -> StpUtil.checkLogin());

                    // 管理员权限校验：拦截 /admin/**，排除已在白名单的登录接口
                    SaRouter.match("/admin/**").check(r -> {
                        // 使用 Sa-Token 推荐的角色/权限校验方式，或者保留你的前缀判断
                        String loginId = StpUtil.getLoginIdAsString();
                        if (!loginId.startsWith("admin_")) {
                            throw new RuntimeException("非管理员账号无法访问");
                        }
                    });
                })

                // 3. 异常处理
                .setError(e -> {
                    log.warn("Sa-Token过滤器拦截到异常: {}", e.getMessage());
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");

                    // 统一返回 401 或自定义错误码
                    ResponseResult<Object> result = ResponseResult.error(401, e.getMessage());

                    try {
                        return objectMapper.writeValueAsString(result);
                    } catch (Exception ex) {
                        return "{\"code\":500, \"message\":\"Internal Server Error\"}";
                    }
                })

                // 4. 前置处理（CORS 与 安全响应头）
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()
                            .setServer("zjzWx")
                            .setHeader("X-Frame-Options", "DENY")
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            .setHeader("X-Content-Type-Options", "nosniff")
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Max-Age", "3600");

                    // OPTIONS 预检请求直接返回
                    SaRouter.match(SaHttpMethod.OPTIONS).back();
                });
    }
}