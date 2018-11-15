package com.yd.ydsp.biz.address;

import com.yd.ydsp.client.domian.UserAddressInfoVO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/10/20.
 */
public interface AddressSysService {
/**
 * 此接口全是系统层面的地址服务，比如经纬度，IP，省市区、地址搜索等接口数据获取与转换
 */


    Map<String,Object> getAddressFromIP(String ip);
    List<Object> getProvinces() throws IOException, ClassNotFoundException;
    List<Object> getCity(String province) throws IOException, ClassNotFoundException;

    Map<String,Object> geocoder(String city,String address);

    /**
     * 百度坐标转为高德坐标
     * @param lng
     * @param lat
     * @return
     */
    Map<String,Object> convertFromBaidu(String lng,String lat);

    /**
     *
     * @param address  填写结构化地址信息:省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码
     * @param city  查询城市，可选：城市中文、中文全拼、citycode、adcode，无此字段值时，会在全国范围查找
     * @return  108.777375,29.521875
     */
    Map<String,String> getGeoCode(String address,String city);

}
