package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.client.domian.UserAddressInfoVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zengyixun
 * @date 18/1/16
 */
public class CpMallOrderVO implements Serializable {

    private String shopid;

    /**
     * 订单id
     */
    private String orderid;

    /**
     * 订单金额:元
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private Integer status=0;

    /**
     * 用户收货地址信息
     */
    private UserAddressInfoVO addressInfoVO;


    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 套餐升级类型
     */
    private String upgradeType;
    /**
     * 返回金额：元
     */
    private BigDecimal upgradeTotalAmount;

    /**
     * 套餐升级的天数
     */
    private Integer remanent=0;

    private Integer bagNumber=0;
    /**
     * 返回金额：元
     */
    private BigDecimal bagTotalAmount;

    /**
     * 共几件商品
     */
    private Integer orderCount=0;

    private String buyerMessage;


    /**
     * 子订单，一般是需要送货的实体商品
     */
    private List<CpMallSubOrderVO> subOrders;

    public String getShopid(){ return shopid; }
    public void setShopid(String shopid){ this.shopid = shopid; }

    public String getOrderid(){ return orderid; }
    public void setOrderid(String orderid){ this.orderid = orderid; }

    public BigDecimal getTotalAmount(){ return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount){ this.totalAmount = totalAmount; }

    public Integer getStatus(){ return status; }
    public void setStatus(Integer status){ this.status = status; }

    public UserAddressInfoVO getAddressInfoVO(){return addressInfoVO; }
    public void setAddressInfoVO(UserAddressInfoVO addressInfoVO){this.addressInfoVO = addressInfoVO; }

    public Date getCreateDate(){ return createDate; }
    public void setCreateDate(Date createDate){ this.createDate = createDate; }

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

    public List<CpMallSubOrderVO> getSubOrders(){ return subOrders; }
    public void setSubOrders(List<CpMallSubOrderVO> subOrders){ this.subOrders = subOrders; }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }
}
