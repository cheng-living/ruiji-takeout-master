package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.dto.SetmealDto;
import com.chenchen.reggie.entity.Setmeal;

import java.util.List;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface SetmealService extends IService<Setmeal> {
    //保存套餐方法
    public void saveWithDish(SetmealDto setmealDto);
    //删除套餐方法
    public void removeWithDish(List<Long> ids);
    //修改套餐方法
    public void updateWithDish(SetmealDto setmealDto);
    //新增一个方法，用于数据回显
    public SetmealDto getByIdWithDish(Long id);
    //分页查询方法
    public Page pageList(int page, int pageSize,String name);
    //新增一个方法，用于根据ID查询当前套餐分类下的菜品
    public List<Setmeal> setmealOfDish(Setmeal setmeal);
}
