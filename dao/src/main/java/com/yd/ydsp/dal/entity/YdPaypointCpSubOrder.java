package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdPaypointCpSubOrder {
    private Long id;

    private String goodsid;

    private String orderid;

    private String subOrderid;

    private String name;

    private BigDecimal price;

    private BigDecimal amount;

    private Integer goodsCount;

    private Date createDate;

    private Date modifyDate;

    private Integer isDelete=0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid == null ? null : goodsid.trim();
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public String getSubOrderid() {
        return subOrderid;
    }

    public void setSubOrderid(String subOrderid) {
        this.subOrderid = subOrderid == null ? null : subOrderid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}