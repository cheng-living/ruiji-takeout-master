package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.common.BaseContext;
import com.chenchen.reggie.entity.AddressBook;
import com.chenchen.reggie.mapper.AddressBookMapper;
import com.chenchen.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * AddressBookService：可以调用这个接口的常用方法
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    //自动装配
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 修改默认地址方法
     * Transactional：事务处理，保证全部成功或者全部失败
     * @param addressBook
     * @return
     */
    @Transactional
    @Override
    public void updateDefaultAddress(AddressBook addressBook) {
        //1.创建条件构造器
        //①创建构造器
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //②添加条件：数据库中的userId和当前线程中的Id进行匹配
        lambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        //③添加条件：将所有的地址都改为0（禁用状态，不为默认地址）
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        //④调用addressBookService类中的update方法进行修改
        addressBookService.update(lambdaUpdateWrapper);
        //2.将当前的地址改为默认地址（状态码为1）
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
    }

    /**
     * 修改地址方法
     * @param addressBook
     * @return
     */
    @Override
    public AddressBook updateAddress(AddressBook addressBook) {
        //1.创建条件构造器
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //2.添加条件：数据库中的userId和当前线程中的Id进行匹配
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //3.调用addressBookService类中的update方法进行修改
        addressBookService.updateById(addressBook);
        //4.返回结果
        return addressBook;
    }

    /**
     * 查询默认地址方法
     * @return
     */
    @Override
    public AddressBook queryDefaultAddress() {
        //1.创建条件构造器
        LambdaUpdateWrapper<AddressBook> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
        //2.添加条件：数据库中的userId和当前线程中的Id进行匹配
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //3.添加条件：匹配状态码等于1（默认地址）
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        //4.调用addressBookService类中的getOne方法进行查询唯一一个值
        AddressBook addressBook= addressBookService.getOne(lambdaQueryWrapper);
        //5.将结果返回
        return addressBook;
    }

    /**
     * 查询所有地址方法
     * @return
     */
    @Override
    public List<AddressBook> queryListAddress(AddressBook addressBook) {
        //1.获取保存在线程中的ID并且保存到实体中
        addressBook.setUserId(BaseContext.getCurrentId());
        //2.创建条件构造器
        //①创建构造器
        LambdaUpdateWrapper<AddressBook> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
        //②添加条件：数据库中的userId和当前线程中的Id进行匹配
        //addressBook.getUserId()!= null：不等于空时才进行匹配
        lambdaQueryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        //③添加排序条件：根据修改的时间进行排序
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //④调用addressBookService类中的list方法进行查询
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        //3.返回结果
        return list;
    }

    /**
     * 删除地址方法
     * @param ids
     */
    @Override
    public void deleteAddress(Long ids) {
        //1.创建条件构造器
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //2.添加条件：数据库中的userId和当前传送进来的id进行匹配
        lambdaUpdateWrapper.eq(AddressBook::getUserId,ids);
        //3.调用addressBookService类中的removeById方法进行删除
        addressBookService.removeById(ids);
    }
}
