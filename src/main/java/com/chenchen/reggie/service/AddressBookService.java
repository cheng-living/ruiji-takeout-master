package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.entity.AddressBook;

import java.util.List;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface AddressBookService extends IService<AddressBook> {
    //修改默认地址方法
    public void updateDefaultAddress(AddressBook addressBook);
    //修改地址方法
    public AddressBook updateAddress(AddressBook addressBook);
    //查询默认地址方法
    public AddressBook queryDefaultAddress();
    //查询所有地址方法
    public List<AddressBook> queryListAddress(AddressBook addressBook);
    //删除地址方法
    public void deleteAddress(Long ids);
}
