package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resorder implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer roid;

    private Integer userid; // 收货人
    private String address; // 地址
    private String tel; // 电话

    private String ordertime; // 下订时间
    private String deliverytime; // 发货时间 // 时间当成String处理

    private String ps; // 留言
    private Integer status;
}
