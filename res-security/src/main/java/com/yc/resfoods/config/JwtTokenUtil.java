package com.yc.resfoods.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yc.bean.Resuser;
import com.yc.resfoods.dao.ResuserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {
    // token 的有效期
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    // 注入密匙
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private ResuserDao resuserDao;

    /**
     * 根据用户信息生成token
     * 用户信息根据Spring security框架中的UserDetail中拿
     * UserDetail：由Spring security 提供的用户类：包装登录用户信息
     */
    public String generateToken(UserDetails userDetails){
        // 存载荷。七个默认值：sub, iss, ...

        // 准备一个空荷载claims，用于存储生成的key和value键值对（下面是存储生成token的时间和用户名）
        Map<String,Object> claims = new HashMap<>();
        // 自己增加的载荷
        claims.put("username", userDetails.getUsername());
        claims.put("pwd", userDetails.getPassword());
        QueryWrapper<Resuser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", userDetails.getUsername());
        wrapper.eq("pwd", userDetails.getPassword());
        Resuser user = this.resuserDao.selectOne(wrapper);
        claims.put("userid", user.getUserid());
        claims.put("email", user.getEmail());

        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * 根据荷载生成token
     * 主要是通过Jwts把荷载、失效时间、以及密钥加密生成token
     *
     * 创建 token 步骤
     * 1. 定义令牌的声明，如 Issuer，Expiration，Subject 和 ID
     * 2. 使用 HS512 算法和密钥对 JWT 进行签名
     * 3. 根据 JWT Compact Serialization(http://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1) 将 JWT 压缩为 URL 安全字符串
     */
    private String doGenerateToken(Map<String,Object> claims, String subject){ // subject 是用户名username
        //有了荷载claims就可以通过Jwts生成token,方式如下：
        return Jwts.builder()
                // 有效载荷 （可以自己加
                .setClaims(claims)//把荷载存储到里面
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // 设置失效时间
                // 签名运算
                .signWith(SignatureAlgorithm.HS512, secret)
                // 压缩
                .compact();
    }


    // 获取 jwt token 中的用户名
    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }


    // 从 jwt token 中获取过期日期
    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration); // 从Claims类中获取Expiration
    }


    // 判断token是否已经失效
    private boolean isTokenExpired(String token) {
        //先获取之前设置的token的失效时间
        final Date expireDate = getExpirationDateFromToken(token);
        return expireDate.before(new Date()); //判断下，当前时间是都已经在expireDate之后
    }


    /**
     * 从 token 中获取 claim ...
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){ // Function<Claims, T>配合 T（泛型）增强 getClaimFromToken 的泛用性 （主要是解决了getClaimFromToken的返回值不同问题）
        final Claims claims = getAllClaimsFromToken(token); // 通过 token 解析出 Claims类
        return claimsResolver.apply(claims); // 通过传入的 Claims::getExpiration 形成的 Function<Claims, T> 来规定应用的函数
    }

    // 利用 secret 密钥从 token 中获取信息
    public Claims getAllClaimsFromToken(String token){
        // JWT依赖工具类
        return Jwts
                .parser() // 构造解析器
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // 检验 token
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        // 用户相同 且 token不过期
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
