package com.chenchen.reggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

//这个类用于服务器返回的结果，服务器响应的数据最终都将封装成这个对象
@Data
public class Result<T> {

    //编码：1成功，0和其它数字为失败
    private Integer code;
    //错误信息
    private String msg;
    //数据
    private T data;
    //动态数据
    private Map map = new HashMap();

    //响应成功
    //static <T> Result<T>：static后面的泛型是因为为静态方法，所以要加这个泛型
    public static <T> Result<T> success(T object) {
        //创建对象
        Result<T> result = new Result<T>();
        //将对象中的数据改为传送进来的数据
        result.data = object;
        //将编码改为成功
        result.code = 1;
        //返回Result对象
        return result;
    }
    //响应失败
    //static <T> Result<T>：static后面的泛型是因为为静态方法，所以要加这个泛型
    public static <T> Result<T> error(String msg) {
        //创建对象
        Result result = new Result();
        //将对象中的错误信息改为传送进来的错误信息提示
        result.msg = msg;
        //将编码改为失败
        result.code = 0;
        //返回Result对象
        return result;
    }

    //添加方法
    public Result<T> add(String key, Object value) {
        //将传进来的key和value保存起来
        this.map.put(key, value);
        //返回result对象
        return this;
    }

}
