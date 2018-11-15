package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareItemVO implements Serializable {

    private Long id;

    /**
     * 分类id
     */
    private String itemid;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序索引
     */
    private Integer itemseq;

    /**
     * 分类状态
     */
    private Integer status;

    public Long getId(){ return id;}

    public void setId(Long id){ this.id = id; }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid == null ? null : itemid.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getItemseq() {
        return itemseq;
    }

    public void setItemseq(Integer itemseq) {
        this.itemseq = itemseq;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
