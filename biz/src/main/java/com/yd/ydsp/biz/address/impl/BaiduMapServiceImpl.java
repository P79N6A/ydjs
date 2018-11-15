package com.yd.ydsp.biz.address.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.address.BaiduMapService;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduMapServiceImpl implements BaiduMapService {

    String ak="HRaqEKMoc8AfSOCjMkiDVuhQbVu0cCl6";

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    // 对Map内所有value作utf8编码，拼接返回结果
    private String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    @Override
    public Map<String, Object> geocoder(String city, String address) throws UnsupportedEncodingException {
        Map paramsMap = new LinkedHashMap<String, String>();
        paramsMap.put("output", "json");
        paramsMap.put("ak", this.getAk());
        paramsMap.put("address",address);
        paramsMap.put("city",city);
        String paramsStr = this.toQueryString(paramsMap);
        String wholeStr = new String("/geocoder/v2/?" + paramsStr);
        String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
        String sn = this.MD5(tempStr);
        String resultStr = HttpUtil.get("http://api.map.baidu.com"+wholeStr+"&sn="+sn);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        return resultMap;

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        BaiduMapServiceImpl demo = new BaiduMapServiceImpl();
    }
}
