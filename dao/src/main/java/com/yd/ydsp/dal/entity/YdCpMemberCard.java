package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdCpMemberCard {
    private Long id;

    private String shopid;

    private String cardid;

    private String levelName;

    private Integer levelType;

    private Integer minPoint;

    private Integer maxPoint;

    private Integer deductionPoint;

    private Integer deductionMoney;

    private Integer accPoint;

    private Integer accMoney;

    private BigDecimal rechargeCardMaxMoney;

    /**
     * 持卡用户数
     */
    private Long userCount;

    /**
     * 充值用户数
     */
    private Long payUserCount;

    private String feature;

    private Integer status;

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

    public Integer getDeductionMoney() {
        return deductionMoney;
    }

    public void setDeductionMoney(Integer deductionMoney) {
        this.deductionMoney = deductionMoney;
    }

    public Integer getAccPoint() {
        return accPoint;
    }

    public void setAccPoint(Integer accPoint) {
        this.accPoint = accPoint;
    }

    public Integer getAccMoney() {
        return accMoney;
    }

    public void setAccMoney(Integer accMoney) {
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}