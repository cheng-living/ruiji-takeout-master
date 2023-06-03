package com.chenchen.reggie.controller;

import com.chenchen.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *  文件上传和下载的操作
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/common）
 * */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //让这个变量的值为配置文件中的reggie.path路径
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传方法
     * MultipartFile：处理文件上传对象
     * PostMapping：指定HTTP请求的路径前缀（/common/upload）
     * @param file：名字一定要是这个（和前端设置的名字相同），否则无法接收
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        //1.获取用户上传的图片名
        String originalFilename = file.getOriginalFilename();
        // 2.将图片名重新修改
        //①获取图片的后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //②使用UUID生成一个新的名字，并且拼接刚才的后缀名
        String name = UUID.randomUUID().toString() + substring;
        //3.将图片保存
        //①获取配置文件的保存路径，并且判断文件夹是否已经创建，如果没有则创建一个
        File dir = new File(basePath);
        if (!dir.exists()){
            //目录不存在，则创建
            dir.mkdirs();
        }
        //②将图片保存
        try {
            file.transferTo(new File(basePath + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //4.将图片的名字返回给客户端，方便下载的时候进行使用
        return Result.success(name);
    }

    /**
     * 文件下载方法
     * GetMapping：指定HTTP请求的路径前缀（/common/download）
     * @param response
     * @param name
     * @return
     */
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response){
        try {
            //1.获取文件名
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //2.将文件通过输出流的方式写回到页面
            //①写的是什么类型
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            //②写内容
            int len = 0;
            byte[] bytes = new byte[1024];
            //循坏获取文件的字节大小并且写回页面
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
