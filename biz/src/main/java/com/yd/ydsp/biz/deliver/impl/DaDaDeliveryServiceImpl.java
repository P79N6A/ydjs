package com.yd.ydsp.biz.deliver.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.deliver.DaDaDeliveryService;
import com.yd.ydsp.biz.deliver.model.DADACallbackData;
import com.yd.ydsp.biz.deliver.model.DaDaCityData;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.DeliveryOrderPriceVO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.DeliveryOrderStatusEnum;
import com.yd.ydsp.common.enums.DeliveryTypeEnum;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

public class DaDaDeliveryServiceImpl implements DaDaDeliveryService {

    public static final Logger logger = LoggerFactory.getLogger(DaDaDeliveryServiceImpl.class);

    @Resource
    private MqMessageService mqMessageService;

    private String key;
    private String secret;
    private String apiUrl;
    private String sourceid;
    private String apiVersion="1.0";
    private List<DaDaCityData> cityInfo;

    public void setKey(String key) {
        this.key = key;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }


    /**
     * 对数据进行初始化操作
     */
    void init(){

        this.getCityInfo();

    }


    private void getCityInfo(){
        try {
            String request = this.apiUrl + "/api/cityCode/list";
            String resultStr = HttpUtil.postJson(request, "");
            Map<String, Object> respone = JSON.parseObject(resultStr, Map.class);
            if (respone.containsKey("status")) {
                if (StringUtil.equals((String) respone.get("status"), "success")) {
                    this.cityInfo = JSON.parseArray(JSON.toJSONString(respone.get("result")),DaDaCityData.class);
                }
            }
        }catch (Exception e){
            logger.error("初始化达达，城市信息失败!");
        }
    }

    /**
     * 构造公共参数以及签名计算
     * @param body
     * @param sourceid
     * @param apiVersion
     * @return
     */
    protected String createPublicParam(String body,String sourceid,String apiVersion){

        HashMap<String,String> requestMap = new HashMap<>();
        requestMap.put("app_key",this.key);
        requestMap.put("body",body);
        requestMap.put("timestamp", DateUtils.getNowTimeStamp());
        requestMap.put("format","json");
        if(StringUtil.isEmpty(apiVersion)){
            requestMap.put("v",this.apiVersion);
        }
        if(StringUtil.isEmpty(sourceid)){
            requestMap.put("source_id",this.sourceid);
        }
        //Key值排序
        Collection<String> keySet = requestMap.keySet();
        List<String> list = new ArrayList<String>(keySet);
        Collections.sort(list);

        //拼凑签名字符串
        StringBuffer signStr = new StringBuffer();
        for(int i=0; i<list.size(); i++){
            String key = list.get(i);
            signStr.append(key + requestMap.get(key));
        }
        //MD5签名
        String mySign = EncryptionUtil.md5Hex(this.secret + signStr.toString() + this.secret);

        String sign = mySign.toUpperCase();
        requestMap.put("signature",sign);

        return JSON.toJSONString(requestMap);
    }

    protected String createPublicParam(String body){
        return this.createPublicParam(body,null,null);
    }

    protected  boolean callbackDataSign(HashMap<String,String> requestMap, String signature){
        boolean result = false;
        if(requestMap==null){
            return result;
        }

        //valueSet
        Collection<String> valueSet = requestMap.values();
        List<String> list = new ArrayList<String>(valueSet);
        Collections.sort(list);

        //拼凑签名字符串
        StringBuffer signStr = new StringBuffer();
        for(int i=0; i<list.size(); i++){
            signStr.append(list.get(i));
        }

        //MD5签名
        String mySign = EncryptionUtil.md5Hex(signStr.toString() );
        if(StringUtil.equals(mySign,signature)){
            result = true;
        }
        return result;

    }


