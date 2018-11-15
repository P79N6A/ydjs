package com.yd.ydsp.biz.address.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.address.AddressSysService;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.HttpUtil;
import org.apache.http.client.fluent.Form;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/10/20.
 */
public class AddressSysServiceImpl implements AddressSysService {

    private String appName = "ydjs_geo_api";
    private String appKey = "0613c9ae7bbf4f72e42d8258c519b3dd";
    private String requestUrl = "http://restapi.amap.com/v3";
    private boolean cacheOn=false;
    private String provinceKey = "province";

    @Resource
    RedisManager redisManager;

    public void setAppName(String appName){
        this.appName = appName;
    }
    public void setAppKey(String appKey){
        this.appKey = appKey;
    }
    public void setCacheOn(boolean cacheOn){ this.cacheOn = cacheOn; }

    @Override
    public Map<String, Object> getAddressFromIP(String ip) {
        String url = this.requestUrl+"/ip";
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("ip",ip);
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> result = JSON.parseObject(resultStr,Map.class);
        return result;
    }

    @Override
    public List<Object> getProvinces() throws IOException, ClassNotFoundException {

        if(cacheOn){
            byte[] bToken = redisManager.get(SerializeUtils.serialize(this.provinceKey));
            if (bToken != null) {
                return (List<Object>) SerializeUtils.deserialize(bToken);
            }
        }

        List<Object> result = null;
        String url = this.requestUrl+"/config/district";
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("keywords","中国");
        form.add("subdistrict","1");
        form.add("extensions","base");
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(((String)resultMap.get("status")).equals("1")){
            result = (List<Object>) (((List<Map>)resultMap.get("districts")).get(0).get("districts"));
            if(cacheOn) {
                redisManager.set(SerializeUtils.serialize(this.provinceKey), SerializeUtils.serialize(result),0);
            }
        }
        return result;
    }

    @Override
    public List<Object> getCity(String province) throws IOException, ClassNotFoundException {
        if(cacheOn){
            byte[] bToken = redisManager.get(SerializeUtils.serialize(province));
            if (bToken != null) {
                return (List<Object>) SerializeUtils.deserialize(bToken);
            }
        }
        List<Object> result = null;
        String url = this.requestUrl+"/config/district";
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("keywords",province);
        form.add("subdistrict","1");
        form.add("extensions","base");
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(((String)resultMap.get("status")).equals("1")){
            result = (List<Object>) (((List<Map>)resultMap.get("districts")).get(0).get("districts"));
            if(cacheOn) {
                redisManager.set(SerializeUtils.serialize(province), SerializeUtils.serialize(result),86400);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> geocoder(String city, String address) {
        String url = this.requestUrl+"/geocode/geo";
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("address",address);
        form.add("city",city);
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> result = JSON.parseObject(resultStr,Map.class);
        return result;
    }

    @Override
    public Map<String, Object> convertFromBaidu(String lng, String lat) {
        String url = this.requestUrl+"/assistant/coordinate/convert";
        lng = AmountUtils.bigDecimal2Str(new BigDecimal(lng),6);
        lat = AmountUtils.bigDecimal2Str(new BigDecimal(lat),6);
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("locations",lng+","+lat);
        form.add("coordsys","baidu");
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(((String)resultMap.get("status")).equals("1")){
            result.put("success",true);
            String[] conver = ((String)resultMap.get("locations")).split(",");
            result.put("lng",conver[0]);
            result.put("lat",conver[1]);
            result.put("info",resultMap.get("info"));
        }else {
            result.put("success",false);
            result.put("info",resultMap.get("info"));
        }
        return result;
    }

    @Override
    public Map<String,String> getGeoCode(String address, String city) {
        String url = this.requestUrl+"/geocode/geo";
        Form form = Form.form();
        form.add("key",this.appKey);
        form.add("address",address);
        form.add("city",city);
        String resultStr = HttpUtil.post(url,form);
        Map<String,Object> resultMap = JSON.parseObject(resultStr,Map.class);

        Map<String,String> result = null;

        if(resultMap.containsKey("status")){
            if(StringUtil.equals((String)resultMap.get("status"),"1")){
                List<Object> objectList = (List)resultMap.get("geocodes");
                if(objectList.size()>0){
                    Map<String,Object> objectMap = (Map)objectList.get(0);
                    result = new HashMap<>();
                    String longAndLat = (String)objectMap.get("location");
                    String[] longLat = longAndLat.split(",",2);
                    if(longLat.length==2){
                        result.put(Constant.LONGITUDE,longLat[0]);
                        result.put(Constant.LATITUDE,longLat[1]);
                        result.put(Constant.ZIPCODE,(String) objectMap.get("adcode"));
                        result.put(Constant.CITYCODE,(String) objectMap.get("citycode"));
                    }else {
                        result = null;
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        AddressSysServiceImpl demo = new AddressSysServiceImpl();
    }
}
