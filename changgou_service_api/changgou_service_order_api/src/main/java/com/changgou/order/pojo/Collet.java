package com.changgou.order.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author：YangGeGe
 * @Date：2020/3/9 0009 11:48
 */

@Table(name="tb_collet")
public class Collet {
    private String username;
    private Date created;
    @Column(name = "sku_id")
    private String skuId;//SPUID

    public Collet(String username, Date created, String skuId) {
        this.username = username;
        this.created = created;
        this.skuId = skuId;
    }

    public Collet() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSpuId() {
        return skuId;
    }

    public void setSpuId(String spuId) {
        this.skuId = spuId;
    }
}
