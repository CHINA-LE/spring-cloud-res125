package com.yc.resfoods.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yc.bean.Resfood;
import com.yc.resfoods.model.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 需要配置 Redis数据库

    // rest客户端：它利用http请求，请求 其他服务 // 服务发现
    // resTemplate : HTTPUrlConnection
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 清空购物车
     * 取出 token ，根据 键("cart_" + token) 到 redis 中进行购物车项删除
     *      注意：并不能删除这个键值对，只能删除这个键对应的 值(购物项) 即可。
     * @param bearerToken
     * @return
     */
    @RequestMapping(value = "clearAll", method = RequestMethod.GET)
    public Map<String, Object> clearAll(@RequestHeader("Authorization") String bearerToken){
        Map<String, Object> result = new HashMap<>();
        // 检测("cart_"+bearerToken)键 在Redis数据库中是否存在
        if (this.redisTemplate.hasKey("cart_" + bearerToken)){
            // 获取("cart_" + bearerToken)键的 哈希表的键集合 // 取出此人（bearerToken）所有的购物项
            Set<Object> keys = this.redisTemplate.opsForHash().keys("cart_" + bearerToken);
            // keys: 1,2    =>      "1,2"
            // 从redis的哈希表中删除上面的keys
            this.redisTemplate.opsForHash().delete("cart_" + bearerToken, keys.toArray());

            result.put("code", 1);
            result.put("obj", keys); // keys中存的是 删除的商品编号
        } else {
            result.put("code", 0);
        }
        return result;
    }

    /**
     * 获取购物车信息
     * @param bearerToken
     * @return
     */
    @RequestMapping(value = "getCartInfo", method = RequestMethod.GET)
    public Map<String, Object> getCartInfo(@RequestHeader("Authorization") String bearerToken){
        Map<String, Object> result = new HashMap<>();
        if (this.redisTemplate.hasKey("cart_" + bearerToken)){
            // entries() 取键值对（全部取到
            Map<Object, Object> cart = this.redisTemplate.opsForHash().entries("cart_" + bearerToken);
            log.info("token为：" + bearerToken + "，其购物车为：" + cart);
             result.put("code", 1);
             result.put("data", cart.values()); // keys中存的是 删除的商品编号
        } else {
            result.put("code", 0);
        }
        return result;
    }

    /**
     * 添加购物车
     * @param fid
     * @param num
     * @param bearerToken
     * @return
     */
    @RequestMapping(value = "addCart", method = RequestMethod.GET)
    public Map<String, Object> addCart(@RequestParam Integer fid,
                                       @RequestParam Integer num,
                                       @RequestHeader("Authorization") String bearerToken){
        // 返回结果：{"code":1/0, "obj":{数据对象}/"msg":错误信息}
        Map<String, Object> result = new HashMap<>();
        // TODO: 到 nacos中查找 res-food服务中的 findById  要得到菜品对象 resfood => 此技术叫服务发现，且调用服务得到结果
        Resfood rf = null;
        // 方案一：利用url地址直接访问 服务
//        String url = "http://localhost:9011/resfood/findById/" + fid;
        // 目标：发一个http请求，url如上
        //      socket -> URLConnection -> HttpClient(针对是http的请求)
        //                                            ->  restTemplate(针对 rest，是spring的封装)

        // 方案二：利用服务名，通过服务发现功能自动找到url
        String url = "http://res-food/resfood/findById/" + fid;  // feign  ->  openfeign
        Map<String, Object> resultMap = this.restTemplate.getForObject(url, Map.class);

        if ("1".equalsIgnoreCase(resultMap.get("code").toString())){
            Map map = (Map) resultMap.get("obj");
            // 如何将一个Map转为 一个Resfood对象  ->  反射
            // spring的底层对json的处理使用 jackson框架（json处理框架），这个框架有将map转为对象的方法
            ObjectMapper mapper = new ObjectMapper();
            rf = mapper.convertValue(map, Resfood.class);
        } else {
            result.put("code", 0);
            result.put("msg", "查无此商品"+fid);
            return result; // 业务问题，不是http的问题 返回200
        }
        // 从redis中查询当前用户是否有此商品的购买，如有，则加数量；如果没有，则创建一个CartItem，存数据
        // 获取购物车的信息
        CartItem ci = (CartItem) this.redisTemplate.opsForHash().get("cart_" + bearerToken, fid+"");
        // 加购物车的逻辑
        // 计算 fid 这个购物项的数量
        if (ci == null){
            ci = new CartItem();
            ci.setFood(rf);
            ci.setNum(num);
        } else {
            int newNum = ci.getNum() + num;
            ci.setNum(newNum);
        }
        // 计算金额
        if (ci.getNum() <= 0){
            this.redisTemplate.opsForHash().delete("cart_" + bearerToken, fid+"");
        } else {
//            ci.getSmallCount();
            this.redisTemplate.opsForHash().put("cart_" + bearerToken, fid+"", ci);
        }
        result.put("code", 1);
        //  token       Map
        //      fid         CartItem
        Map map = redisTemplate.opsForHash().entries("cart_" + bearerToken);  // 取出此 bearerToken所代表的人的购物车全部商品信息，给用户看
        result.put("data", map.values());
        return result;
    }
}
