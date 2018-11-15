package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/17.
 * 引灯C端的订单状态
 */
public enum OrderStatusEnum {
    TIMEOUT("TIMEOUT", -2),//订单超时关闭
    OVER("OVER", -1),//订单关闭
    NEW("NEW", 0),//新订单，等待支付
    PAYFINISH("PAYFINISH", 1),//完成支付，等待商家接单
    READY("READY",2),//商家已确认接单
    DELIVERED("DELIVERED", 3),//已经发货
    REFUND("REFUND",4),//申请退款
    CONFIRM("CONFIRM", 5),//已经确认收货
    /**
     * 已经使用现金支付，更新状态时，如果是此状态，
     * 最终还是会设置订单状态为PAYFINISH，
     * 这里是为了区分支付的方式，以方便以便更瓣订单状态时，在订单上打上线下支付标记
     */
    PAYCASH("PAYCASH", 6),
    ;

    public String name;

    public Integer status;

    OrderStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (OrderStatusEnum compareEnum : OrderStatusEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                status = compareEnum.getStatus();
            }
        }
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static OrderStatusEnum nameOf(String value){
        for (OrderStatusEnum compareEnum : OrderStatusEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static OrderStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (OrderStatusEnum compareEnum : OrderStatusEnum.values()) {
            if (compareEnum.getStatus().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
