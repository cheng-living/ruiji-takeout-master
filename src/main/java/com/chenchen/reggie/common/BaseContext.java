package com.chenchen.reggie.common;

/*
    这个类用于获取和储存一些信息在线程中
 */
public class BaseContext {
    //提供了线程本地存储能力，允许变量在一个线程的方法和操作之间共享，而无需将它们作为方法参数传递或使用全局变量。
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //setCurrentId：这个方法用于存储信息到线程中
    public static void setCurrentId(Long id){
        //将线程本地变量的值设置为提供的id
        threadLocal.set(id);
    }
    //getCurrentId：这个方法用于获取存储在线程中的信息
    public static Long getCurrentId(){
        //返回线程本地变量的当前值
        //作用域相当于是以线程为单位，当前线程不死，就能获取到当前线程存储的值
        return threadLocal.get();
    }

}
