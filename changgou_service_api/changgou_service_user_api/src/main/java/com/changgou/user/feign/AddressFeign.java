package com.changgou.user.feign;

import com.changgou.entity.Result;
import com.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user")
public interface AddressFeign {

    //根据当前的登录人名称获取与之相关的收件人地址信息
    @GetMapping("/address/list")
    public Result<List<Address>> list();

    //根据id删除收件人信息
    @DeleteMapping(value = "/address/{id}" )
    public Result delete(@PathVariable("id") Integer id);

    //根据id修改收件人信息
    @PutMapping(value="/address/{id}")
    public Result update(@RequestBody Address address, @PathVariable("id") Integer id);

    //根据id修改收件人信息之前, 查询出相对应的收件人信息, 回显页面
    @GetMapping("/address/{id}")
    public Result findById(@PathVariable("id") Integer id);

    //新增用户收件人信息
    @PostMapping("/address")
    public Result add(@RequestBody Address address);

    //修改默认收件人信息
    @PutMapping("/address/default/{id}")
    public Result defaultModification(@PathVariable("id") Integer id);

}
