package com.yc.resfoods.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// 对每个服务分别指定负载均衡策略
@LoadBalancerClients(
        value = {
                @LoadBalancerClient(value = "res-food", configuration = MyOnlyOneConfig.class), // RoundRobinConfig轮询/MyOnlyOneConfig自定义
                @LoadBalancerClient(value = "res-security", configuration = RandomConfig.class) // 随机
        }, defaultConfiguration = LoadBalancerClientConfiguration.class // 其他未指定的服务，默认轮询
)
@Configuration
public class LoadBalancerConfig {

        @LoadBalanced // 开启RestTemplate对象的负载均衡功能
        @Bean
        public RestTemplate restTemplate(){
                return new RestTemplate();
        }
}
