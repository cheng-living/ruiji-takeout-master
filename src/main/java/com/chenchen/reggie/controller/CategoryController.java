package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.entity.Category;
import com.chenchen.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  category相关的操作
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/category）
 * */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    //自动装配
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类方法
     * PostMapping：设置访问路径为/employee/login
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("category：" + category);
        //1.调用categoryService类中的保存方法即可
        categoryService.save(category);
        //2.返回结果
        return Result.success("新增分类成功！");
    }

    /**
     * 分页查询方法
     * GetMapping：设置访问路径为/category/page
     * @param page：当前页数
     * @param pageSize：每页显示的条数
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize){
        //1.调用categoryService类中的pageList方法进行查询
        Page pageInfo = categoryService.pageList(page, pageSize);
        //2.将查询结果返回
        return Result.success(pageInfo);
    }

    /**
     * 删除分类方法
     * DeleteMapping：设置访问路径为/category
     * @param id
     * @return
     */
    @DeleteMapping
    private Result<String> delete(Long id){
        //1.调用categoryService类中的remove方法进行删除，这个方法可以判断分类是否包含菜品
        categoryService.remove(id);
        //2.返回结果
        return Result.success("分类信息删除成功！");
    }

    /**
     * 修改分类方法
     * PutMapping：设置访问路径为/category
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param category
     * @return
     */
    @PutMapping
    private Result<String> update(@RequestBody Category category){
        //1.调用categoryService类中的update方法进行修改
        categoryService.updateById(category);
        //2.返回结果
        return Result.success("分类信息修改成功！");
    }

    /**
     * 查询分类数据方法
     * GetMapping：设置访问路径为/category/list
     * @param category
     * @return
     */
    @GetMapping("/list")
    private Result<List> list(Category category) {
        //1.调用categoryService类中的categoryPageList方法进行查询即可
        List list = categoryService.categoryPageList(category);
        //2.将查询结果返回
        return Result.success(list);
    }
}
