package com.yc.resfoods.service;

import com.yc.resfoods.dao.ResuserDao;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yc.bean.Resuser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private ResuserDao resuserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 用于构建数据库查询条件
        QueryWrapper<Resuser> wrapper = new QueryWrapper<>(); // ~
        // 设置查询条件，根据用户名来查询匹配的用户信息。
        wrapper.eq("username", username);
        Resuser resuser = resuserDao.selectOne(wrapper);
        if (resuser != null){
            // Spring security 的 User类 （厉害！啥都包了                // 这个是权限，现在传入的是空的权限列表
            return new User(resuser.getUsername(), resuser.getPwd(), new ArrayList<>());
        } else {
            throw  new UsernameNotFoundException("User not found with username: " + username);
        }
    }

}
