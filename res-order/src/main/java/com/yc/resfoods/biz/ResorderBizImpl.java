package com.yc.resfoods.biz;

import com.yc.bean.Resorder;
import com.yc.bean.Resorderitem;
import com.yc.bean.Resuser;
import com.yc.resfoods.dao.ResorderDao;
import com.yc.resfoods.dao.ResorderitemDao;
import com.yc.resfoods.model.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(
        propagation = Propagation.REQUIRED, // 事务设置。REQUIRED：必须事务环境下运行 （SUPPORTED则是有事务就在事务下运行，没有就不在
        isolation = Isolation.DEFAULT,
        timeout = 2000,
        readOnly = false,
        rollbackFor = RuntimeException.class
)
@Slf4j
public class ResorderBizImpl implements ResorderBiz{
    @Autowired
    private ResorderDao resorderDao;

    @Autowired
    private ResorderitemDao resorderitemDao;

    @Override
    public int order(Resorder resorder, Set<CartItem> cartItems, Resuser resuser) {

        resorder.setUserid(resuser.getUserid());
        this.resorderDao.insert(resorder); // 插入成功后，会有 roid存在这个对象 resorder中

//        System.out.println("=========================================================" + resorder);

        // 一个resorder下可能有多个 resorderItem对象
        for (CartItem ci: cartItems){
            Resorderitem roi = new Resorderitem();

            roi.setRoid(resorder.getRoid());
            roi.setFid(ci.getFood().getFid());
            roi.setDealprice(ci.getFood().getRealprice());
            roi.setNum(ci.getNum());
            this.resorderitemDao.insert(roi);
        }
        return 1;
    }
}
