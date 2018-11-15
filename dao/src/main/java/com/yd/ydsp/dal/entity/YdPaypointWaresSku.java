package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdPaypointWaresSku {
    private Long id;

    private String shopid;

    private String skuid;

    private String wareitemid;

    private String name;

    private String wareimg;

    private String description;

    private Integer waretype;

    private Long flag;

    /**
     * 已经卖出数量，不再只是表是一个月的售出数，而表未总体出售数量
     */
    private Integer sellCountMonth;

    private Integer wareseq;

    private Integer inventory;

    private BigDecimal price;

    private BigDecimal disprice;

    private Integer status;

    private String feature;

    private String unitName;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public String getWareitemid() {
        return wareitemid;
    }

    public void setWareitemid(String wareitemid) {
        this.wareitemid = wareitemid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getWareimg() {
        return wareimg;
    }

    public void setWareimg(String wareimg) {
        this.wareimg = wareimg == null ? null : wareimg.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getWaretype() {
        return waretype;
    }

    public void setWaretype(Integer waretype) {
        this.waretype = waretype;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public Integer getSellCountMonth() {
        return sellCountMonth;
    }

    public void setSellCountMonth(Integer sellCountMonth) {
        this.sellCountMonth = sellCountMonth;
    }

    public Integer getWareseq() {
        return wareseq;
    }

    public void setWareseq(Integer wareseq) {
        this.wareseq = wareseq;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDisprice() {
        return disprice;
    }

    public void setDisprice(BigDecimal disprice) {
        this.disprice = disprice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName == null ? null: unitName.trim();
    }
}