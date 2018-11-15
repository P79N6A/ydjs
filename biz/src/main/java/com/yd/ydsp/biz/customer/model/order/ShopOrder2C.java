package com.yd.ydsp.biz.customer.model.order;


import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.common.constants.paypoint.ShopUserOrderFlagConstants;
import com.yd.ydsp.common.enums.DeliveryTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 订单结构
 */
public class ShopOrder2C implements Serializable {

    /**
     * 订单名称，以其中一个商品名称为名称再加上有多少商品来定名称
     */
    String orderName;

    /**
     * 订单类型(PayOrderTypeEnum)
     */
    Integer orderType;

    /**
     * -1：未支付；0:线上支付完成；1:线下使用现金支付完成; 2:使用帐户余额支付
     */
    Integer isPay;

    /**
     * 订单创建时间
     */
    Date orderDate;

    /**
     * 订单状态
     */
    Integer status;

    /**
     * 用来表示此订单为今天的这个商家的第几单（流水号），用于店家接单时判定先后关系
     * 每日重新置为0
     */
    Integer printCount;

    /**
     * 扫描码(入口码）tableid或者channelId
     */
    String enterCode;
    /**
     * 渠道或者桌位名称
     */
    String enterName;

    /**
     * 订单编号
     */
    String orderid;
    /**
     * 支付模式，比如是不是立即付款;0:后付款；1:立即付款  (配合订单状态使用，如果为0，表示货到付款订单)
     */
    Integer payMode;

    String shopid;
    BigDecimal totalAmount;
    BigDecimal disTotalAmount;

    /**
     * 折扣
     */
    Double discount;

    /**
     * 使用多少点积分
     */
    Integer usePoint;

    /**
     * 本次订单获得了多少积分
     */
    Integer accPoint;

    /**
     * 用积分抵扣了多少金额
     */
    BigDecimal deMoney;

    /**
     * skuid,VO
     */
    List<ShoppingOrderSkuVO> shoppingOrderSkuVOList;

    /**
     * 用户备注:不能超过100长度
     */
    String description;

    /**
     * 商家备注
     */
    String cpDesc;

    /**
     * 订单配送方式
     */
    Integer deliveryType;

    /**
     * 打标字段
     */
    private Long flag=0L;

    /**
     * 是不是店内（堂食)订单   0-不是; 1-是; 2-收钱宝（也是在店内）;3-充值订单
     */
    Integer isIndoor;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getEnterCode() {
        return enterCode;
    }

    public void setEnterCode(String enterCode) {
        this.enterCode = enterCode;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public BigDecimal getDisTotalAmount() {
        return disTotalAmount;
    }

    public void setDisTotalAmount(BigDecimal disTotalAmount) {
        this.disTotalAmount = disTotalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<ShoppingOrderSkuVO> getShoppingOrderSkuVOList() {
        return shoppingOrderSkuVOList;
    }

    public void setShoppingOrderSkuVOList(List<ShoppingOrderSkuVO> shoppingOrderSkuVOList) {
        this.shoppingOrderSkuVOList = shoppingOrderSkuVOList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPrintCount() {
        if(printCount==null){
            return 0;
        }
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descption) {
        this.description = descption;
    }

    public Integer getUsePoint() {
        return usePoint;
    }

    public void setUsePoint(Integer usePoint) {
        this.usePoint = usePoint;
    }

    public BigDecimal getDeMoney() {
        return deMoney;
    }

    public void setDeMoney(BigDecimal deMoney) {
        this.deMoney = deMoney;
    }

    public Integer getDeliveryType() {
        if (deliveryType==null){
            return DeliveryTypeEnum.SHANGJIAPEI.getType();
        }
        return deliveryType;
    }

    public Integer getAccPoint() {
        return accPoint;
    }

    public void setAccPoint(Integer accPoint) {
        this.accPoint = accPoint;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    /**
     * 添加flag标记
     */
    public void addFlag(ShopUserOrderFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag | f.getValue();
    }

    /**
     * 删除flag标记
     */
    public void removeFlag(ShopUserOrderFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag & ~f.getValue();
    }

    /**
     *判断是否存在某个标志
     */
    public boolean isExistFlag(ShopUserOrderFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
    }

    public String getCpDesc() {
        if(cpDesc==null){
            return "";
        }
        return cpDesc;
    }

    public void setCpDesc(String cpDesc) {
        this.cpDesc = cpDesc;
    }

    public Integer getIsIndoor() {
        return isIndoor;
    }

    public void setIsIndoor(Integer isIndoor) {
        this.isIndoor = isIndoor;
    }
}
