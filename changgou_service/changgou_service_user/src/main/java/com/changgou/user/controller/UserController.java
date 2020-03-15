package com.changgou.user.controller;
import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.user.config.TokenDecode;
import com.changgou.user.pojo.Track;
import com.changgou.user.service.UserService;
import com.changgou.user.pojo.User;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private UserService userService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('accountant')")
    public Result findAll(){
        List<User> userList = userService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",userList) ;
    }

    /***
     * 根据ID查询数据
     * @param username
     * @return
     */
    @GetMapping("/{username}")
    public Result findById(@PathVariable String username){
        User user = userService.findById(username);
        return new Result(true,StatusCode.OK,"查询成功",user);
    }

    @GetMapping("/load/{username}")
    public User findUserInfo(@PathVariable("username") String username){
        User user = userService.findById(username);
        return user;
    }



    /***
     * 新增数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user){
        userService.add(user);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param user
     * @param username
     * @return
     */
    @PutMapping(value="/{username}")
    public Result update(@RequestBody User user,@PathVariable String username){
        user.setUsername(username);
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param username
     * @return
     */
    @DeleteMapping(value = "/{username}" )
    public Result delete(@PathVariable String username){
        userService.delete(username);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<User> list = userService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<User> pageList = userService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    //修改密码
    @PutMapping("/password/{username}")
    public Result password(@RequestBody User user,@PathVariable("username")String username){
        String password = user.getPassword();
        userService.updatePassword(username,password);
        return new Result(true,StatusCode.OK,"修改密码成功");
    }
    //绑定手机
    @PutMapping("/phone/{username}")
    public Result phone(@RequestBody User user,@PathVariable("username")String username) {
        user.setUsername(username);
        userService.updatePhone(user);
        return new Result(true, StatusCode.OK, "绑定手机成功");
    }

    @Autowired
    private SkuFeign skuFeign;
    @GetMapping("/track")
    public Result track(){
        ArrayList<Sku> list = new ArrayList<>();
        String username = tokenDecode.getUserInfo().get( "username" );
        List<Track> tracks=userService.findTrack(username);
        if (tracks!=null&& tracks.size()>0){
            for (Track track : tracks) {
                list.add( skuFeign.findById( track.getSkuId() ).getData() );
            }
        }
        return new Result(true,StatusCode.OK,"查询足迹成功",list);
    }

    @GetMapping("/addTrack/{skuId}")
    public Result addTrack(@PathVariable("skuId")String skuId){
        String username = tokenDecode.getUserInfo().get( "username" );
        userService.addTrack(username,skuId);
        return new Result(true,StatusCode.OK,"记录足迹成功");
    }
}
