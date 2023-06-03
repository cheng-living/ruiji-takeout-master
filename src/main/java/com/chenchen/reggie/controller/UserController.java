package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.entity.User;
import com.chenchen.reggie.service.UserService;
//import com.chenchen.reggie.utils.SMSUtils;
import com.chenchen.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 *  用户管理
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/user）
 * */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    //自动装配
    @Autowired
    private UserService userService;

    /**
     * 生成验证码以及发送验证码
     * HttpSession：提供了访问和操作存储在会话中的数据的方法
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * PostMapping：指定HTTP请求的路径前缀（/user/sendMsg）
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(HttpSession httpSession,@RequestBody User user){
        //1.获取手机号码
        String phone = user.getPhone();
        //2.生成验证码
        //①判断手机号码是否为空
        if (phone != null){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Session

            httpSession.setAttribute(phone,code);

            //将生成的验证码缓存到Redis中，并且设置有效期为5分支
//            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("手机验证码短信发送成功");
        }
        return Result.error("手机验证码短信发送失败");
    }

    /**
     * 用户登录及验证码校验
     * HttpSession：提供了访问和操作存储在会话中的数据的方法
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * PostMapping：指定HTTP请求的路径前缀（/user/login）
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(HttpSession httpSession,@RequestBody Map map){
        log.info(map.toString());
        //1.获取传送过来的手机号
        String phone = map.get("phone").toString();
        //2.获取传送过来的验证码
        String code = map.get("code").toString();
        //3.获取保存在session中的验证码
        Object sessionCode = httpSession.getAttribute(phone);
        //4.进行验证码比对，判断用户输入的验证码和发送的验证码是否一致
        //①sessionCode != null：判断是否已经发送了验证码并且正确的保存到session
        //②判断两个验证码是否一致
        if ((sessionCode != null) && (sessionCode.equals(code))){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setName("哆啦a梦");
                userService.save(user);
            }
            //④将用户的ID存储到session
            httpSession.setAttribute("user",user.getId());
            //⑤返回用户的数据给客户端进行保存
            return Result.success(user);
        }
        //5.验证码不一直，直接返回登录失败
        return Result.error("验证码错误！");
    }

    /**
     * 用户退出登录方法
     * HttpSession：提供了访问和操作存储在会话中的数据的方法
     * @param httpSession
     * @return
     */
    @PostMapping("/loginout")
    public Result<String> login(HttpSession httpSession){
        //1.获取并且移除保存在session中的用户id
        httpSession.removeAttribute("user");
        //2.返回结果
        return Result.success("退出成功！");
    }
}
