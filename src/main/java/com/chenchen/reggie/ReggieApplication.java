package com.chenchen.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
    SpringBootApplication：表明该类是SpringBoot应用程序的启动类
    Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
    ServletComponentScan：用于启用Servlet组件的自动扫描和注册。
    EnableTransactionManagement：启用声明式事务管理。
 */
@EnableTransactionManagement
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@Slf4j
@ServletComponentScan
public class ReggieApplication {
    public static void main(String[] args) {
        //使用SpringApplication启动程序
        SpringApplication.run(ReggieApplication.class, args);
        //打印日志
        log.info("项目启动成功！");
    }
}
