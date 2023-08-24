package com.yc.resfoods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.yc.resfoods.dao")
@EnableDiscoveryClient  // 启动服务注册发现的客户端：httpclient/postman
@EnableSwagger2
@EnableOpenApi
public class ResFoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResFoodsApplication.class, args);
    }
}
