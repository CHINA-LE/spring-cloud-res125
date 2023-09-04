package com.yc.resfoods.controller;

import com.yc.bean.Resorder;
import com.yc.bean.Resuser;
import com.yc.resfoods.biz.ResorderBiz;
import com.yc.resfoods.model.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RequestMapping("order")
@RestController
@Slf4j
public class OrderController {
    @Autowired
    private RedisTemplate redisTemplate; // 从它里面取当前这个token代表的用户的购物车信息
    @Autowired
    private RestTemplate restTemplate; // 业务
    @Autowired
    private ResorderBiz resorderBiz; // http客户端

    @PostMapping("orderFood") //  /order/orderFood
    public Map<String, Object> orderFood(Resorder resorder,
                                         @RequestHeader("Authorization") String bearerToken){
        Map<String, Object> map = new HashMap<>();

        if (!this.redisTemplate.hasKey("cart_"+bearerToken) ||
                this.redisTemplate.opsForHash().entries("cart_"+bearerToken).size() <= 0){
            // 没有购物车数据
            map.put("code", 0);
            return map;
        }
        // 有购物车数据
        Map<String, CartItem> cart = this.redisTemplate.opsForHash().entries("cart_"+bearerToken);
        Collection<CartItem> cis = cart.values(); // CartItem(resfood对象, num, smallcount)
        // TODO: 调用业务层完成订单记录操作

        Resuser user = new Resuser();
        // TODO: 到security服务上去取userid

//        String url = "http://localhost:8011/resfood/hello";
        String url = "http://res-security/resfood/hello";
        // 添加上面url的头域
        HttpHeaders headers = new HttpHeaders(){{
            set("Authorization", bearerToken);
            set("User-Agent", "yc IE");
        }};

        // 进行请求
        ResponseEntity<Map> re =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Map>(headers), Map.class);
        Map m = re.getBody(); // m就是那个 claims
        int userid = Integer.parseInt(m.get("userid").toString());
        user.setUserid(userid);

        // 处理下订时间
        // 创建LocalDateTime对象，表示当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象，指定格式为“yyyy-MM-dd HH:mm”
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 调用format方法，将LocalDateTime对象转换为字符串
        resorder.setOrdertime(formatter.format(now));

        // 处理发货时间
        LocalDateTime deliveryTime = now.plusHours(5); // 这里直接加了五个小时 TODO: 可以添加确认发货功能
        resorder.setDeliverytime(formatter.format(deliveryTime));
        // 订单状态：0表示已下单
        resorder.setStatus(0);
        //                  订单      订单项             用户(userid)
        resorderBiz.order(resorder, new HashSet<>(cis), user);

        double total = 0;
        for (CartItem ci: cis){
            total += ci.getSmallCount();
        }
        this.redisTemplate.delete("cart_"+bearerToken);
        map.put("code", 1);
        return map;
    }

}
