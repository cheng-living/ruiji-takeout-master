package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.entity.Category;

import java.util.List;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface CategoryService extends IService<Category> {
    //这个方法用于删除分类（要判断是否关联套餐）
    public void remove(Long id);
    //分页查询方法
    public Page pageList(int page, int pageSize);
    //查询分类数据方法
    public List categoryPageList(Category category);

}
