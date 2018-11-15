package com.yd.ydsp.dal.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.security.util.SecurityUtil;

import java.math.BigDecimal;
import java.util.*;

public class YdShopConsumerOrder {
    private Long id;

    private String shopid;

    private String orderName;

    /**
     * 可能是桌号id,渠道id,也可能是shopid，这个要根据第3与第4位数字来确定
     */
    private String channelId;

    private String openid;

    private String unionid;

    private String weixinConfigId;

    private String orderid;

    /**
     * 0-商家向引灯付款; 1-消费者向商家付款，但是由引灯代收款; 2-一个商家向另一个商家付款; 3-消费者向商家付款(商家自有的独立支付帐号)
     */
    private Integer orderType;

    /**
     * 支付模式，比如是不是立即付款;0:后付款；1:立即付款
     */
    private Integer payMode;

    /**
     * 订单打印单上的流水号
     */
    private Integer printCount;

    /**
     * -1:未支付; 0:线上支付；1:使用现金支付
     */
    private Integer isPay;

    private BigDecimal totalAmount;

    /**
     * 手续费
     */
    private BigDecimal commissionCharge;

    private Integer status;

    /**
     * 订单的json字串
     */
    private String feature;

    /**
     * 历史订单数据（如果员工进行了打折与退免操作，才会记录)
     */
    private String history;

    /**
     * 物流相关的JSON格式数据信息
     */
    private String delivery;

    /**
     * 使用map结构转为的json字串，保存微信与支付宝等扫码支持二维码的qrcodeurl
     */
    private String payqrcode;

    private String resultPayInfo;

    private String description;

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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId == null ? null : channelId.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId == null ? null : weixinConfigId.trim();
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

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history == null ? null : history.trim();
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery == null ? null : delivery.trim();
    }

    public String getPayqrcode() {
        return payqrcode;
    }

    public void setPayqrcode(String payqrcode) {
        this.payqrcode = payqrcode == null ? null : payqrcode.trim();
    }

    public String getResultPayInfo() {
        return resultPayInfo;
    }

    public void setResultPayInfo(String resultPayInfo) {
        this.resultPayInfo = resultPayInfo == null ? null : resultPayInfo.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setShopDesc(String info){
        if(StringUtil.isEmpty(info)){
            return;
        }
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            map = new HashMap<>();
        }
        map.put(Constant.SHOPDESC,info);
        this.setDescription(JSON.toJSONString(map));
    }

    public String getShopDesc(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains(Constant.SHOPDESC)){
            return null;
        }
        return SecurityUtil.escapeHtml((String) map.get(Constant.SHOPDESC));
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
        map.put(Constant.OPTIONLOG,managerDescList);
        this.setDescription(JSON.toJSONString(map));
    }

    public String getManagerOptionHistory(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains(Constant.OPTIONLOG)){
            return null;
        }
        String result = "";
        for (Object info: ((JSONArray)map.get(Constant.OPTIONLOG)).toArray()){
            result = result+(String)info+"\\r\\n";
        }
        return result;
    }

    public List<String> getManagerOptionHistoryList(){
        Map<String,Object> map = this.getDescription2Map();
        if(map==null){
            return null;
        }
        if(!map.keySet().contains(Constant.OPTIONLOG)){
            return null;
        }
        List<String> listInfo = new ArrayList<>();
        for (Object info: ((JSONArray)map.get(Constant.OPTIONLOG)).toArray()){
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