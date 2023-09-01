package com.yc.resfoods.filters;

import com.yc.resfoods.config.JwtTokenUtil;
import com.yc.resfoods.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtRequestFilter 扩展了 Spring Web FilterOncePerRequestFilter类
 * 对于任何传入的请求，都会执行此Filter类。它检查请求是否具有有效的JWT令牌
 * 如果它具有有效的JWT令牌，则在上下文中设置身份验证，以指定当前用户已通过身份验证
 */
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService; // 验证用户身份的业务类
    @Autowired
    private JwtTokenUtil jwtTokenUtil; // token工具类（生成token，验证token）

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 因为客户端会将 token 以 Authorization: Bearer token 的形式 在header中传播
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // Jwt token 以 “Bearer token值”的形式提供，移除Bearer，得到 token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")){
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e){
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e){
                System.out.println("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
        // 判断用户名
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // 根据用户名获取信息（权限）
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            // 验证 token 中的 userDetails 是否有效
            // jwtToken是用户传过来的      userDetails是根据用户名从数据库查询出来的，标准的
            // 重新用 HS256( userDetails, secret )进行计算得到String，再与 jwtToken 作比较，看是否相等
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 在上下文中设置身份验证后，我们指定当前用户已通过身份验证，所以它通过了Spring安全配置成功
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
