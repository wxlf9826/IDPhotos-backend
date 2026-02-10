package org.xuanfeng.idphotosbackend;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@MapperScan(basePackages = "org.xuanfeng.idphotosbackend.model.mapper")
@EnableCaching
@EnableTransactionManagement
public class IdPhotosBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdPhotosBackendApplication.class, args);
    }

}
