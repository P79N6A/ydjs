package com.yd.ydsp.biz.deliver.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.deliver.UUDeliveryService;
import com.yd.ydsp.biz.deliver.model.UUCallbackData;
import com.yd.ydsp.client.domian.openshop.DeliveryOrderPriceVO;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.HttpUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import org.apache.http.client.fluent.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UUDeliveryServiceImpl implements UUDeliveryService {

    public static final Logger logger = LoggerFactory.getLogger(UUDeliveryServiceImpl.class);

    private String key;//appid
    private String secret;//appkey
    private String requestAdress="http://openapi.uupaotui.com";
    private String sourceId;//openid

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRequestAdress() {
        return requestAdress;
    }

    public void setRequestAdress(String requestAdress) {
        this.requestAdress = requestAdress;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private String getSignature(TreeMap<String,String> paramMap, boolean encode){

        List<String> keys = new ArrayList<String>(paramMap.keySet());
        // Collections.sort(keys);//不按首字母排序, 需要按首字母排序请打开
        StringBuilder prestrSB = new StringBuilder();
        for (int k = 0; k < keys.size(); k++) {
            String key = keys.get(k);
            String value = paramMap.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (k == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestrSB.append(key).append("=").append(value);
            } else {
                prestrSB.append(key).append("=").append(value).append("&");
            }
        }
        String result =  prestrSB.toString()+"&key="+this.getSecret();
        result = result.toUpperCase();
        return EncryptionUtil.md5Hex(result).toUpperCase();

    }

    protected Form reloadParams(TreeMap<String, String> params){
        Form form = Form.form();
        params.put("timestamp", DateUtils.getNowTimeStamp());
        params.put("openid",this.getKey());
        params.put("appid",this.getSourceId());
        params.put("nonce_str", "29"+RandomUtil.getNotSimple(9));
        String sign = this.getSignature(params,false);
        params.put("sign",sign);

        List<String> keys = new ArrayList<String>(params.keySet());
        for (int k= 0; k < keys.size(); k++) {
            String key = keys.get(k);
            String value = params.get(key);
            form.add(key,value);
        }

        return form;
    }

    @Override
    public DeliveryOrderPriceVO queryDeliverFee(String originId, String city, String county,
                                                String fromAddress, String fromUsernote,
                                                String fromLat, String fromLng,
                                                String toAddress, String toUsernote,
                                                String toLat, String toLng) {
        TreeMap<String,String> paramMap = new TreeMap<>();
        paramMap.put("origin_id",originId);
        paramMap.put("city_name",city);
        if(StringUtil.isNotBlank(county)) {
            paramMap.put("county_name", county);
        }
        paramMap.put("send_type","0");
        paramMap.put("from_address",fromAddress);
        if(StringUtil.isNotBlank(fromUsernote)) {
            paramMap.put("from_usernote", fromUsernote);
        }
        paramMap.put("to_address",toAddress);
        if(StringUtil.isNotBlank(toUsernote)){
            paramMap.put("to_usernote",toUsernote);
        }
        paramMap.put("subscribe_type","0");
        paramMap.put("to_lng",toLng);
        paramMap.put("to_lat",toLat);
        paramMap.put("from_lng",fromLng);
        paramMap.put("from_lat",fromLat);
        Form form = this.reloadParams(paramMap);
        String resultStr = HttpUtil.post(this.getRequestAdress()+"/v2_0/getorderprice.ashx",form);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(StringUtil.equals((String)resultMap.get("return_code"),"ok")){
            resultMap.put("success",true);
        }else {
            resultMap.put("success",false);
        }

        return null;
    }

    @Override
    public Map<String, Object> createOrder(String priceToken, String orderPrice, String balancePaymoney,
                                           String receiver, String receiverPhone, String note,
                                           String callbackUrl, String specialType, String callmeWithtake,
                                           String pubusermobile) {
        TreeMap<String,String> paramMap = new TreeMap<>();
        paramMap.put("price_token",priceToken);
        paramMap.put("order_price",orderPrice);
        paramMap.put("balance_paymoney",balancePaymoney);
        paramMap.put("receiver",receiver);
        paramMap.put("receiver_phone", receiverPhone);
        if(StringUtil.isNotBlank(note)){
            paramMap.put("note",note);
        }
        if(StringUtil.isNotBlank(callbackUrl)){
            paramMap.put("callback_url",callbackUrl);
        }
        paramMap.put("push_type","0");
        paramMap.put("special_type",specialType);
        paramMap.put("callme_withtake",callmeWithtake);
        paramMap.put("pubusermobile",pubusermobile);
        Form form = this.reloadParams(paramMap);
        String resultStr = HttpUtil.post(this.getRequestAdress()+"/v2_0/addorder.ashx",form);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(StringUtil.equals((String)resultMap.get("return_code"),"ok")){
            resultMap.put("success",true);
        }else {
            resultMap.put("success",false);
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> cancelOrder(String originId, String orderCode, String reason) {
        TreeMap<String,String> paramMap = new TreeMap<>();
        paramMap.put("origin_id",originId);
        if(StringUtil.isNotBlank(orderCode)){
            paramMap.put("order_code",orderCode);
        }
        if(StringUtil.isNotBlank(reason)){
            paramMap.put("reason",reason);
        }
        Form form = this.reloadParams(paramMap);
        String resultStr = HttpUtil.post(this.getRequestAdress()+"/v2_0/cancelorder.ashx",form);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(StringUtil.equals((String)resultMap.get("return_code"),"ok")){
            resultMap.put("success",true);
        }else {
            resultMap.put("success",false);
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> getOrderDetail(String originId, String orderCode) {
        TreeMap<String,String> paramMap = new TreeMap<>();
        paramMap.put("origin_id",originId);
        if(StringUtil.isNotBlank(orderCode)){
            paramMap.put("order_code",orderCode);
        }

        Form form = this.reloadParams(paramMap);
        String resultStr = HttpUtil.post(this.getRequestAdress()+"/v2_0/getorderdetail.ashx",form);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(StringUtil.equals((String)resultMap.get("return_code"),"ok")){
            resultMap.put("success",true);
        }else {
            resultMap.put("success",false);
        }

        return resultMap;
    }

    @Override
    public void uuCallback(UUCallbackData UUCallbackData) {

    }
}
