package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.dto.DishDto;
import com.chenchen.reggie.entity.Dish;
import com.chenchen.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  菜品管理
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/dish）
 * */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    //自动装配
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品方法
     * PostMapping：设置访问路径为/dish
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        //1.调用dishService类中的saveWithFlavor方法进行保存
        dishService.saveWithFlavor(dishDto);
        //2.返回结果
        return Result.success("新增菜品成功！");
    }

    /**
     * 分页查询方法
     * GetMapping：设置访问路径为/dish/page
     * @param page：当前页数
     * @param pageSize：每页显示的条数
     * @param name：查询的名字
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        //1.调用dishService类中的pageList方法进行查询
        Page dishDto =dishService.pageList(page,pageSize,name);
        //2.将查询结果返回
        return Result.success(dishDto);
    }

    /**
     * 菜品信息回显方法
     * PathVariable：这个注解用于将请求路径中的参数值绑定到方法参数上
     * GetMapping：设置访问路径为/id，id为一串值
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id){
        //1.调用dishService类中的getByIdWithFlavor方法进行查询数据
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        //2.返回结果
        return Result.success(dishDto);
    }

    /**
     * 修改菜品方法
     * PutMapping：设置访问路径为/dish
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        //1.调用dishService类中的updateWithFlavor方法进行保存
        dishService.updateWithFlavor(dishDto);
        //2.返回结果
        return Result.success("修改菜品成功！");
    }

    /**
     * 删除菜品方法
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long[] ids){
        //1.调用dishService类中的deleteWithFlavor方法进行删除
        boolean b = dishService.deleteWithFlavor(ids);
        if (!b){
            return Result.error("分类有处于售卖的菜品，删除失败！");
        }
        //2.返回结果
        return Result.success("分类信息删除成功！");
    }

    /**
     * 修改状态方法
     * PathVariable：这个注解用于将请求路径中的参数值绑定到方法参数上
     * PostMapping：设置访问路径为/dish/{status}，status为动态参数
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> setStatus(@PathVariable int status,Long[] ids){
        //1.创建更新的实体对象，并且设置他的状态值为传过来的状态值
        Dish dish = new Dish();
        dish.setStatus(status);
        //2.构造查询条件，根据ids批量更新
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        //in：根据某个字段匹配值的集合，查询符合条件的记录。
        //参数1：需要匹配的字段名
        //参数2：需要匹配的值集合
        wrapper.in("id", ids);
        //3.执行批量更新操作，并且判断是否成功
        //参数1：要更新的数据实体对象
        //参数2：更新的条件
        if (!(dishService.update(dish, wrapper))){
            //失败
            return Result.success("修改失败！");
        }
        //成功
        return Result.success("修改成功！");
    }

    /**
     * 根据ID查询当前菜品分类下的菜品
     * GetMapping：设置访问路径为/list
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        //1.调用dishService类中的categoryOfDish方法进行查询
        List<DishDto> dishDto = dishService.categoryOfDish(dish);
        //2.将查询的结果返回
        return Result.success(dishDto);
    }
}
