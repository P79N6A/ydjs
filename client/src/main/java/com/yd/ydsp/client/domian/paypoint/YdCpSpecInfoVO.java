package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class YdCpSpecInfoVO implements Serializable{
    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 规格id
     */
    private String specid;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格类型(暂时不使用)
     */
    private Integer specType;

    /**
     *规格的内容设置，不重复的集合字串
     */
    private Set<String> items;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getSpecid() {
        return specid;
    }

    public void setSpecid(String specid) {
        this.specid = specid == null ? null : specid.trim();
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName == null ? null : specName.trim();
    }

    public Integer getSpecType() {
        return specType;
    }

    public void setSpecType(Integer specType) {
        this.specType = specType;
    }

    public Set<String> getItems() {
        return items;
    }

    public void setItems(Set<String> items) {
        this.items = items;
    }
}
