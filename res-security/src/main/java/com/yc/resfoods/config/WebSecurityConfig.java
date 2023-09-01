package com.yc.resfoods.config;

import com.yc.resfoods.controller.JwtAuthenticationEntryPoint;
import com.yc.resfoods.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 这个类扩展了WebSecurityConfigurerAdapter
 * 它允许对 WebSecurityConfigurerAdapter 进行自定义
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 出错了的处理类
    @Autowired
    private UserDetailsService jwtUserDetailsService; // 业务类
    @Autowired
    private JwtRequestFilter jwtJwtRequestFilter; // 过滤类


    // 加密算法对应的类
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // 配置AuthenticationManager，使其知道从何处（jwtUserDetailsService）加载匹配凭依的用户
        // 在匹配时使用 BCryptPasswordEncoder
        // auth类组装 jwtUserDetailsService(它提供了一个根据用户名加载用户信息的方法)
        // 组装加密算法   =>   AuthenticationManager
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭 csrf 跨站请求伪造
        http.csrf().disable()
                // 不验证的特定请求
                .authorizeHttpRequests()
                .antMatchers("/resfood/code.action", "/resfood/resuser.action", "/swagger-ui/**")
                .permitAll()
                // 所有其他请求都需要经过身份验证
                .anyRequest()
                .authenticated()

                .and()
                // 验证出错则返回401
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                .and()
                // 确保我们使用无状态会话：会话不会用于存储用户状态
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 添加一个筛选器以验证每个请求的令牌
        http.addFilterBefore(jwtJwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
