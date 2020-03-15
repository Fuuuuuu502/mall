package com.changgou.web.user.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/waddress")
public class AddressController {

    @Autowired
    private AddressFeign addressFeign;

    //删除收件人
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Integer id){
        addressFeign.delete(id);
        return new Result<>(true, StatusCode.OK,"删除成功");
    }

    //根据id修改收件人信息之前, 查询出相对应的收件人信息, 回显页面
    @GetMapping("/findById/{id}")
    public Result<Address> findById(@PathVariable("id") Integer id ,Model model){
        Result address = addressFeign.findById(id);
        Object data = address.getData();
        model.addAttribute("address",data);
        return new Result<>(true, StatusCode.OK,"查询成功",model);
    }

    //新增用户收件人信息
    @PostMapping("/address/{username}")
    public Result add(@RequestBody Address address,@PathVariable("username")String username){
        address.setUsername(username);
        address.setIsDefault("0");
        addressFeign.add(address);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    //修改默认收件人信息
    @PutMapping("/default/{id}")
    public Result defaultModification(@PathVariable("id") Integer id){
        //设置修改默认收件人信息
        addressFeign.defaultModification(id);//
        return new Result(true,StatusCode.OK,"修改成功");
    }

    //根据id修改收件人信息
    @PutMapping(value="/update/{id}/{username}")
    public Result update(@RequestBody Address address,@PathVariable("id") Integer id,@PathVariable("username")String username){
        address.setId(id);
        address.setIsDefault("0");
        address.setUsername(username);
        addressFeign.update(address,id);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    @GetMapping("/list")
    public String list(Model model){
        Result<List<Address>> result = addressFeign.list();
        List<Address> data = result.getData();
        model.addAttribute("address",data);
        return "center-setting-address";
    }


}
