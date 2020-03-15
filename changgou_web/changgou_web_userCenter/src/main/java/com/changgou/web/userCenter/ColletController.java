package com.changgou.web.userCenter;

import com.aliyuncs.exceptions.ClientException;
import com.changgou.entity.MessageConstant;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.feign.OrderFeign;
import com.changgou.user.feign.UserFeign;
import com.changgou.user.pojo.User;
import com.changgou.util.SMSUtils;
import com.changgou.util.ValidateCodeUtils;
import com.sun.glass.ui.delegate.MenuItemDelegate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author：YangGeGe
 * @Date：2020/3/9 0009 22:53
 */
@RequestMapping("/wcollet")

@Controller
public class ColletController {
    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/collet")
    public String collet(Model model) {
        Result result = orderFeign.findCollet();
        ArrayList<Sku> data = (ArrayList<Sku>) result.getData();
        model.addAttribute( "skus", data );
        return "center-collect";
    }


    @GetMapping("/track")
    public String track(Model model) {
        Result result = userFeign.track();
        ArrayList<Sku> data = (ArrayList<Sku>) result.getData();
        model.addAttribute( "skus", data );
        return "center-footmark";
    }

    @RequestMapping("/regist")
    public String regist(Model model) {

        return "register";
    }

    //    @GetMapping("/send_sms")
////    public
    @RequestMapping("/send_sms")
    @ResponseBody
    public Result send4Login(@RequestParam("mobile") String mobile) {
        //随机生成6位数字验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode( 4 );
        //给用户发送验证码
        try {
            SMSUtils.sendShortMessage( SMSUtils.ORDER_NOTICE, mobile, validateCode.toString() );
        } catch (ClientException e) {
            e.printStackTrace();

        }
        //将验证码保存到redis数据库中,保存5分钟
        //setex 将指定数值保存指定时间
        redisTemplate.boundValueOps( mobile + "_sms" ).set( validateCode.toString(), 3000, TimeUnit.SECONDS );
        //验证码发送成功
        return new Result( true, StatusCode.OK,"发送验证码成功",validateCode );
    }

    @PostMapping("/addUser")
    @ResponseBody
    public Result addUser(@RequestBody Map map, @RequestParam("smsCode") String smsCode){
        String phone = (String) map.get( "phone" );
        String password = (String) map.get( "password" );
        String validateCode = (String) redisTemplate.boundValueOps( phone + "_sms" ).get();
        if (StringUtils.isEmpty( validateCode )|| !validateCode.equals( smsCode )){
            return new Result( true, StatusCode.OK,"验证码输入错误" );
        }
        //进行用户数据的存储
        User user = new User();
        user.setUsername( phone );
        user.setPassword( password );
        //获取盐
        String gensalt = BCrypt.gensalt();
        //对用户的密码进行加密
        String hashpw = BCrypt.hashpw(user.getPassword(), gensalt);
        user.setPassword( hashpw );
        user.setCreated( new Date(  ) );
        user.setUpdated( new Date(  ) );
        try {
            userFeign.add( user );
        } catch (Exception e) {
            e.printStackTrace();
            return new Result( false, StatusCode.OK,"注册失败" );
        }
        return new Result( true, StatusCode.OK,"注册成功" );

    }
}
