package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.dto.DishDto;
import com.chenchen.reggie.entity.Category;
import com.chenchen.reggie.entity.Dish;
import com.chenchen.reggie.entity.DishFlavor;
import com.chenchen.reggie.mapper.DishMapper;
import com.chenchen.reggie.service.CategoryService;
import com.chenchen.reggie.service.DishFlavorService;
import com.chenchen.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * DishService：可以调用这个接口的常用方法
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    //自动装配
    @Autowired
    private DishFlavorService dishFlavorService;
    //自动装配
    @Autowired
    private DishService dishService;
    //自动装配
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品方法，并且将菜品信息保存
     * Transactional：事务处理，保证全部成功或者全部失败
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //1.将菜品的基本信息保存
        this.save(dishDto);
        //2.将菜品的分类保存
        //①获取当前的ID
        Long id = dishDto.getId();
        //②将ID添加到集合里面
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //③将菜品的分类信息批量保存
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品信息和对应的口味信息方法
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //1.查询菜品的基本信息
        Dish byId = this.getById(id);
        //2.查询菜品对应的口味信息
        //①创建条件构造器
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②匹配ID
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,byId.getId());
        //③调用dishFlavorService类中的list方法进行查询
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        //3.将菜品信息和菜品对应的口味信息拷贝/添加到DishDto对象中
        //①创建DishDto对象
        DishDto dishDto = new DishDto();
        //②拷贝菜品的基本信息
        BeanUtils.copyProperties(byId,dishDto);
        //③添加菜品对应口味的信息
        dishDto.setFlavors(list);
        //4.返回封装好后的dishDto对象
        return dishDto;
    }

    /**
     * 修改菜品方法，并且将菜品信息保存
     * Transactional：事务处理，保证全部成功或者全部失败
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //1.更新基本信息
        this.updateById(dishDto);
        //2.清除当前菜品的口味信息
        LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishDtoLambdaQueryWrapper);
        //3.重新添加口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //4.将口味信息保存
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品方法
     * Transactional：事务处理，保证全部成功或者全部失败
     * @param ids
     */
    @Transactional
    @Override
    public boolean deleteWithFlavor(Long ids[]) {
        //1.循坏获取每一个id
        for (Long id:ids) {
            //2.判断当前菜品是否处于停售状态，如果是才进行删除
            Dish byId = dishService.getById(id);
            if (byId.getStatus() != 0){
                //如果不是则返回失败
                return false;
            }
            //3.删除基本信息
            this.removeById(id);
            //4.删除菜品的口味信息
            LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(dishDtoLambdaQueryWrapper);
        }
        return true;
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
        Page pageInfo = new Page(page, pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //3.添加过滤条件（根据输入的名字进行查询）
        //①like：模糊查询，与eq不同的是eq要相同才可以，like是包含就可以
        //②参数1：查询的条件（是否要进行查询），当name不为空时才进行模糊查询
        //③参数2：表示要过滤的目标对象为dish中的name属性
        //④参数3：要过滤的值
        queryWrapper.like(name != null, Dish::getName,name);
        //4.添加排序条件
        //Dish::getUpdateTime：Dish中的UpdateTime属性
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //5.调用dishService类中的page方法进行查询
        //参数1：分页信息的对象
        //参数2：查询条件的封装
        dishService.page(pageInfo,queryWrapper);
        //6.将pageInfo对象拷贝一份，因为这个对象会缺少一个属性(categoryName)，这个属性用于展示分类名字
        //①创建一个新对象
        Page<DishDto> dishDto = new Page<>();
        //②拷贝
        //参数1：源对象
        //参数2：目标对象
        //参数3：不需要拷贝的属性
        BeanUtils.copyProperties(pageInfo,dishDto,"records");
        //7.获取records属性下的categoryName属性
        //①获取records属性
        List<Dish> records = pageInfo.getRecords();
        //②读取records下的categoryName属性，并且赋给一个新的集合
        List<DishDto> list = records.stream().map((item) -> {
            //(1)创建一个dishDto对象
            DishDto dishDto1 = new DishDto();
            //(2)将遍历到的每一个item对象都赋值到dishDto1
            BeanUtils.copyProperties(item, dishDto1);
            //(3)获取分类ID
            Long categoryId = item.getCategoryId();
            //(4)根据分类ID获取分类的名字
            Category byId = categoryService.getById(categoryId);
            String name1 = byId.getName();
            //(5)将获取到的分类名字赋值给dishDto1对象
            dishDto1.setCategoryName(name1);
            //(6)将dishDto1对象返回
            return dishDto1;
        }).collect(Collectors.toList());
        //8.将查询到的分类名字赋值给dishDto对象
        dishDto.setRecords(list);
        //9.将结果返回
        return dishDto;
    }

    /**
     * 查询当前分类下的菜品
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> categoryOfDish(Dish dish) {
        //1.创建条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.添加过滤条件
        //①ID要相同的菜品分类
        //参数1：查询的条件（是否要进行查询），当CategoryId不为空时才进行模糊查询
        //参数2：表示要过滤的目标对象为dish中的CategoryId属性
        //参数3：要过滤的值
        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //②菜品要处于起售的状态才进行显示
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //③当用户使用搜索框时使用name进行模糊查询
        //like：模糊查询，与eq不同的是eq要相同才可以，like是包含就可以
        //参数1：查询的条件（是否要进行查询），当name不为空时才进行模糊查询
        //参数2：表示要过滤的目标对象为dish中的name属性
        //参数3：要过滤的值
        lambdaQueryWrapper.like(dish.getName() != null, Dish::getName,dish.getName());
        //3.添加排序添加
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //4.调用dishService类中的list方法进行查询
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        //5.获取list对象下的flavors属性
        List<DishDto> dishDto = list.stream().map((item) -> {
            //(1)创建一个dishDto对象
            DishDto dishDto1 = new DishDto();
            //(2)将遍历到的每一个item对象都赋值到dishDto1
            BeanUtils.copyProperties(item, dishDto1);
            //(3)获取分类ID
            Long categoryId = item.getCategoryId();
            //(4)根据分类ID获取分类的名字
            Category byId = categoryService.getById(categoryId);
            String name1 = byId.getName();
            //(5)将获取到的分类名字赋值给dishDto1对象
            dishDto1.setCategoryName(name1);
            //(6)获取当前的菜品id
            Long id = item.getId();
            //(7)创建条件构造器
            LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //(8)添加条件
            dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            //(9)查询
            List<DishFlavor> dishList = dishFlavorService.list(dishDtoLambdaQueryWrapper);
            //(10)将查询到的口味信息赋值给dishDto对象
            dishDto1.setFlavors(dishList);
            //(11)将dishDto1对象返回
            return dishDto1;
        }).collect(Collectors.toList());
        return dishDto;
    }
}
