package com.chenchen.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.chenchen.reggie.common.BaseContext;
import com.chenchen.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个类用于判断用户是否已经登录，如果登录了就放行
 * WebFilter：标记为过滤器类
 * filterName：指定过滤器名称
 * urlPatterns：拦截哪些URL，/*表示拦截所有URI
 * Filter：这个接口包含很多关于过滤器的操作
 * Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //这个对象是spring提供的一个对象，用于匹配路径
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    //实现doFilter方法
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //1.获取本次请求的URL
        //①将servletRequest和servletResponse对象向下转型，这个对象可以获取到URI
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //②获取URI
        String requestURI = request.getRequestURI();
        //2.判断本次请求是否需要处理
        //①定义好不需要进行处理的请求路径
        String[] uris = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
        log.info("拦截到请求："+requestURI);
        //②调用匹配路径的方法进行匹配
        boolean check = check(uris, requestURI);
        //3.如果不需要处理，则直接放行
        if (check){
            log.info("请求不需要处理，已经放行");
            //为真就是不需要处理，放行然后直接结束方法
            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态，如果已登录，则直接放行
        //①管理端判断
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，已经放行");
            //将用户的ID保存到线程，让自动填充类可以获取到数据
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            //如果不等于空就是已经登录，放行然后直接结束方法
            filterChain.doFilter(request,response);
            return;
        }
        //②客户端判断
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，已经放行");
            //将用户的ID保存到线程，让自动填充类可以获取到数据
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            //如果不等于空就是已经登录，放行然后直接结束方法
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录，已经拦截");
        //5.如果未登录则返回未登录结果(通过输出流的方式向客户端页面响应数据)
        //①要为JSON格式
        //②调用Result类中的error方法
        //③错误信息一定要和客户端页面判断的信息(NOTLOGIN)一致
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        //④结束方法
        return;
    }

    /**
     * 这个方法用于匹配路径
     * @param uris
     * @param requestURI
     * @return
     */
    public boolean check(String[] uris,String requestURI){
        //①遍历数组
        for (String uri:uris) {
            //②调用AntPathMatcher对象进行匹配
            boolean match = PATH_MATCHER.match(uri, requestURI);
            //③判断结果
            if (match){
                //④为真就返回真
                return true;
            }
        }
        //⑤否则返回假
        return false;
    }
}
