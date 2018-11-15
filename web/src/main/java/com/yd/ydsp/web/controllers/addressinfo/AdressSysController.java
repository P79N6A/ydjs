package com.yd.ydsp.web.controllers.addressinfo;

import com.yd.ydsp.biz.address.AddressSysService;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/10/20.
 */
@Controller
@RequestMapping(value = "/paypoint/sys/address")
public class AdressSysController {
    public static final Logger logger = LoggerFactory.getLogger(AdressSysController.class);

    @Resource
    AddressSysService addressSysService;


    @RequestMapping(value = {"/ip.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map<String,Object>> getAddressFromIP(HttpServletRequest request){
        Result<Map<String,Object>> result = new Result<>();
        try{
            String ipStr = CookieUtils.getIpAddress(request);
            Map<String,Object> map = addressSysService.getAddressFromIP(ipStr);
            map.put("ip",ipStr);
            result.setResult(map);
            result.setSuccess(true);
        }catch (Exception ex){
            logger.error("getAddressFromIP is error: ",ex);
            result.setSuccess(false);
            result.setMsgInfo("获取地址出错，请稍后再试!");
        }
        return result;

    }


    @RequestMapping(value = {"/getProvinces.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List<Object>> getProvinces(HttpServletRequest request){
        Result<List<Object>> result = new Result<>();
        try{
            result.setResult(addressSysService.getProvinces());
            result.setSuccess(true);
        }catch (Exception ex){
            logger.error("getProvinces is error: ",ex);
            result.setSuccess(false);
            result.setMsgInfo("获取省份出错，请稍后再试!");
        }
        return result;

    }

    @RequestMapping(value = {"/getCity.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List<Object>> getCity(HttpServletRequest request, @RequestParam String province){
        Result<List<Object>> result = new Result<>();
        try{
            result.setResult(addressSysService.getCity(province));
            result.setSuccess(true);
        }catch (Exception ex){
            logger.error("getCity is error: ",ex);
            result.setSuccess(false);
            result.setMsgInfo("获取城区出错，请稍后再试!");
        }
        return result;

    }

    @RequestMapping(value = {"/geocode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> geocode(HttpServletRequest request,@RequestParam String address, @RequestParam String city){
        Result<Map> result = new Result<>();
        try{
            Map<String,String> map = addressSysService.getGeoCode(address, city);
            result.setResult(map);
            result.setSuccess(true);
        }catch (Exception ex){
            logger.error("geocode is error: ",ex);
            result.setSuccess(false);
            result.setMsgInfo("地址转换经纬度出错，请稍后再试!");
        }
        return result;

    }
}
