package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.entity.Employee;
import com.chenchen.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * employee相关的操作
*  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
*  RestController：表明该类是rest风格的控制器
*  RequestMapping：指定HTTP请求的路径前缀（/employee）
* */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    //自动装配
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录方法
     * PostMapping：设置访问路径为/employee/login
     * HttpServletRequest:获取请求对象
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        //①Spring框架提供了一个类DigestUtils，里面有个方法可以将密码使用md5的方式进行加密
        //②getBytes：将命名转为字节数组
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        //①LambdaQueryWrapper：MyBatis-Plus中的一个查询构造器类，它提供了一系列的方法用于拼接各种查询条件
        //②Employee::getUsername：表示访问员Employee类中的静态方法getUsername。
        //③queryWrapper.eq：指定一个相等的条件，
        //④employeeService.getOne(queryWrapper)：在employeeService类中调用getOne方法在queryWrapper中获取唯一一条符合的数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查询到则返回登录失败结果
        if (emp == null){
            //调用Result类中的错误方法，并且携带参数
            return Result.error("用户名不存在！");
        }
        //4.查询到则密码比对，如果不一致则返回登录失败结果
        //①emp.getPassword()：数据库的密码
        //②password：用户输入加密后的密码
        if (!emp.getPassword().equals(password)){
            //调用Result类中的错误方法，并且携带参数
            return Result.error("密码错误！");
        }
        //5.一致则查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        //emp.getStatus()：数据库中的状态码，0表示禁用，1表示可用
        if (emp.getStatus() == 0){
            //调用Result类中的错误方法，并且携带参数
            return Result.error("该账号已被禁用！");
        }
        //6.如果不是则登录成功，并且将员工id存入Session并返回登录成功结果
        //①getSession()：返回当前请求所关联的Session对象。
        //②setAttribute()：设置Session。
        //③emp.getId()：数据库中的id
        request.getSession().setAttribute("employee",emp.getId());
        //返回结果
        return Result.success(emp);
    }

    /**
     * 退出方法
     * PostMapping：设置访问路径为/employee/logout
     * HttpServletRequest:获取请求对象
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //1.清除当前session中保存的id
        request.getSession().removeAttribute("employee");
        //2.直接返回对象即可
        return Result.success("退出成功！");
    }

    /**
     * 添加员工方法
     * PostMapping：设置访问路径为/employee
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save( @RequestBody Employee employee){
        //1.在客户端传过来的数据基础上添加以下信息
        //①初始密码，要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //②创建时间、修改时间、哪个管理员创建的、修改信息的管理员
        /*
            这些方法在公共字段填充类中都写了，这里不用再写
         */
        //2.调用employeeService类中的save方法进行保存
        employeeService.save(employee);
        //3.返回结果
        return Result.success("新增员工成功！");
    }

    /**
     * 分页查询方法
     * GetMapping：设置访问路径为/employee/page
     * @param page：当前页数
     * @param pageSize：每页显示的条数
     * @param name：查询的名字
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        //1.调用employeeService类中的pageList方法进行查询
        Page pageList = employeeService.pageList(page, pageSize, name);
        //2.将查询结果返回
        return Result.success(pageList);
    }

    /**
     * 修改员工信息方法
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * PutMapping：设置访问路径为/employee
     * @param employee：这个对象包含客户端传送过来的要修改的人的id和要修改的人状态码
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee){
        log.info(employee.toString());
        //1.获取当前要修改的员工id、修改本次的修改人、修改本次的修改时间
        /*
            这些方法在公共字段填充类中都写了，这里不用再写
         */
        //2.调用employeeService类中的updateById方法根据id进行修改
        employeeService.updateById(employee);
        //3.返回修改结果
        return Result.success("员工信息修改成功！");
    }

    /**
     * 员工信息回显方法
     * PathVariable：这个注解用于将请求路径中的参数值绑定到方法参数上
     * GetMapping：设置访问路径为/id，id为一串值
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据ID查询员工信息方法已执行。。。。");
        //1.调用employeeService类中的getById方法进行查询数据
        Employee byId = employeeService.getById(id);
        //2.返回结果
        //①判断查询的结果是否为空
        if (byId != null){
            //②不为空则返回查询的数据
            return Result.success(byId);
        }
        //③为空就返回错误
        return Result.error("员工信息未查询到");
    }
}
