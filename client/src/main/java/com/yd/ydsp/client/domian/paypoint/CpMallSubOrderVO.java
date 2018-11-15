package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zengyixun
 * @date 18/1/16
 */
public class CpMallSubOrderVO implements Serializable {

    /**
     * 商品id
     */
    private String goodsid;

    /**
     * 主订单id
     */
    private String orderid;

    /**
     * 子订单id
     */
    private String subOrderid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品单价,到元
     */
    private BigDecimal price;

    /**
     * 商品图片
     */
    private String goodsImageUrl;

    /**
     * 子订单金额，到元
     */
    private BigDecimal amount;

    /**
     * 数量
     */
    private Integer goodsCount;

    /**
     * 订单创建时间
     */
    private Date createDate;


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

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
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
}
