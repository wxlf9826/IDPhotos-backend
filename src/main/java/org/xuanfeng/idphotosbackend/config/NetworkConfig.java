package org.xuanfeng.idphotosbackend.config;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class NetworkConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        // 1. 定义调度器：控制并发请求数量
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(200);      // 最大总并发请求数（默认64）
        dispatcher.setMaxRequestsPerHost(40); // 每个域名的最大并发数（默认5）- 防止被对方防爬机制封锁或单点压力过大

        // 2. 定义连接池：复用 TCP 连接，减少握手时间
        // 参数：最大空闲连接数, 连接空闲存活时间, 时间单位
        ConnectionPool connectionPool = new ConnectionPool(50, 5, TimeUnit.MINUTES);

        // 3. 构建客户端
        return new OkHttpClient.Builder()
                // --- 超时配置 ---
                .connectTimeout(5, TimeUnit.SECONDS) // 建立连接超时
                .readTimeout(20, TimeUnit.SECONDS)    // 读取数据超时
                .writeTimeout(20, TimeUnit.SECONDS)   // 发送数据超时
                .callTimeout(60, TimeUnit.SECONDS)    // 整个调用链路的超时时间（含解析、DNS、重试等）

                // --- 资源配置 ---
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)

                // --- 策略配置 ---
                .retryOnConnectionFailure(true) // 网络抖动时自动重试
                .followRedirects(true)          // 自动重定向

                // --- 拦截器（建议选配） ---
                // .addInterceptor(new HttpLoggingInterceptor()) // 日志拦截器

                .build();
    }
}