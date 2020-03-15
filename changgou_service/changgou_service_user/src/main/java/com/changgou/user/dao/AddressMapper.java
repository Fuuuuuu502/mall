package com.changgou.user.dao;

import com.changgou.user.pojo.Address;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface AddressMapper extends Mapper<Address> {
    //修改默认收件人地址
    @Update("update tb_address set is_default='1' where id = #{id}")
    public void defaultModification(Integer id);

    //查询默认收件人//
    @Select("select * from tb_address where is_default='1' and username=#{username}")
    Address queryDefaultRecipients(String username);

    //修改原默认收件人地址
    @Update("update tb_address set is_default='0' where id = #{id}")
    void defaultRecipient(Integer id);

}
