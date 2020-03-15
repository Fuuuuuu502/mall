package com.changgou.order.dao;

import com.changgou.order.pojo.Collet;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author：YangGeGe
 * @Date：2020/3/9 0009 14:52
 */
public interface ColletMapper extends Mapper<Collet> {

    @Select( "select sku_id from tb_collet where username=#{username}" )
    List<String> findColletSkuId(String username);
}
