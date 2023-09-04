package com.yc.resfoods.biz;

import com.yc.bean.Resorder;
import com.yc.bean.Resuser;
import com.yc.resfoods.model.CartItem;

import java.util.Set;

/**
 * 保存订单：事务操作
 */
public interface ResorderBiz {

    /**
     * 下单
     * @param resorder：订单相关信息   address tel ps status
     * @param cartItems: 订单项信息   CartItem < roid  fid  dealprice  num >
     * @param resuser: 用户信息       userid
     * @return
     */
    public int order(Resorder resorder, Set<CartItem> cartItems, Resuser resuser);
}
