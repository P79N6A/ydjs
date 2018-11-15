package com.yd.ydsp.web.controllers.monitor;

import com.yd.ydsp.biz.cp.CpMonitorService;
import com.yd.ydsp.biz.cp.model.CPMonitorVO;
import com.yd.ydsp.biz.pay.YdPayService;
import com.yd.ydsp.biz.pay.YeePayService;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/5
 */

@Controller
@RequestMapping(value = "/monitor/manager")
public class CPMonitorController {

    public static final Logger logger = LoggerFactory.getLogger(CPMonitorController.class);

    @Resource
    CpMonitorService cpMonitorService;
    @Resource
    YdPayService ydPayService;
    @Resource
    YeePayService yeePayService;

    @RequestMapping(value = {"/getShopInfo.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<CPMonitorVO> getShopInfo(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<CPMonitorVO> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpMonitorService.getShopMonitorInfo(shopid));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/getPlayAddress.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<String> getPlayAddress(HttpServletRequest request, @RequestParam String deviceSerial, @RequestParam Integer channelNo)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpMonitorService.getPlayAddress(deviceSerial,channelNo));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }


    @RequestMapping(value = {"/getChannelInfo.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Map> getChannelInfo(HttpServletRequest request, @RequestParam String deviceSerial, @RequestParam Integer channelNo)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpMonitorService.getChannelInfo(deviceSerial,channelNo));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/payCallBackTest.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String payCallBackTest(HttpServletRequest request, @RequestParam Integer orderType,@RequestParam String data,@RequestParam String encryptkey)
    {
        String result = "SUCCESS";
        try {
            PayOrderTypeEnum payOrderTypeEnum = PayOrderTypeEnum.nameOf(orderType);
            if(payOrderTypeEnum==null){
                return "orderType is error";
            }
            result = yeePayService.payCallback(data,encryptkey,payOrderTypeEnum);
        }catch (Exception ex){
            logger.error("",ex);
            result = "ERROR";
        }
        return result;
    }


}
