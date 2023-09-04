package com.yc.resfoods.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @program: cloud_res_parent
 * @description: 自定义负载均衡策略:只访问一个服务
 * @author: zy
 * @create: 2023-04-05 22:24
 */
@Slf4j
public class MyOnlyOneLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    //服务列表
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider; // 服务实际列表的提供器

    public MyOnlyOneLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 创建服务实例列表提供器
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        // 获取服务实例
        return supplier.get(request).next().map( (serviceInstances) -> {
            return processInstanceResponse(supplier, serviceInstances);
        });
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        // 按照算法取服务实例
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    // 核心算法
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        log.info("自定义负载");
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        //固定访问第1个服务
        ServiceInstance instance = instances.get(0);

        return new DefaultResponse(instance);
    }
}
