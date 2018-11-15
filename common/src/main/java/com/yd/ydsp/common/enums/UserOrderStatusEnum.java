package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 18/7/24.
 * 独立商家模式的订单状态，注意这里的状态只表示当前流程节点，并不代表支付逻辑，支付成功结果由订单的isPay判断
 */
public enum UserOrderStatusEnum {
    /**前端显示状态：订单超时取消**/
    TIMEOUT("TIMEOUT", -1),//订单超时取消
    /**前端显示状态：消费者取消订单**/
    CANCEL("CANCEL", -2),//消费者取消
    /**前端显示状态：商家取消订单**/
    CANCELBYSHOP("CANCELBYSHOP", -3),//商家取消
    /**前端显示状态：等待商家接单**/
    WAITE("WAITE", 0),//等待商家接单
    /**前端显示状态：待付款**/
    WAITEPAYPRE("WAITEPAYPRE", 1),//订单创建待付款
    /**前端显示状态：待发货**/
    PAYFINISH("PAYFINISH", 2),//完成支付，等待商家发货
    /**前端显示状态：待发货**/
    READY("READY",3),//商家已确认接单，等待商家发货
    /**前端显示状态：已发货**/
    SENDOUT("SENDOUT", 5),//商家已经发货
    /**前端显示状态：申请退款**/
    REFUND("REFUND",6),//申请退款
    /**前端显示状态：退款中**/
    REFUNDING("REFUNDING",7),//退款中
    /**前端显示状态：已确认收货**/
    CONFIRM("CONFIRM", 8),//确认收货
    /**前端显示状态：待付款**/
    WAITEPAYPOST("WAITEPAYPOST", 9),//货到待付款
    /**前端显示状态：退款完成**/
    FINISHREFUND("FINISHREFUND", 999),//退款已完成
    /**前端显示状态：订单已完成**/
    FINISH("FINISH", 1000),//订单已经完成
    ;

    public String name;

    public Integer status;

    UserOrderStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (UserOrderStatusEnum compareEnum : UserOrderStatusEnum.values()) {
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

    public static UserOrderStatusEnum nameOf(String value){
        for (UserOrderStatusEnum compareEnum : UserOrderStatusEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static UserOrderStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (UserOrderStatusEnum compareEnum : UserOrderStatusEnum.values()) {
            if (compareEnum.getStatus().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
