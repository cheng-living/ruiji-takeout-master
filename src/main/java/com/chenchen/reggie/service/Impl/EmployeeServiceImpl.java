package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.entity.Employee;
import com.chenchen.reggie.mapper.EmployeeMapper;
import com.chenchen.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * EmployeeService：可以调用这个接口的常用方法
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
    //自动装配
    @Autowired
    private EmployeeService employeeService;

    /**
     * 分页查询方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page pageList(int page, int pageSize,String name) {
        //1.创建分页构造器,参数1：当前页数，参数2：每页显示的条数
        Page pageInfo = new Page(page, pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //3.添加过滤条件（根据输入的名字进行查询）
        //①like：模糊查询，与eq不同的是eq要相同才可以，like是包含就可以
        //②参数1：查询的条件（是否要进行查询），当name不为空时才进行模糊查询
        //③参数2：表示要过滤的目标对象为Employee中的name属性
        //④参数3：要过滤的值
        queryWrapper.like(name != null,Employee::getName,name);
        //4.添加排序条件
        //Employee::getUpdateTime：Employee中的UpdateTime属性
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //5.调用employeeService类中的page方法进行查询
        //参数1：分页信息的对象
        //参数2：查询条件的封装
        employeeService.page(pageInfo,queryWrapper);
        //6.返回结果
        return pageInfo;
    }

}
