package com.chenchen.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
    这个类用于配置mybatis相关的配置
    Configuration：表明这个类是一个配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页查询方法
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //1.创建MybatisPlusInterceptor对象
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //2.使用这个对象添加分页查询插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //3.返回对象
        return mybatisPlusInterceptor;
    }
}
