package com.yc.resfoods.biz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yc.bean.Resfood;

import java.util.List;

public interface ResfoodBiz {
    public List<Resfood> findAll();

    public Resfood findById(Integer fid);

    public Page<Resfood> findByPage(int pageno, int pagesize, String sortby, String sort);

}
