package com.yd.ydsp.dal.entity;

import com.yd.ydsp.common.security.util.SecurityUtil;

import java.io.Serializable;
import java.util.Date;

public class YdPaypointAgentapplyinfo implements Serializable {
    private static final long serialVersionUID =  -1431820772985284411L;
    private Long id;

    private String name;

    private String companyname;

    private String phone;

    private String ip;

    private String address;

    private Integer status;

    private String applyType;

    private String description;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return SecurityUtil.escapeHtml(name);
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCompanyname() {
        return SecurityUtil.escapeHtml(companyname);
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname == null ? null : companyname.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public String getPhone() {
        return SecurityUtil.escapeHtml(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getAddress() {
        return SecurityUtil.escapeHtml(address);
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getDescription() {
        return SecurityUtil.escapeHtml(description);
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getApplyType() {
        return SecurityUtil.escapeHtml(applyType);
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }
}