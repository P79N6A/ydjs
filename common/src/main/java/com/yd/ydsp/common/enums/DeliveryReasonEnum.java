package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum DeliveryReasonEnum {
    Unanswered("没有接单", 1),
    NoPickUp("没有收货", 2),
    BadAttitude("配送员态度差", 3),
    CancelOrderByConsumer("顾客取消订单", 4),
    OrderIsError("订单填写错误", 5),
    CancelOrderByDistributor("配送员叫我取消订单",34 ),
    Nodoortodoor("配送员不愿意上门取货",35),
    NotNeedDelivery("商家不需要配送了",36),
    RejectByDistributor("配送员以各种理由表示无法完成订单",36),
    OtherReason("其它原因", 10000),
    ;

    public String reason;

    public Integer id;

    DeliveryReasonEnum(String reason, Integer id) {
        this.reason = reason;
        this.id = id;
    }

    public static Integer getStatusByName(String reason) {
        Integer id = null;
        for (DeliveryReasonEnum compareEnum : DeliveryReasonEnum.values()) {
            if (compareEnum.getReason().equals(reason)) {
                id = compareEnum.getId();
            }
        }
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
