package com.yc.resfoods.model;

import com.yc.bean.Resfood;

import java.io.Serializable;

public class CartItem implements Serializable {

    private Resfood food; // 里面有 realprice 菜品的价格
    private Integer num;
    private Double smallCount;

    @Override
    public String toString() {
        return "CartItem{" +
                "food=" + food +
                ", num=" + num +
                ", smallCount=" + smallCount +
                '}';
    }

    public Resfood getFood() {
        return food;
    }

    public void setFood(Resfood food) {
        this.food = food;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getSmallCount() {
        if (food != null){
            smallCount = this.food.getRealprice() * this.num;
        }
        return smallCount;
    }

    public void setSmallCount(Double smallCount) {
        this.smallCount = smallCount;
    }
}
