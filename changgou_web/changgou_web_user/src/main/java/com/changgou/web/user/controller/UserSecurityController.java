package com.changgou.web.user.controller;

import com.aliyuncs.exceptions.ClientException;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.user.feign.UserFeign;
import com.changgou.user.pojo.User;
import com.changgou.web.user.util.SMSUtils;
import com.changgou.web.user.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/wusers")
public class UserSecurityController {
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //校验验证码是否正确
    @PostMapping("/{userphone}")
    public String userPhone(@RequestBody Map map){
        String userphone = (String) map.get("phone");//手机号
        String validateCode = (String) map.get("validateCode");//验证码
        //获取redis中的验证码
        //String codeInRedis = jedisPool.getResource().get(phone);
        String codeInRedis = stringRedisTemplate.opsForValue().get(userphone);
        if(validateCode==null && codeInRedis==null && !validateCode.equals(codeInRedis)){
            return "";
        }
        return "center-setting-address-phone";
    }

    //绑定手机
    @PutMapping("/phone/{username}")
    public String updatePhone(@RequestBody User user, @PathVariable("username") String username){
        userFeign.phone(user,username);
        return "center-setting-address-complete";
    }
    //回显当前手机号
    @GetMapping("/{username}")
    public Result findById(@PathVariable("username") String username, Model model){
        Result result = userFeign.findById(username);
        Object data = result.getData();
        model.addAttribute("user",data);
        return new Result(true,StatusCode.OK,"",data);
    }
    //修改密码
    @PutMapping("/password/{username}")
    public Result updatePassword(@RequestBody User user, @PathVariable("username") String username){
        userFeign.password(user,username);
        return new Result(true, StatusCode.OK,"修改密码成功");
    }
    //发送短信
    @PostMapping("/code/{phone}")
    public Result verificationCode(@PathVariable("phone") String userphone){
        //随机生成4位数字
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,userphone,validateCode.toString());
        }catch (ClientException e){
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"发送失败");
        }
        //将验证码保存到redis数据库中,5分钟
        stringRedisTemplate.opsForValue().set(userphone, validateCode.toString(),60*100, TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间
        //验证码发送成功
        return new Result(true,StatusCode.OK,"发送成功");
    }
}
