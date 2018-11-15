package com.yd.ydsp.web.controllers;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.yunprinter.FEPrinterService;
import com.yd.ydsp.common.enums.RoleTypeEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.passport.AuthPassport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/3/19.
 */

@Controller
public class testPage {

    public static final Logger log = LoggerFactory.getLogger(testPage.class);

    @Resource
    private Producer producerMq;
    @Resource
    private WeiXinPayService weiXinPayService;

//    @AuthPassport(roleType = {RoleTypeEnum.NORMAL,RoleTypeEnum.DEVICEOWNER})
    @RequestMapping(value = {"/test/mq/sender.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> handleMain(HttpServletRequest request,@RequestParam String info)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(info);
            Message msg = new Message( //
                    // Message所属的Topic
                    "ydmq",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    "ydpay",
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    info.getBytes());
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            msg.setKey("demo");
            SendResult sendResult = producerMq.send(msg);
            assert sendResult != null;

        }catch (Exception ex){
            result.setSuccess(false);
        }
        return result;
    }


//    @RequestMapping(value = {"/test/adduser"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Integer> addUser(HttpServletRequest request)
//    {
//        Result<Integer> result = new Result<>();
//        YdUserInfo userInfo = new YdUserInfo();
//        Map<String,Object> map = new HashMap<String,Object>();
//        userInfo.setOpenid("asfafafasf");
//        userInfo.setSex(1);
//        map.put("accessToken","asfafasf");
//        map.put("accessToken1","assdfasfafafasf");
//        Integer i = userinfoService.newUser(userInfo);
//
//        result.setSuccess(true);
//        return result;
//    }

    @RequestMapping(value = {"/test/logger.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> logger(@RequestParam String logtext,HttpServletRequest request){

        Result<Boolean> result= new Result<>();
        result.setSuccess(true);
        try {
            if(DiamondYdSystemConfigHolder.getInstance().getLogDebug()){
                log.info("前端日志："+logtext);
            }

        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
        }
        return result;

    }

    @RequestMapping(value = {"/test/qrcode.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> testQrcode(HttpServletRequest request,
                                     @RequestParam(required = false) String appid,
                                     @RequestParam(required = false) String mchId,
                                     @RequestParam String orderid){

        Result<String> result= new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(weiXinPayService.createBizPayQrCode(appid,mchId,orderid));

        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
        }
        return result;

    }

}
