package com.chenchen.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
    这个类用于全局的异常处理
    Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
    RequestBody：用于将对象反序列化为json格式的对象。
    ControllerAdvice：处理类中有RestController和Controller这两个注解的类的异常信息
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * 处理SQLIntegrityConstraintViolationException这个异常的方法
     * ExceptionHandler：处理SQLIntegrityConstraintViolationException类型的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //1.账号/菜单/套餐已存在异常：Duplicate entry 'xxx' for key 'employee
        //①判断是否为账号/菜单/套餐已存在的异常(判断是否包含`Duplicate entry`这个的错误信息即可)
        if (ex.getMessage().contains("Duplicate entry")){
            //②将异常的信息进行分割(按空格进行分割)并且存储到数组里
            String[] s = ex.getMessage().split(" ");
            //③获取数组中第三个元素(报错的用户名/菜单/套餐xxx)，然后拼接`已存在`
            String msg = s[2] + "已存在";
            //④将错误信息直接返回
            return Result.error(msg);
        }
        //2.返回其他异常
        return Result.error("未知错误！");
    }

    /**
     * 处理SCustomException这个异常的方法
     * ExceptionHandler：处理SCustomException类型的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        //直接将传送过来报错信息返回给客户端即可
        return Result.error(ex.getMessage());
    }
}
