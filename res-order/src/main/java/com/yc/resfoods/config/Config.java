package com.yc.resfoods.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class Config {

//    @LoadBalanced // 开启RestTemplate对象的负载均衡功能
//    @Bean
//    public RestTemplate restTemplate(){
//        return new RestTemplate();
//    }
}
