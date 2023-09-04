package com.yc.resfoods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yc.resfoods.dao") // 该注解会自动创建一个接口的代理对象，该代理对象负责将方法调用转发给MyBatis底层的SQL映射语句。
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
