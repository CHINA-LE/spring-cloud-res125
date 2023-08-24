package com.yc.resfoods.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yc.bean.Resfood;
import com.yc.resfoods.dao.ResfoodDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.annotation.W3CDomHandler;
import java.util.List;

// Propagation.SUPPORTS  表示支持事务环境，即事务可有可无
// readOnly = true 表示这是只读事务
@Service
@Transactional(
        propagation = Propagation.SUPPORTS,
        isolation = Isolation.DEFAULT,
        timeout = 2000,
        readOnly = true,
        rollbackFor = RuntimeException.class
)
@Slf4j
public class ResfoodBizImpl implements ResfoodBiz{
    @Autowired
    private ResfoodDao resfoodDao;

    @Override
    public List<Resfood> findAll() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByDesc("fid"); // 根据fid列的值降序排序
        return this.resfoodDao.selectList(wrapper);
    }

    @Override
    public Resfood findById(Integer fid) {
        return this.resfoodDao.selectById(fid);
    }

    @Override
    public Page<Resfood> findByPage(int pageno, int pagesize, String sortby, String sort) {
        QueryWrapper wrapper = new QueryWrapper();
        if ("asc".equalsIgnoreCase(sort)){
            wrapper.orderByAsc(sortby);
        }else {
            wrapper.orderByDesc(sortby);
        }
        // Page 是 mybatis-plus 提供的分页组件，拼接 sql 的分页语句
        // 因为不同的数据库使用的分页语句不同。
        Page<Resfood> page = new Page<>(pageno, pagesize);
        // 执行分页查询   可以采用 mp 提供的分页方法 selectPage() 来完成分页  wrapper可以完成排序条件 通过Page中pageno，pagesize就知道如何分页
        Page<Resfood> resfoodPage = this.resfoodDao.selectPage(page, wrapper);
        log.info("总记录数 = " + resfoodPage.getTotal());
        log.info("总页数 = " + resfoodPage.getPages());
        log.info("当前页码 = " + resfoodPage.getCurrent());
        return resfoodPage;
    }
}
