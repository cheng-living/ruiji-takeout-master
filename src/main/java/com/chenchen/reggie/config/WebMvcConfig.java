package com.chenchen.reggie.config;

import com.chenchen.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/*
    Configuration：表明该类是配置类
    Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 */
@Slf4j
@Configuration
//继承WebMvcConfigurationSupport类
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //打印日志
        log.info("访问静态资源映射....");
        // 配置 /backend/** 映射到 classpath 下指定目录中的静态资源
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        // 配置 /front/** 映射到 classpath 下指定目录中的静态资源
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 这个方法用于扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.创建消息转换器
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //2.设置消息转换（使用Jackson通用类中的方法将Java对象和json数据进行转换）
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //3.设置消息转换的优先级，0表示最优
        converters.add(0,mappingJackson2HttpMessageConverter);
    }
}
