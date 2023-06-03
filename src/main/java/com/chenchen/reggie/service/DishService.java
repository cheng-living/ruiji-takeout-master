package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.dto.DishDto;
import com.chenchen.reggie.entity.Dish;

import java.util.List;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface DishService extends IService<Dish> {
    //新增一个方法，同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);
    //新增一个方法，查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);
    //新增一个方法，用于修改菜品信息
    public void updateWithFlavor(DishDto dishDto);
    //新增一个方法，用于删除菜品信息
    public boolean deleteWithFlavor(Long[] ids);
    //新增一个方法，用于分页查询
    public Page pageList(int page, int pageSize, String name);
    //新增一个方法，用于根据ID查询当前菜品分类下的菜品
    public List<DishDto> categoryOfDish(Dish dish);
}
