package com.yc;

import com.yc.resfoods.SecurityApp;
import com.yc.resfoods.config.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityApp.class})
public class TestToken extends TestCase {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void test1(){
        // 假设 一个用户 ud登录成功，要生成token
        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }
            @Override
            public String getPassword() {
                return "$2a$10$4AlRcNGs3dTRR1rPi0a19.5YE0yb0JglFY4pUoyByauxdqENZlxEG";
            }
            @Override
            public String getUsername() {
                return "a";
            }
            @Override
            public boolean isAccountNonExpired() {
                return false;
            }
            @Override
            public boolean isAccountNonLocked() {
                return false;
            }
            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }
            @Override
            public boolean isEnabled() {
                return true;
            }
        };

        String token = jwtTokenUtil.generateToken(userDetails);
        System.out.println(token);

//        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);
//        System.out.println(usernameFromToken);
//
//        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
//        System.out.println(claims);

    }
}
