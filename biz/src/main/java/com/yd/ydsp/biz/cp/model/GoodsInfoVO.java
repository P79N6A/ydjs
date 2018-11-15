package com.yd.ydsp.biz.cp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品页提交订单或者计算金额时的vo数据
 * @author zengyixun
 * @date 18/1/4
 */
public class GoodsInfoVO implements Serializable {

    String shopid;

    String upgradeType;
    /**
     * 返回金额：元
     */
    BigDecimal upgradeTotalAmount;

    /**
     * 套餐升级的天数
     */
    Integer remanent=0;

    Integer bagNumber=0;
    /**
     * 返回金额：元
     */
    BigDecimal bagTotalAmount;

    /**
     * 提交时传：goodId,goodNumber
     */
    Map<String,Integer> goods;
    /**
     * 返回时给：goodId,goodAmount
     * 返回金额：元
     */
    Map<String,BigDecimal> goodsAmount;

    BigDecimal totalAmount;

    /**
     * 买家留言
     */
    String buyerMessage;


    public String getShopid(){ return shopid; }
    public void setShopid(String shopid){ this.shopid = shopid; }

    public String getUpgradeType(){ return upgradeType; }
    public void setUpgradeType(String upgradeType){ this.upgradeType = upgradeType; }
    public BigDecimal getUpgradeTotalAmount(){ return upgradeTotalAmount; }
    public void setUpgradeTotalAmount(BigDecimal upgradeTotalAmount){ this.upgradeTotalAmount = upgradeTotalAmount; }
    public Integer getRemanent(){ return remanent; }
    public void setRemanent(Integer remanent){ this.remanent = remanent; }

    public Integer getBagNumber(){
        return bagNumber;
    }
    public void setBagNumber(Integer bagNumber){ this.bagNumber = bagNumber; }
    public BigDecimal getBagTotalAmount(){ return bagTotalAmount; }
    public void setBagTotalAmount(BigDecimal bagTotalAmount){ this.bagTotalAmount = bagTotalAmount; }

    public Map<String,Integer> getGoods(){ return goods; }
    public void setGoods(Map<String,Integer> goods){ this.goods = goods; }
    public Map<String,BigDecimal> getGoodsAmount(){ return goodsAmount; }
    public void setGoodsAmount(Map<String,BigDecimal> goodsAmount){ this.goodsAmount = goodsAmount; }

    public BigDecimal getTotalAmount(){return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount){ this.totalAmount = totalAmount; }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }
}