    @Override
    public boolean queryStationIsExist(String channelId) throws Exception {
        boolean result = false;
        String request = this.apiUrl+"/api/shop/detail";
        Map<String,String> body = new HashMap<>();
        body.put("origin_shop_id",channelId);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")){
            if(StringUtil.equals((String)respone.get("status"),"success")){
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean createStation(String channelId, String stationName, Integer business, String cityName, String areaName, String stationAddress, Double lng, Double lat, String contactName, String phone) throws Exception {
        boolean result = false;
        String request = this.apiUrl+"/api/shop/add";
        List<Map> requestBody = new ArrayList<>();
        Map<String,Object> body = new HashMap<>();
        body.put("origin_shop_id",channelId);
        body.put("business",business);
        body.put("city_name",cityName);
        body.put("area_name",areaName);
        body.put("station_address",stationAddress);
        body.put("contact_name",contactName);
        body.put("phone",phone);
        body.put("lng",lng);
        body.put("lat",lat);
        requestBody.add(body);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(requestBody)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")){
            if(StringUtil.equals((String)respone.get("status"),"success")){
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean updateStation(String channelId, String cityName, String areaName, String stationAddress, Double lng, Double lat, String contactName, String phone) throws Exception {
        boolean result = false;
        String request = this.apiUrl+"/api/shop/update";
        Map<String,Object> body = new HashMap<>();
        body.put("origin_shop_id",channelId);
        if(StringUtil.isNotEmpty(cityName)) {
            body.put("city_name", cityName);
        }
        if(StringUtil.isNotEmpty(areaName)) {
            body.put("area_name", areaName);
        }
        if(StringUtil.isNotEmpty(stationAddress)) {
            body.put("station_address", stationAddress);
        }
        if(StringUtil.isNotEmpty(contactName)) {
            body.put("contact_name", contactName);
        }
        if(StringUtil.isNotEmpty(phone)) {
            body.put("phone", phone);
        }
        if(lng!=null) {
            body.put("lng", lng);
        }
        if(lat!=null) {
            body.put("lat", lat);
        }
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")){
            if(StringUtil.equals((String)respone.get("status"),"success")){
                result = true;
            }
        }
        return result;
    }

    @Override
    public DeliveryOrderPriceVO queryDeliverFee(String channelId, String orderid, String cityName, BigDecimal price, String receiverName, String receiverAddress,
                                                Double lng, Double lat, String receiverPhone, String info, Integer cargoType, Double cargoWeight) throws Exception {
        DeliveryOrderPriceVO result = null;

        String request = this.apiUrl+"/api/order/queryDeliverFee";
        Map<String,Object> body = new HashMap<>();
        String cityCode = this.getCityCode(cityName);
        if(StringUtil.isEmpty(cityCode)){
            throw new NullPointerException("citycode is null.");
        }
        body.put("shop_no",channelId);
        body.put("origin_id",orderid);
        body.put("city_code",cityCode);
        body.put("cargo_price",price.doubleValue());
        body.put("is_prepay",0);
        body.put("receiver_name",receiverName);
        body.put("receiver_address",receiverAddress);
        body.put("receiver_lng",lng);
        body.put("receiver_lat",lat);
        body.put("receiver_phone",receiverPhone);
        body.put("info",info);
        body.put("callback", DiamondYdSystemConfigHolder.getInstance().dadaCallbackPreUtl+orderid);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")) {
            if (StringUtil.equals((String) respone.get("status"), "success")) {
                Map<String,Object> resultMap = (Map<String, Object>) respone.get("result");
                result = new DeliveryOrderPriceVO();
                result.setClientId((String) resultMap.get("deliveryNo"));
                result.setPayMoney(AmountUtils.bigDecimalBy2(new BigDecimal((Double) resultMap.get("fee"))));
                result.setClientId((String) resultMap.get("deliveryNo"));
            }
        }

        return result;
    }

    @Override
    public boolean addAfterQuery(String deliveryNo) throws Exception {
        boolean result = false;
        String request = this.apiUrl+"/api/order/addAfterQuery";
        Map<String,Object> body = new HashMap<>();
        body.put("deliveryNo",deliveryNo);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")) {
            if (StringUtil.equals((String) respone.get("status"), "success")) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public UserOrderDeliveryInfoVO queryOrderInfo(String orderId) throws Exception {
        String request = this.apiUrl+"/api/order/status/query";
        Map<String,Object> body = new HashMap<>();
        body.put("order_id",orderId);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")) {
            if (StringUtil.equals((String) respone.get("status"), "success")) {
                return this.doMap((Map<String, Object>) respone.get("result"));
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> cancelOrder(String orderId, Integer reasonId, String reason) throws Exception {
        String request = this.apiUrl+"/api/order/formalCancel";
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        Map<String,Object> body = new HashMap<>();
        body.put("order_id",orderId);
        body.put("cancel_reason_id",reasonId);
        body.put("cancel_reason",reason);
        String resultStr = HttpUtil.postJson(request,this.createPublicParam(JSON.toJSONString(body)));
        Map<String,Object> respone = JSON.parseObject(resultStr,Map.class);
        if(respone.containsKey("status")) {
            if (StringUtil.equals((String) respone.get("status"), "success")) {
                Map resultMap = (Map) respone.get("result");
                result.put("success",true);
                result.put(Constant.DEDUCFEE,new BigDecimal((Double)resultMap.get("deduct_fee")));
            }
        }
        return result;
    }

    /**
     * 已取消：包括配送员取消、商户取消、客服取消、系统取消（比如：骑士接单后一直未取货）

     已过期：订单30分钟未被骑士接单，系统会自动将订单过期终结。

     妥投异常：配送员在收货地，无法正常送到用户手中（包括用户电话打不通、客户暂时不方便收件、客户拒收、货物有问题等等）
     * @param callbackData
     */
    @Override
    public void dadaCalback(DADACallbackData callbackData) throws Exception {
        //orderdelivery
        if(callbackData==null){
            return;
        }
        if(StringUtil.isEmpty(callbackData.getClient_id())||StringUtil.isEmpty(callbackData.getOrder_id())||callbackData.getUpdate_time()==null||StringUtil.isEmpty(callbackData.getSignature())){
            return;
        }

        HashMap<String,String> requestMap = new HashMap<>();
        requestMap.put("client_id",callbackData.getClient_id());
        requestMap.put("order_id",callbackData.getOrder_id());
        requestMap.put("update_time",callbackData.getUpdate_time().toString());
        if(!this.callbackDataSign(requestMap,callbackData.getSignature())){
            logger.error("达达回调签名不正确："+JSON.toJSONString(callbackData));
            return;
        }

        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG, MqTagEnum.ORDERDELIVERY);
        msgMap.put("orderid",callbackData.getOrder_id());

        DeliveryOrderStatusEnum orderStatusEnum = DeliveryOrderStatusEnum.nameOf(callbackData.getOrder_status());
        if(DeliveryOrderStatusEnum.WaiteDispatch==orderStatusEnum){

        }else if(DeliveryOrderStatusEnum.WaiteDispatch==orderStatusEnum){
            /**
             * 待派单对我们没意义，忽略
             */
            return;

        }else if(DeliveryOrderStatusEnum.WaitePickUpGood==orderStatusEnum||DeliveryOrderStatusEnum.Delivery==orderStatusEnum||
                DeliveryOrderStatusEnum.Finish==orderStatusEnum||DeliveryOrderStatusEnum.Cancel==orderStatusEnum||
                DeliveryOrderStatusEnum.TimeOut==orderStatusEnum||DeliveryOrderStatusEnum.ExceptionOrderComeBackOver==orderStatusEnum||
                DeliveryOrderStatusEnum.Fail==orderStatusEnum){

            UserOrderDeliveryInfoVO userOrderDeliveryInfoVO = this.queryOrderInfo(callbackData.getOrder_id());
            if(userOrderDeliveryInfoVO!=null) {
                msgMap.put(Constant.MQBODY,userOrderDeliveryInfoVO);
                String messageId = mqMessageService.sendMessage(callbackData.getClient_id()+orderStatusEnum.getStatus(),callbackData.getOrder_id(), MqTagEnum.ORDERDELIVERY, JSON.toJSONString(msgMap));
//                logger.info("dadaCalback message send over messageid is:" + messageId);
            }

        }else {
            logger.error("达达回调未知的订单状态："+JSON.toJSONString(callbackData));
        }

    }

    @Override
    public String getCityCode(String cityName) {
        if(this.cityInfo==null){
            this.getCityInfo();
        }
        if(this.cityInfo==null){
            return null;
        }

        for (DaDaCityData cityData : this.cityInfo){
            if(StringUtil.contains(cityName,cityData.getCityName())){
                return cityData.getCityCode();
            }
        }

        return null;
    }


    protected UserOrderDeliveryInfoVO doMap(Map<String,Object> resultMap){
        if(resultMap==null){
            return null;
        }

        UserOrderDeliveryInfoVO udi = new UserOrderDeliveryInfoVO();
        udi.setStatusCode((Integer)resultMap.get("statusCode"));
        udi.setStatusMsg((String)resultMap.get("statusMsg"));
        udi.setDeliveryType(DeliveryTypeEnum.DADA.getType());
        if(resultMap.containsKey("actualFee")){
            udi.setActualFee(new BigDecimal((Double)resultMap.get("actualFee")));
        }
        if(resultMap.containsKey("distance")){
            udi.setDistance((Double)resultMap.get("distance"));
        }
        if(resultMap.containsKey("transporterName")){
            udi.setTransporterName((String)resultMap.get("transporterName"));
        }

        if(resultMap.containsKey("transporterPhone")){
            udi.setTransporterPhone((String)resultMap.get("transporterPhone"));
        }

        if(resultMap.containsKey("transporterLng")){
            udi.setTransporterLng((String)resultMap.get("transporterLng"));
        }

        if(resultMap.containsKey("transporterLat")){
            udi.setTransporterLat((String)resultMap.get("transporterLat"));
        }

        if(resultMap.containsKey("createTime")){
            if(resultMap.get("createTime")!=null) {
                udi.setCreateTime(DateUtils.dateStrToUdate((String) resultMap.get("createTime")));
            }
        }

        if(resultMap.containsKey("acceptTime")){
            if(resultMap.get("acceptTime")!=null) {
                udi.setAcceptTime(DateUtils.dateStrToUdate((String) resultMap.get("acceptTime")));
            }
        }

        if(resultMap.containsKey("fetchTime")){
            if(resultMap.get("fetchTime")!=null) {
                udi.setFetchTime(DateUtils.dateStrToUdate((String) resultMap.get("fetchTime")));
            }
        }

        if(resultMap.containsKey("finishTime")){
            if(resultMap.get("finishTime")!=null) {
                udi.setFetchTime(DateUtils.dateStrToUdate((String) resultMap.get("finishTime")));
            }
        }

        if(resultMap.containsKey("cancelTime")){
            if(resultMap.get("cancelTime")!=null) {
                udi.setCancelTime(DateUtils.dateStrToUdate((String) resultMap.get("cancelTime")));
            }
        }

        if(resultMap.containsKey("orderFinishCode")){
            if(resultMap.get("orderFinishCode")!=null) {
                udi.setCancelReason((String) resultMap.get("orderFinishCode"));
            }
        }

        return udi;

    }


}
