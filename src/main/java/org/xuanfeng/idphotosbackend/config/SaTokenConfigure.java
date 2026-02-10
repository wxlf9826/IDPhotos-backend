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

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

                // 1. 指定 拦截路由 与 放行路由
                .addInclude("/**")
                .addExclude("/favicon.ico", "/user/login", "/menu/**", "/photo/create")

                // 2. 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，除了上面 addExclude 放行的
                    SaRouter.match("/**")
                            .notMatch( "/item/itemList", "/admin/login", "/admin/checkLogin", "/admin/okLogin", "/otherApi/exploreCount", "/api/getWebGlow", "/api/getvideoUnit")
                            .check(r -> StpUtil.checkLogin());

                    // 管理员权限校验 -- 拦截所有 /admin/** 路由，除了登录接口
                    SaRouter.match("/admin/**")
                            .notMatch("/admin/login", "/admin/checkLogin")
                            .check(r -> {
                                String loginId = StpUtil.getLoginIdAsString();
                                if (!loginId.startsWith("admin_")) {
                                    throw new RuntimeException("非管理员账号无法访问");
                                }
                            });

                })

                // 3. 异常处理函数：每次认证函数报错时执行

                .setError(e -> {
                    log.warn("Sa-Token过滤器拦截到异常: {}", e.getMessage());
                    // 1. 设置响应头为 JSON 格式
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");

                    // 2. 构造返回对象
                    ResponseResult<Object> result = ResponseResult.error(401, e.getMessage());

                    // 3. 手动转为 JSON 字符串返回
                    try {
                        return objectMapper.writeValueAsString(result);
                    } catch (Exception ex) {
                        return "{\"code\":500, \"message\":\"JSON转换异常\"}";
                    }
                })

                // 4. 前置函数：在认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("zjzWx")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "DENY")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Max-Age", "3600");
                    // 如果是预检请求，则直接返回（Options请求不进行鉴权）
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> System.out.println("-------- OPTIONS预检请求，直接返回 --------"))
                            .back();
                })
                ;
    }


}
