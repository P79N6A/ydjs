package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdPrintOrderLog {
    private Long id;

    private String orderid;

    private String printOrderid;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public String getPrintOrderid() {
        return printOrderid;
    }

    public void setPrintOrderid(String printOrderid) {
        this.printOrderid = printOrderid == null ? null : printOrderid.trim();
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