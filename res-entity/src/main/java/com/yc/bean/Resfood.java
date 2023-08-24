package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Resfood implements Serializable { // 序列化
    @TableId(type = IdType.AUTO) // 当前键为mysql表的主键，提示自增
    private Integer fid;
    private String fname;
    private Double normprice;
    private Double realprice;
    private String detail;
    private String fphoto;

    // 增加一个属性：点赞数。 这个属性的值是redis提供的，不是数据库
    @TableField(select = false) // mybatis-plus 在做操作时忽略
    private Long praise;
}

/**
 * 创建实体类注意点：
 *      @TableId 表明此为主键
 *      尽量不用基本类型，要用包装类
 *      实体类要 implements Serializable，因为此类的对象都有序列化需求
 */

