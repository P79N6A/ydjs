package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdPaypointWaresSkuExt {
    private Long id;

    private String skuid;

    private BigDecimal weight;

    private String volume;

    private BigDecimal discount;

    private String feature4;

    private String feature5;

    private String feature;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume == null ? null : volume.trim();
    }


    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


    public String getFeature4() {
        return feature4;
    }

    public void setFeature4(String feature4) {
        this.feature4 = feature4 == null ? null : feature4.trim();
    }

    public String getFeature5() {
        return feature5;
    }

    public void setFeature5(String feature5) {
        this.feature5 = feature5 == null ? null : feature5.trim();
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
}