package com.changgou.user.dao;

import com.changgou.user.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    @Update("update tb_user set points=points+#{point} where username=#{username}")
    int updateUserPoint(@Param("username")String username, @Param("point") int point);

    //修改密码
    @Update("update tb_user set password=#{password} where username=#{username}")
    void updatePassword(@Param("username")String username, @Param("password")String password);
    //绑定手机
    @Update("update tb_user set phone=#{phone} where username=#{username}")
    void updatePhone(@Param("username")String username, @Param("phone")String phone);


}
