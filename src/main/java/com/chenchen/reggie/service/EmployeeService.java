package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.entity.Employee;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface EmployeeService extends IService<Employee> {
    //新增一个方法，用于分页查询
    public Page pageList(int page, int pageSize,String name);
}
