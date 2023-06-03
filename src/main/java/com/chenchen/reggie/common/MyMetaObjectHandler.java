package com.chenchen.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/*
    这个类用于公共字段填充时自动填充，不用每个类都编写
    Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
    Component：让Spring框架检测为一个bean。
    MetaObjectHandler：这个接口提供了在数据库表进行插入和更新操作之前的回调方法，在持久化之前填充实体字段。
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * insertFill：这个方法在插入数据时执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充方法insertFill执行......");
        //编写要执行的公共字段，参数1：执行的字段（在实体类中进行标记），参数2：更新的数据
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //使用线程的方式获取当前的ID
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段填充方法updateFill执行......");
        //编写要执行的公共字段，参数1：执行的字段（在实体类中进行标记），参数2：更新的数据
        metaObject.setValue("updateTime", LocalDateTime.now());
        //使用线程的方式获取当前的ID
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
