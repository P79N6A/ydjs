package com.yd.ydsp.dal.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.security.util.SecurityUtil;

import java.math.BigDecimal;
import java.util.*;

public class YdConsumerOrder {
    private Long id;

    private String shopid;

    private String orderName;

    private Date orderDate;

    private String tableid;

    private String openid;

    private String orderid;

    private Integer orderType=0;

    private Integer useCash=0;

    private Integer payMode=0;

    /**
     * 订单打印单上的流水号
     */
    Integer printCount;

    private BigDecimal totalAmount;

    /**
     * 实际金额，包括了手续费
     */
    private BigDecimal disTotalAmount;

    /**
     * 手续费
     */
    private BigDecimal commissionCharge;

    private Integer status=0;

    /**
     * 购物车的json字串
     */
    private String feature;

    /**
     * 历史订单数据（如果员工进行了打折与退免操作，才会记录
     */
    private String history;

    /**
     * 备注
     */
    String description;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid == null ? null : tableid.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getUseCash() {
        return useCash;
    }

    public void setUseCash(Integer useCash) {
        this.useCash = useCash;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDisTotalAmount() {
        return disTotalAmount;
    }

    public void setDisTotalAmount(BigDecimal disTotalAmount) {
        this.disTotalAmount = disTotalAmount;
    }

    public BigDecimal getCommissionCharge() {
        if(commissionCharge==null){
            return new BigDecimal("0.00");
        }
        return commissionCharge;
    }

    public void setCommissionCharge(BigDecimal commissionCharge) {
        this.commissionCharge = commissionCharge;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history == null ? null : history.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
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

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setConsumerDesc(String info){
        if(StringUtil.isEmpty(info)){
            return;
        }
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            map = new HashMap<>();
        }
        map.put("consumerDesc",info);
        this.setDescription(JSON.toJSONString(map));
    }

    public String getConsumerDesc(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains("consumerDesc")){
            return null;
        }
        return SecurityUtil.escapeHtml((String) map.get("consumerDesc"));
    }

    public void setManagerOptionHistory(String info){
        if(StringUtil.isEmpty(info)){
            return;
        }
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            map = new HashMap<>();
        }
        List<String> managerDescList = this.getManagerOptionHistoryList();
        if(managerDescList==null){
            managerDescList = new ArrayList<>();
        }
        managerDescList.add(info);
        map.put("managerDesc",managerDescList);
        this.setDescription(JSON.toJSONString(map));
    }

    public String getManagerOptionHistory(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains("managerDesc")){
            return null;
        }
        String result = "";
        for (Object info: ((JSONArray)map.get("managerDesc")).toArray()){
            result = result+(String)info+"\\r\\n";
        }
        return result;
    }

    public List<String> getManagerOptionHistoryList(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains("managerDesc")){
            return null;
        }
        List<String> listInfo = new ArrayList<>();
        for (Object info: ((JSONArray)map.get("managerDesc")).toArray()){
            listInfo.add((String)info);
        }
        return listInfo;
    }

    public Map<String,Object> getDescription2Map(){
        if(StringUtil.isBlank(this.description)){
            return null;
        }
        Map<String, Object> map = null;
        try {
            map = JSON.parseObject(this.description, Map.class);
        }
        catch(Exception ex){

        }
        finally {
            return map;
        }
    }
}