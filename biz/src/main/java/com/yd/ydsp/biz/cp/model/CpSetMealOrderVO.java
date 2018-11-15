package com.yd.ydsp.biz.cp.model;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 17/12/15
 */
public class CpSetMealOrderVO implements Serializable {
    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 订单id
     */
    private String orderid;

    /**
     * 订单金额
     */
    private Integer totalAmount;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单名称
     */
    private String name;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 合约Id，每一种不同的合同会统一有一个id，以此来看当时用户约的是哪个合同
     */
    private String contractId;

    private String setMealEndDate;


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name == null ? null : name.trim(); }

    public String getOrderType(){ return orderType; }

    public void setOrderType(String orderType){ this.orderType = orderType == null ? null : orderType.trim().toUpperCase(); }

    public String getContractId(){ return contractId; }

    public void setContractId(String contractId){ this.contractId = contractId; }

    public String getSetMealEndDate(){ return setMealEndDate; }

    public void setSetMealEndDate(String setMealEndDate){ this.setMealEndDate = setMealEndDate; }


}
