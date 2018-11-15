package com.yd.ydsp.biz.address;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface BaiduMapService {

    Map<String,Object> geocoder(String city,String address) throws UnsupportedEncodingException;

}
