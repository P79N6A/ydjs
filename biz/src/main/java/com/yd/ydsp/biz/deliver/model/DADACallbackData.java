package com.yd.ydsp.biz.deliver.model;

import java.io.Serializable;

public class DADACallbackData implements Serializable {
    private static final long serialVersionUID = 6000000000000000008L;


    /**
     * 对client_id, order_id, update_time的值进行字符串升序排列，再连接字符串，取md5值
     */
    String signature;

    /**
     * 返回达达运单号，默认为空
     */
    String client_id;
    /**
     * 添加订单接口中的origin_id值
     */
    String order_id;
    /**
     * 订单状态(待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10 创建达达运单失败=1000 可参考文末的状态说明）
     */
    Integer order_status;
    /**
     * 订单取消原因,其他状态下默认值为空字符串
     */
    String cancel_reason;
    /**
     * 订单取消原因来源(1:达达配送员取消；2:商家主动取消；3:系统或客服取消；0:默认值)
     */
    Integer cancel_from;
    /**
     * 更新时间,时间戳
     */
    Integer update_time;
    /**
     * 达达配送员id，接单以后会传
     */
    Integer dm_id;
    /**
     * 配送员姓名，接单以后会传
     */
    String dm_name;
    /**
     * 配送员手机号，接单以后会传
     */
    String dm_mobile;


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public Integer getCancel_from() {
        return cancel_from;
    }

    public void setCancel_from(Integer cancel_from) {
        this.cancel_from = cancel_from;
    }

    public Integer getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Integer update_time) {
        this.update_time = update_time;
    }

    public Integer getDm_id() {
        return dm_id;
    }

    public void setDm_id(Integer dm_id) {
        this.dm_id = dm_id;
    }

    public String getDm_name() {
        return dm_name;
    }

    public void setDm_name(String dm_name) {
        this.dm_name = dm_name;
    }

    public String getDm_mobile() {
        return dm_mobile;
    }

    public void setDm_mobile(String dm_mobile) {
        this.dm_mobile = dm_mobile;
    }
}
