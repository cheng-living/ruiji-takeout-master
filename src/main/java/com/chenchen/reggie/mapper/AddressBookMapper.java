package com.chenchen.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenchen.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用于定义访问Category数据库表的CRUD操作
 * Mapper：用于标记该接口是一个MyBatis的Mapper接口。
 * BaseMapper：继承这个类可以免去编写基本的增删改查等操作
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
