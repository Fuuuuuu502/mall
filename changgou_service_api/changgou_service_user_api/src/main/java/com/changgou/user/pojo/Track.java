package com.changgou.user.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author：YangGeGe
 * @Date：2020/3/9 0009 11:48
 */

@Table(name = "tb_track")
public class Track {

    @Id
    private Integer id;
    private String username;
    private Date created;
    @Column(name = "sku_id")
    private String skuId;

    public Track(Integer id, String username, Date created, String skuId) {
        this.id = id;
        this.username = username;
        this.created = created;
        this.skuId = skuId;
    }

    public Track() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}



