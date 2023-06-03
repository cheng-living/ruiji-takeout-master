package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.common.CustomException;
import com.chenchen.reggie.dto.SetmealDto;
import com.chenchen.reggie.entity.Category;
import com.chenchen.reggie.entity.Setmeal;
import com.chenchen.reggie.entity.SetmealDish;
import com.chenchen.reggie.mapper.SetmealMapper;
import com.chenchen.reggie.service.CategoryService;
import com.chenchen.reggie.service.SetmealDishService;
import com.chenchen.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * SetmealService：可以调用这个接口的常用方法
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    //自动装配
    @Autowired
    private SetmealDishService setmealDishService;
    //自动装配
    @Autowired
    private SetmealService setmealService;
    //自动装配
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐的信息方法
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //1.保存套餐的基本信息
        this.save(setmealDto);
        //2.将套餐的ID保存
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            //获取套餐的ID
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //3.保存套餐的信息和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐方法
     * Transactional：事务处理，保证全部成功或者全部失败
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //1.查询套餐的状态，判断是否处于停售状态，如果不是则不能进行删除
        //①创建条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②添加条件：在ids里面的ID
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        //③添加条件：状态等于1的不可以进行删除
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //④统计查询的条数
        long count = this.count(lambdaQueryWrapper);
        //⑤判断不可以删除的条数是否大于0
        if (count > 0){
            //如果大于0，则抛出一个异常，提示不可以删除
            throw new CustomException("套餐有处于售卖的菜品，删除失败！");
        }
        //可以删除
        this.removeByIds(ids);
        //2.删除关联表中的数据
        //①创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //②添加条件：根据ids获取SetmealDish实体中的SetmealId属性进行删除
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        //③删除
        setmealDishService.remove(queryWrapper);

    }

    /**
     * Transactional：保证数据操作全部失败或者成功
     * 套餐更新方法
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //1.更新基本信息
        this.updateById(setmealDto);
        //2.清除当前套餐的菜品信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //3.重新添加菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //4.将菜品信息保存
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐信息回显方法
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //1.查询套餐的基本信息
        Setmeal setmeal = this.getById(id);
        //2.查询套餐对应的菜品
        //①创建条件构造器
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②匹配ID
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        //③调用setmealDishService类中的list方法进行查询
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //3.将套餐信息和套餐下的菜品信息拷贝/添加到setmealDto对象中
        //①创建setmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //②拷贝套餐的基本信息
        BeanUtils.copyProperties(setmeal,setmealDto);
        //③添加套餐下的菜品信息
        setmealDto.setSetmealDishes(list);
        //4.返回封装好后的dishDto对象
        return setmealDto;
    }

    /**
     * 分页查询方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page pageList(int page, int pageSize, String name) {
        //1.创建分页构造器,参数1：当前页数，参数2：每页显示的条数
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //3.添加过滤条件（根据输入的名字进行查询）
        //①like：模糊查询，与eq不同的是eq要相同才可以，like是包含就可以
        //②参数1：查询的条件（是否要进行查询），当name不为空时才进行模糊查询
        //③参数2：表示要过滤的目标对象为Setmeal中的name属性
        //④参数3：要过滤的值
        queryWrapper.like(name != null,Setmeal::getName,name);
        //4.添加排序条件
        //Setmeal::getUpdateTime：Setmeal中的UpdateTime属性
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //5.调用employeeService类中的page方法进行查询
        //参数1：分页信息的对象
        //参数2：查询条件的封装
        setmealService.page(pageInfo,queryWrapper);
        //6.将pageInfo对象拷贝一份，因为这个对象会缺少一个属性(setmealName)，这个属性用于展示分类名字
        //①创建一个新对象
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //②拷贝
        //参数1：源对象
        //参数2：目标对象
        //参数3：不需要拷贝的属性
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        //7.获取records属性下的setmealName属性
        //①获取records属性
        List<Setmeal> records = pageInfo.getRecords();
        //②读取records下的setmealName属性，并且赋给一个新的集合
        List<SetmealDto> list = records.stream().map((item) -> {
            //(1)创建一个setmealDto象
            SetmealDto setmealDto = new SetmealDto();
            //(2)将遍历到的每一个item对象都赋值到setmealDto
            BeanUtils.copyProperties(item, setmealDto);
            //(3)获取分类ID
            Long categoryId = item.getCategoryId();
            //(4)根据分类ID获取分类的名字
            Category category = categoryService.getById(categoryId);
            //(5)判断分类对象是否为空
            if (category != null){
                //不为空时获取套餐的名字
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            //(6)将setmealDto对象返回
            return setmealDto;
        }).collect(Collectors.toList());
        //8.将查询到的分类名字赋值给setmealDtoPage对象
        setmealDtoPage.setRecords(list);
        //9.返回结果
        return setmealDtoPage;
    }

    /**
     * 查询当前套餐下的菜品
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> setmealOfDish(Setmeal setmeal) {
        //1.创建条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.添加过滤条件
        //①ID要相同的菜品分类
        //参数1：查询的条件（是否要进行查询），当CategoryId不为空时才进行模糊查询
        //参数2：表示要过滤的目标对象为Setmeal中的CategoryId属性
        //参数3：要过滤的值
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //②菜品要处于起售的状态才进行显示
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //3.添加排序添加
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //4.调用setmealService类中的list方法进行查询
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        //5.返回结果
        return list;
    }
}
