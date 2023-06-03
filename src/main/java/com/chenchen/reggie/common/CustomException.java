package com.chenchen.reggie.common;

/*
    自定义异常类
    RuntimeException：具有一个带有一个字符串参数的构造函数，用于创建一个实例并设置异常消息。
 */
public class CustomException extends RuntimeException{

    //这个方法在被创建自定义异常对象时，会将该参数作为异常消息传递给父类的构造函数进行初始化。
    public CustomException(String message){
        super(message);
    }
}
