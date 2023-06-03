package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.dto.SetmealDto;
import com.chenchen.reggie.entity.Setmeal;
import com.chenchen.reggie.service.CategoryService;
import com.chenchen.reggie.service.SetmealDishService;
import com.chenchen.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  套餐管理
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/employee）
 * */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
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
     * 保存套餐方法
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * PostMapping：指定HTTP请求的路径前缀（/setmeal）
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        //1.调用setmealService类中的saveWithDish方法进行保存
        setmealService.saveWithDish(setmealDto);
        //2.返回结果
        return Result.success("新增套餐成功！");
    }

    /**
     * 分页查询方法
     * GetMapping：设置访问路径为/setmeal/page
     * @param page：当前页数
     * @param pageSize：每页显示的条数
     * @param name：查询的名字
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        //1.调用setmealService类中的pageList方法进行保存
        Page pageList = setmealService.pageList(page, pageSize, name);
        //6.将查询结果返回
        return Result.success(pageList);
    }

    /**
     * 删除套餐方法
     * DeleteMapping：指定HTTP请求的路径前缀（/setmeal）
     * RequestParam：请求 URL 中获取参数值
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        //1.调用setmealService类中的removeWithDish方法进行删除
        setmealService.removeWithDish(ids);
        //2.返回结果
        return Result.success("套餐删除成功！");
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
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        //2.构造查询条件，根据ids批量更新
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        //in：根据某个字段匹配值的集合，查询符合条件的记录。
        //参数1：需要匹配的字段名
        //参数2：需要匹配的值集合
        wrapper.in("id", ids);
        //3.执行批量更新操作，并且判断是否成功
        //参数1：要更新的数据实体对象
        //参数2：更新的条件
        if (!(setmealService.update(setmeal, wrapper))){
            //失败
            return Result.success("修改失败！");
        }
        //成功
        return Result.success("修改成功！");
    }

    /**
     * 套餐信息回显方法
     * PathVariable：这个注解用于将请求路径中的参数值绑定到方法参数上
     * GetMapping：设置访问路径为/id，id为一串值
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> getById(@PathVariable Long id){
        //1.调用dishService类中的getByIdWithFlavor方法进行查询数据
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        //2.返回结果
        return Result.success(setmealDto);
    }

    /**
     * 修改套餐方法
     * PutMapping：设置访问路径为/setmeal
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto"+setmealDto);
        //1.调用setmealService类中的updateWithDish方法进行保存
        setmealService.updateWithDish(setmealDto);
        //2.返回结果
        return Result.success("修改套餐成功！");
    }

    /**
     * 根据ID查询当前套餐下的菜品
     * GetMapping：设置访问路径为/list
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        //1.调用setmealService类中的setmealOfDish方法进行查询
        List<Setmeal> setmeals= setmealService.setmealOfDish(setmeal);
        //2.将查询的结果返回
        return Result.success(setmeals);
    }
}
