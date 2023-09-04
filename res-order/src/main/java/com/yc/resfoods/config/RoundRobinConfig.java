package com.yc.resfoods.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RoundRobinConfig {

    // 配置成轮询策略 // 官网找的
//    @Bean
//    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
//                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
//        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
//        // 随机RandomLoadBalancer：轮询RoundRobinLoadBalancer
//        return new RoundRobinLoadBalancer(
//                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
//                name
//        );
//    }

    // 老师提供的
    @Bean
//    @ConditionalOnMissingBean // 没有这个bean才进行创建
    public ReactorServiceInstanceLoadBalancer roundRobinReactorServiceInstanceLoadBalancer(Environment environment,
                                                                                           LoadBalancerClientFactory loadBalancerClientFactory){
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        // 随机RandomLoadBalancer：轮询RoundRobinLoadBalancer
        return new RoundRobinLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name
        );
    }
}
