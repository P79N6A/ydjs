package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.lang.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareSkuBaseVO implements Serializable {

    /**
     * 商品id
     */
    private String skuid;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 商品类目id
     */
    private String wareitemid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 轮播图的第一张
     */
    private String wareimg;

    /**
     * 商品描述信息
     */
    private String description;

    /**
     * 是否推荐
     */
    private Boolean isCommend = false;

    /**
     * 是否是必选商品
     */
    private Boolean isMustWare = false;

    /**
     * 已经卖出数量，不再只表示一个月的数量了
     */
    private Integer sellCountMonth=0;

    /**
     * 商品排序
     */
    private Integer wareseq;

    /**
     * 库存数据
     */
    private Integer inventory=999;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 是否有规格金额(金额要带"起"字， 19.9元起)
     */
    private Boolean hasSpacPrice;

    /**
     * 打折价格
     */
    private BigDecimal disprice;

    /**
     * 折扣(0.95折之类)
     */
    private BigDecimal discount;


    /**
     * 状态：0使用中,-1下架，1已经删除
     */
    private Integer status;

    /**
     * 商品单位
     */
    private String unitName;

    public String getSkuid(){ return skuid; }

    public void setSkuid(String skuid){
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
        if(StringUtil.isNotEmpty(this.wareimg)){
            return this.wareimg.replaceFirst("http://","https://");
        }
        return wareimg;
    }

    public void setWareimg(String wareimg) {
        this.wareimg = wareimg == null ? null : wareimg.trim();
        if(StringUtil.isNotEmpty(this.wareimg)){
            this.wareimg = this.wareimg.replaceFirst("http://","https://");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
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

    public Boolean getIsMustWare() {
        return isMustWare;
    }
    public void setIsMustWare(Boolean isMustWare){
        this.isMustWare = isMustWare;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Boolean getHasSpacPrice() {
        return hasSpacPrice;
    }

    public void setHasSpacPrice(Boolean hasSpacPrice) {
        this.hasSpacPrice = hasSpacPrice;
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

    public Boolean getIsCommend() {
        return isCommend;
    }

    public void setIsCommend(Boolean commend) {
        isCommend = commend;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName==null ? null: unitName.trim();
    }
}
