package com.yc.resfoods.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Controller控制器，IOC， Advice: aop中的增强
@Order(-100000)
// AOP技术
public class CustomerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Map<String, Object> handleRuntimeException(RuntimeException exception){
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "runtimeException occured");
        return map;
    }
}
