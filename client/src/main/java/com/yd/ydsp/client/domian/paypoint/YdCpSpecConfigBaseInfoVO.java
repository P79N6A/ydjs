package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;

import java.io.Serializable;
import java.math.BigDecimal;

public class YdCpSpecConfigBaseInfoVO implements Serializable {

    /**
     * 选择的主规格item
     */
    private String mainSpecName;

    /**
     * 选择的子规格item
     */
    private String childSpecName;

    /**
     * 设置的价格
     */
    private BigDecimal price;

    /**
     * 规格库存
     */
    private Integer inventory=999;

    public String getMainSpecName() {
        return mainSpecName;
    }

    public void setMainSpecName(String mainSpecName) {
        this.mainSpecName = mainSpecName == null ? null : mainSpecName.trim();
    }

    public String getChildSpecName() {
        return childSpecName;
    }

    public void setChildSpecName(String childSpecName) {
        if(StringUtil.isNotEmpty(this.mainSpecName)) {
            this.childSpecName = childSpecName == null ? "" : childSpecName.trim();
        }else {
            this.childSpecName = childSpecName == null ? null : childSpecName.trim();
        }
    }

    public BigDecimal getPrice() {
        if(this.price!=null){
            this.price = AmountUtils.bigDecimalBy2(this.price);
        }
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }
}
