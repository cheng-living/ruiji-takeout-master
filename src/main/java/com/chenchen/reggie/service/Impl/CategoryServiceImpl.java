package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.common.CustomException;
import com.chenchen.reggie.entity.Category;
import com.chenchen.reggie.entity.Dish;
import com.chenchen.reggie.entity.Setmeal;
import com.chenchen.reggie.mapper.CategoryMapper;
import com.chenchen.reggie.service.CategoryService;
import com.chenchen.reggie.service.DishService;
import com.chenchen.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * CategoryService：可以调用这个接口的常用方法
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService{
    //自动装配
    @Autowired
    private DishService dishService;
    //自动装配
    @Autowired
    private SetmealService setmealService;
    //自动装配
    @Autowired
    private CategoryService categoryService;

    /**
     * 这个方法用于删除分类，需要判断是否关联了套餐，菜品
     * @param id
     */
    @Override
    public void remove(Long id) {
        //1.查询分类是否关联了菜品
        //①创建查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②dishLambdaQueryWrapper.eq：指定一个相等的条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //③统计dishLambdaQueryWrapper对象中的count
        int count1 = (int) dishService.count(dishLambdaQueryWrapper);
        //④判断统计的条数
        if (count1 > 0){
            //如果大于0则表示有关联，调用通用异常类中方法的返回一个异常
            throw new CustomException("当前分类下还有关联的菜品，删除失败！");
        }
        //2.查询套餐是否关联了菜品
        //①创建查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②setmealLambdaQueryWrapper.eq：指定一个相等的条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //③setmealLambdaQueryWrapper
        int count2 = (int) setmealService.count(setmealLambdaQueryWrapper);
        //④判断统计的条数
        if (count2 > 0){
            //如果大于0则表示有关联，调用通用异常类中方法的返回一个异常
            throw new CustomException("当前套餐下还有关联的菜品，删除失败！");
        }
        //3.两个都没有关联，执行删除，调用继承的类中的方法进行删除
        super.removeById(id);
    }

    /**
     * 分页查询方法
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page pageList(int page, int pageSize) {
        //1.创建分页构造器,参数1：当前页数，参数2：每页显示的条数
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //3.添加排序条件（根据sort字段进行排序）
        //Category::getSort：Category中的getSort属性
        queryWrapper.orderByAsc(Category::getSort);
        //4.调用employeeService类中的page方法进行查询
        //参数1：分页信息的对象
        //参数2：查询条件的封装
        categoryService.page(pageInfo,queryWrapper);
        //5.返回结果
        return pageInfo;
    }

    /**
     * 查询分类数据方法
     * @param category
     * @return
     */
    @Override
    public List categoryPageList(Category category) {
        //1.创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //2.添加条件
        //①eq：相同查询
        //②参数1：查询的条件（是否要进行查询），当name不为空时才进行模糊查询
        //③参数2：表示要过滤的目标对象为Category中的Type属性
        //④参数3：要过滤的值
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //3.添加排序条件（根据sort字段进行排序）
        //Category::getSort：Category中的getSort属性
        //如果排序一样就根据修改时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        //4.调用categoryService类中的list方法进行查询
        List<Category> list = categoryService.list(queryWrapper);
        //5.返回结果
        return list;
    }


}
