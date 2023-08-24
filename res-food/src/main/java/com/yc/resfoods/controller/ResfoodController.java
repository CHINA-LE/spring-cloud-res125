package com.yc.resfoods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yc.bean.Resfood;
import com.yc.model.PageBean;
import com.yc.resfoods.biz.ResfoodBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
// 表示必须要加前缀
@RequestMapping("resfood") // http://localhost:port/resfood 后面接方法上的路径
@Slf4j
@Api(tags = "菜品管理")
public class ResfoodController {
    @Autowired
    private ResfoodBiz resfoodBiz;

    @GetMapping("findById/{fid}") //{fid} 路径参数
    @ApiOperation(value = "根据菜品编号查询操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "菜品号", required = true)
    })
    public Map<String, Object> findById(@PathVariable Integer fid){
        Map<String, Object> map = new HashMap<>();
        Resfood rf = null;
        try {
            rf = this.resfoodBiz.findById(fid);
        }catch (Exception ex){
            ex.printStackTrace();
            map.put("code", 0);
            map.put("msg", ex.getCause());
            return map;
        }
        map.put("code", 1);
        map.put("obj", rf);
        return map;
    }

    @GetMapping("findAll") // http://localhost:9001/resfood/findAll
    @ApiOperation(value = "查询所有菜品")
    public Map<String, Object> findAll(){
        Map<String, Object> map = new HashMap<>();
        List<Resfood> list = null;
        try {
            list = this.resfoodBiz.findAll();
        }catch (Exception ex){
            ex.printStackTrace();
            map.put("code", 0);
            map.put("msg", ex.getCause());
            return map;
        }
        map.put("code", 1);
        map.put("obj", list);
        return map;
    }

    @RequestMapping(value = "findByPage", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "分页查询操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageno", value = "页号", required = true),
            @ApiImplicitParam(name = "pagesize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "sortby", value = "排序列", required = true),
            @ApiImplicitParam(name = "sort", value = "排列方法", required = true)
    })
    public Map<String, Object> findByPage(@RequestParam int pageno, @RequestParam int pagesize,
                                          @RequestParam String sortby, @RequestParam String sort){
        Map<String, Object> map = new HashMap<>(); // 返回的 json字符串
        Page<Resfood> page = null;
        try {
            page = this.resfoodBiz.findByPage(pageno, pagesize, sortby, sort);
        }catch (Exception ex){
            ex.printStackTrace();
            map.put("code", 0);
            map.put("msg", ex.getCause());
            return map;
        }
        map.put("code", 1);
        // 包装在 web model  用于在 web界面/app界面上显示结果
        PageBean pageBean = new PageBean();
        pageBean.setPageno(pageno);
        pageBean.setPagesize(pagesize);
        pageBean.setSort(sort);
        pageBean.setSortby(sortby);
        pageBean.setTotal(page.getTotal());
        pageBean.setDataset(page.getRecords());
        // 其他的分页数据
        // 计算总页数
        long totalPages = page.getTotal() % pageBean.getPagesize()==0 ?
                page.getTotal()/pageBean.getPagesize()  :  page.getTotal()/page.getSize()+1;
        pageBean.setTotalpages((int)totalPages);
        // 上一页页号的计算
        if (pageBean.getPageno() <= 1){
            pageBean.setPre(1);
        }else {
            pageBean.setPre(pageBean.getPageno() - 1);
        }
        // 下一页页号的计算
        if (pageBean.getPageno() == totalPages){
            pageBean.setPre((int)totalPages);
        }else {
            pageBean.setNext(pageBean.getPageno() + 1);
        }
        map.put("data", pageBean);
        return map; // map 中有两个数据，一个code，一个data
    }
}
