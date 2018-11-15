package com.yd.ydsp.client.domian.openshop;

import java.math.BigDecimal;
import java.util.Date;

public class YdCpMemberCardVO {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 卡基本信息的id
     */
    private String cardid;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级
     */
    private Integer levelType;

    /**
     * 此会员卡类型设置的最小分数
      */
    private Integer minPoint;

    /**
     * 此会员卡类型设置的最大分数
     */
    private Integer maxPoint;

    /**
     * 多少分可以抵扣deductionMoney字段的金额
     */
    private Integer deductionPoint;

    /**
     * 可以抵扣的金额
     */
    private BigDecimal deductionMoney;

    /**
     * 可以累积的分数
     */
    private Integer accPoint;

    /**
     * 多少钱可以累积accPoint字段的分数
     */
    private BigDecimal accMoney;

    /**
     * 最大充值金额，为0或者null表示没有开通充值功能
     */
    private BigDecimal rechargeCardMaxMoney;

    /**
     * 持卡用户数
     */
    private Long userCount;

    /**
     * 充值用户数
     */
    private Long payUserCount;

    /**
     * 对应的会员卡背景图
     */
    private String picUrl;

    /**
     * 状态：0-启用;1-停用
     */
    private Integer status;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid == null ? null : cardid.trim();
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName == null ? null : levelName.trim();
    }

    public Integer getLevelType() {
        return levelType;
    }

    public void setLevelType(Integer levelType) {
        this.levelType = levelType;
    }

    public Integer getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Integer minPoint) {
        this.minPoint = minPoint;
    }

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }

    public Integer getDeductionPoint() {
        return deductionPoint;
    }

    public void setDeductionPoint(Integer deductionPoint) {
        this.deductionPoint = deductionPoint;
    }

    public BigDecimal getDeductionMoney() {
        return deductionMoney;
    }

    public void setDeductionMoney(BigDecimal deductionMoney) {
        this.deductionMoney = deductionMoney;
    }

    public Integer getAccPoint() {
        return accPoint;
    }

    public void setAccPoint(Integer accPoint) {
        this.accPoint = accPoint;
    }

    public BigDecimal getAccMoney() {
        return accMoney;
    }

    public void setAccMoney(BigDecimal accMoney) {
        this.accMoney = accMoney;
    }

    public BigDecimal getRechargeCardMaxMoney() {
        return rechargeCardMaxMoney;
    }

    public void setRechargeCardMaxMoney(BigDecimal rechargeCardMaxMoney) {
        this.rechargeCardMaxMoney = rechargeCardMaxMoney;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getPayUserCount() {
        return payUserCount;
    }

    public void setPayUserCount(Long payUserCount) {
        this.payUserCount = payUserCount;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}