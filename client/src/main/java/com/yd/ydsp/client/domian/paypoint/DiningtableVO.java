package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 18/1/12
 */
public class DiningtableVO implements Serializable {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 桌位id
     */
    private String tableid;

    /**
     * 扫码id
     */
    private String qrcode;

    /**
     * 桌位名称
     */
    private String name;

    /**
     * 桌位能坐多少人
     */
    private Integer pnumber=1;

    /**
     * 排序字段
     */
//    private Integer seq=1;

    /**
     * 状态
     */
    private Integer status=0;

    /**
     * 绑定的后厨房打印机id
     * @return
     */
    private String kitchenPrintId;

    /**
     * 绑定的接单打印机id
     * @return
     */
    private String cashierPrintId;

    private String qrCodeUrl;


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid == null ? null : tableid.trim();
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPnumber() {
        return pnumber;
    }

    public void setPnumber(Integer pnumber) {
        this.pnumber = pnumber;
    }

//    public Integer getSeq() {
//        return seq;
//    }
//
//    public void setSeq(Integer seq) {
//        this.seq = seq;
//    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKitchenPrintId() {
        return kitchenPrintId;
    }

    public void setKitchenPrintId(String kitchenPrintId) {
        this.kitchenPrintId = kitchenPrintId;
    }

    public String getCashierPrintId() {
        return cashierPrintId;
    }

    public void setCashierPrintId(String cashierPrintId) {
        this.cashierPrintId = cashierPrintId;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
