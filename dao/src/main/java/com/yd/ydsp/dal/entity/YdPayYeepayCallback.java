package com.yd.ydsp.dal.entity;

import java.util.Date;

/**
 * @author zengyixun
 */
public class YdPayYeepayCallback {
    private Long id;

    private Long yborderid;

    private String orderid;

    private String bankcode;

    private String bank;

    private String lastno;

    private Integer cardtype;

    private Integer amount;

    private Integer paystatus;

    private Integer status;

    private Integer ordertype;

    private String sign;

    private String payurl;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getYborderid() {
        return yborderid;
    }

    public void setYborderid(Long yborderid) {
        this.yborderid = yborderid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode == null ? null : bankcode.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public String getLastno() {
        return lastno;
    }

    public void setLastno(String lastno) {
        this.lastno = lastno == null ? null : lastno.trim();
    }

    public Integer getCardtype() {
        return cardtype;
    }

    public void setCardtype(Integer cardtype) {
        this.cardtype = cardtype;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(Integer paystatus) {
        this.paystatus = paystatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    public String getPayurl() {
        return payurl;
    }

    public void setPayurl(String payurl) {
        this.payurl = payurl == null ? null : payurl.trim();
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