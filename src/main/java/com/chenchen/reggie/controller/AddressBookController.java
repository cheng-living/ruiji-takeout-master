package com.chenchen.reggie.controller;

import com.chenchen.reggie.common.BaseContext;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.entity.AddressBook;
import com.chenchen.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  文件上传和下载的操作
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/addressBook）
 * */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    //自动装配
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址方法
     * PostMapping：设置访问路径为/addressbook
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        //1.获取保存在线程中的ID并且保存到实体中
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        //2.将实体中的对象保存到数据库
        addressBookService.save(addressBook);
        //3.将结果对象返回
        return Result.success(addressBook);
    }

    /**
     * 修改默认的地址方法
     * PutMapping：设置访问路径为/addressbook/default
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        //1.调用addressBookService类中的updateAddress方法进行修改
        addressBookService.updateDefaultAddress(addressBook);
        //2.返回结果
        return Result.success(addressBook);
    }

    /**
     * 查询地址方法
     * PathVariable：这个注解用于将请求路径中的参数值绑定到方法参数上
     * PutMapping：设置访问路径为/addressbook/id，id为动态参数
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id){
        //1.获取当前id的对象
        AddressBook addressBook = addressBookService.getById(id);
        //2.判断对象是否为空
        if (addressBook != null){
            //不为空则返回对象
            return Result.success(addressBook);
        }else {
            //为空则返回查找失败
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址方法
     * GetMapping：设置访问路径为/addressbook/default
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault(){
        //1.调用addressBookService类中的queryDefaultAddress方法进行查询
        AddressBook addressBook = addressBookService.queryDefaultAddress();
        //2.判断查询的结果是否为空
        if (addressBook == null){
            //为空则表示没有默认地址
            return Result.error("没有找到该对象");
        }else {
            //不为空则表示找到了，返回对象即可
            return Result.success(addressBook);
        }
    }

    /**
     * 查询所有地址方法
     * PutMapping：设置访问路径为/addressbook/list
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook){
        //1.调用addressBookService类中的queryListAddress方法进行查询
        List<AddressBook> addressBooks = addressBookService.queryListAddress(addressBook);
        //2.将查询的结果返回
        return Result.success(addressBooks);
    }

    /**
     * 修改地址方法
     * PutMapping：设置访问路径为/addressbook
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<AddressBook> setAddress(@RequestBody AddressBook addressBook){
        log.info("修改地址方法" + addressBook);
        //1.调用addressBookService类中的queryAddress方法进行查询
        addressBookService.updateAddress(addressBook);
        //2.返回结果
        return Result.success(addressBook);
    }

    /**
     * 删除地址方法
     * DeleteMapping：设置访问路径为/addressbook
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteAddress(Long ids){
        log.info("删除地址方法：" + ids);
        //1.调用addressBookService类中的deleteAddress方法进行查询
        addressBookService.deleteAddress(ids);
        //2.返回结果
        return Result.success("删除成功！");
    }
}
