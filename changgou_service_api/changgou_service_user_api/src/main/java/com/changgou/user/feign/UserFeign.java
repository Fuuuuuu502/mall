package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user")
public interface UserFeign {

    @GetMapping("/user/load/{username}")
    public User findUserInfo(@PathVariable("username") String username);
    //回显手机号
    @GetMapping("/user/{username}")
    public Result findById(@PathVariable("username")  String username);
    //修改密码
    @PutMapping("/user/password/{username}")
    public Result password(@RequestBody User user,@PathVariable("username")String username);
    //绑定手机
    @PutMapping("/user/phone/{username}")
    public Result phone(@RequestBody User user,@PathVariable("username")String username);
    @GetMapping("/user/track")
    public Result track();
    @PostMapping("/user")
    public Result add(@RequestBody User user);
}
