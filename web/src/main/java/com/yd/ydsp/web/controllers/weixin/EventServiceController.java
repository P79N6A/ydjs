package com.yd.ydsp.web.controllers.weixin;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.weixin.WeixinEventService;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.WeixinOauth2ShopService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.enums.paypoint.WeiXinTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.WeiXinMessageUtil;
import com.yd.ydsp.common.weixin.CustomTextMsg;
import com.yd.ydsp.common.weixin.TextMessage;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zengyixun on 17/3/11.
 */

@Controller
public class EventServiceController {

    @Resource
    private WeixinEventService weixinEventService2b;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2b;

    @Resource
    private WeixinEventService weixinEventService2c;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2c;

    @Resource
    private WeixinOauth2ShopService weixinOauth2ShopService;
    @Resource
    private WeiXinPayService weiXinPayService;
    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;

//    @Resource
//    private SendMessage sendMessage;

//    @Resource
//    private SmsMessageService smsMessageService;


    public static final Logger logger = LoggerFactory.getLogger(EventServiceController.class);

    //用于微信第三方开放平台全网发布时的测试
    public static String querAuthCode;

//    @RequiresRoles("administrator")
    @RequestMapping(value = {"/paypoint/aliyun/isok.html"}, method = {RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
    public String handleMain(@RequestParam(required = false) String info,HttpServletRequest request,Model model)
    {
//        Map<String,String> map = new HashMap<>();
//        map.put("result","引灯前行，不忘初心!");
//        map.put("message","haha!");
//        Result<Map> result = new Result<>();
//        result.setSuccess(true);
//        result.setResult(map);
//        return result;
        if(StringUtil.isNotEmpty(info)){
            model.addAttribute("info",info);
        }else {
            model.addAttribute("info","引灯科技，照亮商家营销路！");
        }
        return "weixin/demo";
    }


    @RequestMapping(value = {"/yd/vg"}, method = RequestMethod.POST)
    @ResponseBody
    public String vgQrcodeRequest(@RequestBody String body,HttpServletRequest request)
    {
        String result = "code=0000&&desc=ok";
        try {

            Map<String,Object> argsInfo = new HashMap<>();
            String[] keyAndValue = body.split("&&");
            for (String info : keyAndValue){
                String[] arg = info.split("=",2);
                if(arg.length==2) {
                    argsInfo.put(arg[0],arg[1]);
                }else if(arg.length==1) {
                    argsInfo.put(arg[0],null);
                }
            }


            logger.info("vg body is :"+JSON.toJSONString(argsInfo));

        }catch (Exception e){

            result = "code=1000&&desc=error";
            logger.error("ydvg is error:",e);
        }

        return result;

    }

//    @RequestMapping(value ={"/paypoint/aliyun/sms.do"},method = RequestMethod.GET)
//    @ResponseBody
//    public String configInfo(HttpServletRequest request,@RequestParam(required=false)String code){
//        try {
//            smsMessageService.identitySmsMessage("15067105637",code);
//        }catch (Exception ex){
//
//        }
//        return code;
//    }


    @RequestMapping(value = {"/wex/ydcp.do"}, method = RequestMethod.GET)
    @ResponseBody
    public String checkTokenCP(@RequestParam(required=false)String signature,@RequestParam(required=false)String timestamp,
                             @RequestParam(required=false)String nonce,@RequestParam(required=false)String echostr,
                             HttpServletRequest request)
    {
//        Map<String,String> map = new HashMap<>();
//        map.put("result","欢迎光临鹰眼数据");
//        map.put("message","haha!");
//        Result<Map> result = new Result<>();
//        result.setSuccess(true);
//        result.setDefaultModel(map);
        logger.error("checkTokenCP begin:");
        logger.error(timestamp);
        logger.error(nonce);
        logger.error(echostr);
        String[] arr=new String[] {weixinOauth2Service2b.getWeiXinMsgToken(),timestamp,nonce};
        Arrays.sort(arr);
        if(StringUtil.equals(EncryptionUtil.sha1Hex(arr[0]+arr[1]+arr[2]),signature)) {
            return echostr;
        }else {
            String ip = CookieUtils.getIpAddress(request);
            logger.error("checkTokenCP 非法调用B端的IP地址为：" + ip);
            return "error";
        }
    }


    @RequestMapping(value = {"/wex/yd2c.do"}, method = RequestMethod.GET)
    @ResponseBody
    public String checkToken2C(@RequestParam(required=false)String signature,@RequestParam(required=false)String timestamp,
                             @RequestParam(required=false)String nonce,@RequestParam(required=false)String echostr,
                             HttpServletRequest request)
    {
//        Map<String,String> map = new HashMap<>();
//        map.put("result","欢迎光临鹰眼数据");
//        map.put("message","haha!");
//        Result<Map> result = new Result<>();
//        result.setSuccess(true);
//        result.setDefaultModel(map);
        String[] arr=new String[] {weixinOauth2Service2c.getWeiXinMsgToken(),timestamp,nonce};
        Arrays.sort(arr);
        if(StringUtil.equals(EncryptionUtil.sha1Hex(arr[0]+arr[1]+arr[2]),signature)) {
            return echostr;
        }else {
            String ip = CookieUtils.getIpAddress(request);
            logger.error("checkToken2C 非法调用B端的IP地址为：" + ip);
            return "error";
        }
    }


    @RequestMapping(value = {"/wex/ydcp.do"}, method = RequestMethod.POST)
    @ResponseBody
    public String receiveYDCPWeiXinMessage(
            HttpServletRequest request, @RequestBody String message)
    {
        String encryptMsg = "";
        try

        {
            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 开始验证微信消息是否真实
            String[] arr=new String[] {weixinOauth2Service2b.getWeiXinMsgToken(),timestamp,nonce};
            Arrays.sort(arr);
            if(StringUtil.equals(EncryptionUtil.sha1Hex(arr[0]+arr[1]+arr[2]),signature)) {
                System.out.println("msg=" + message);
                encryptMsg = weixinEventService2b.ReceiveEventHandle(message);
                String ip = CookieUtils.getIpAddress(request);
                logger.debug("CPReceiveEvent调用的IP地址为：" + ip);
            }else {
                String ip = CookieUtils.getIpAddress(request);
                logger.error("CPReceiveEvent非法调用B端的IP地址为：" + ip);
            }

        }catch (Exception e){
            logger.error("CPReceiveEvent Error: ",e);
        }
        return encryptMsg;

    }


    @RequestMapping(value = {"/wex/yd2c.do"}, method = RequestMethod.POST)
    @ResponseBody
    public String receiveYD2CWeiXinMessage(
            HttpServletRequest request, @RequestBody String message)
    {
        String encryptMsg = "";
        try

        {
            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 开始验证微信消息是否真实
            String[] arr=new String[] {weixinOauth2Service2c.getWeiXinMsgToken(),timestamp,nonce};
            Arrays.sort(arr);
            if(StringUtil.equals(EncryptionUtil.sha1Hex(arr[0]+arr[1]+arr[2]),signature)) {
                System.out.println("msg=" + message);
                encryptMsg = weixinEventService2c.ReceiveEventHandle(message);
                String ip = CookieUtils.getIpAddress(request);
                System.out.println("调用的IP地址为：" + ip);
            }else {
                String ip = CookieUtils.getIpAddress(request);
                logger.error("非法调用C端的IP地址为：" + ip);
            }

        }catch (Exception e){
            logger.error("ReceiveEvent Error: ",e);
        }
        return encryptMsg;

    }



//    @RequestMapping(value = {"/wex/shop/{weixinConfigId}"}, method = RequestMethod.GET)
//    @ResponseBody
//    public String checkTokenByShop(@PathVariable String weixinConfigId,
//                                       @RequestParam(required=false)String signature,@RequestParam(required=false)String timestamp,
//                               @RequestParam(required=false)String nonce,@RequestParam(required=false)String echostr,
//                               HttpServletRequest request)
//    {
//        logger.info("checkTokenByShop begin:");
//        logger.info(timestamp);
//        logger.info(nonce);
//        logger.info(echostr);
//        try {
//            String[] arr = new String[]{weixinOauth2ShopService.getWeiXinMsgToken(weixinConfigId), timestamp, nonce};
//            Arrays.sort(arr);
//            if (StringUtil.equals(EncryptionUtil.sha1Hex(arr[0] + arr[1] + arr[2]), signature)) {
//                return echostr;
//            } else {
//                String ip = CookieUtils.getIpAddress(request);
//                logger.error("checkTokenByShop 非法调用WeixinShop端的IP地址为：" + ip);
//                return "error";
//            }
//        }catch (Exception ex){
//            return "error";
//        }
//    }

//    @RequestMapping(value = {"/wex/shop/{weixinConfigId}"}, method = RequestMethod.POST)
//    @ResponseBody
//    public String receiveWeiXinMessage(@PathVariable String weixinConfigId,
//            HttpServletRequest request, @RequestBody String message)
//    {
//        String encryptMsg = "";
//        try
//
//        {
//            // 微信加密签名
//            String signature = request.getParameter("signature");
//            // 时间戳
//            String timestamp = request.getParameter("timestamp");
//            // 随机数
//            String nonce = request.getParameter("nonce");
//            // 开始验证微信消息是否真实
//            String[] arr=new String[] {weixinOauth2ShopService.getWeiXinMsgToken(weixinConfigId),timestamp,nonce};
//            Arrays.sort(arr);
//            if(StringUtil.equals(EncryptionUtil.sha1Hex(arr[0]+arr[1]+arr[2]),signature)) {
//                System.out.println("msg=" + message);
//                String weixinType = weixinOauth2ShopService.getWeiXinType(weixinConfigId);
//                encryptMsg = weixinEvent2ShopService.ReceiveEventHandle(weixinConfigId,weixinType,message);
//                String ip = CookieUtils.getIpAddress(request);
//                System.out.println("调用的IP地址为：" + ip);
//            }else {
//                String ip = CookieUtils.getIpAddress(request);
//                logger.error("非法调用C端的IP地址为：" + ip);
//            }
//
//        }catch (Exception e){
//            logger.error("ReceiveEvent Error: ",e);
//        }
//        return encryptMsg;
//
//    }


    @RequestMapping(value = {"/wex/open/events"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String receiveWeiXinOpenEvents(@RequestParam("timestamp")String timestamp, @RequestParam("nonce")String nonce,
                                          @RequestParam("msg_signature")String msgSignature, @RequestBody String postData)
    {
        String encryptMsg = "";
        try

        {

            encryptMsg = weixinOauth2ShopService.decryptMsg(msgSignature,timestamp,nonce,postData);
            logger.info("events is :"+encryptMsg);
            if(StringUtil.contains(encryptMsg,"ComponentVerifyTicket")) {
                Map<String, Object> dataMap = WeiXinMessageUtil.getInstance().xml2map(encryptMsg);
                String verifyTicket = ((HashMap<String, String>) dataMap.get("ComponentVerifyTicket")).get("ComponentVerifyTicket");
                weixinOauth2ShopService.setComponentVerifyTicket(verifyTicket);
            }


        }catch (Exception e){
            logger.error("receiveWeiXinOpenEvents Error: ",e);
        }
        return "success";

    }


    @RequestMapping(value = {"/wex/open/getPreAuthCode"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String getPreAuthCode()
    {
        try

        {
            String code = weixinOauth2ShopService.getPreAuthCode();
            return code;

        }catch (Exception e){
            logger.error("getPreAuthCode Error: ",e);
            return "error";
        }


    }

    /**
     * 我们作为微信的第三方开发商，代微信公众号与小程序商家接收消息，处理消息
     * @param appid
     * @param timestamp
     * @param nonce
     * @param msgSignature
     * @param postData
     * @return
     */
    @RequestMapping(value = {"/wex/open/{appid}/callback"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String receiveWeiXinOpenMsg(@PathVariable("appid") String appid,@RequestParam(value = "timestamp",required = false)String timestamp, @RequestParam(value = "nonce",required = false)String nonce,
                                          @RequestParam(value = "msg_signature",required = false)String msgSignature, @RequestBody String postData)
    {
        try

        {
            String encryptMsg = weixinOauth2ShopService.decryptMsg(msgSignature,timestamp,nonce,postData);
            logger.info("encryptMsg is :" +encryptMsg);
            Map<String,Object> rMsg = WeiXinMessageUtil.getInstance().xml2map(encryptMsg);

            if(rMsg!=null){
                logger.info("receiveWeiXinOpenMsg is : "+JSON.toJSONString(rMsg));
            }
            /**
             * 全网测试
             */
            if(StringUtil.equals(appid,"wx570bc396a51b8ff8")||StringUtil.equals(appid,"wxd101a85aa106f53e")) {
                if (StringUtil.contains(encryptMsg, "QUERY_AUTH_CODE")) {
                    this.querAuthCode = (String) ((Map) rMsg.get("Content")).get("Content");
                    String[] authCodeSplit = this.querAuthCode.split(":");
                    this.sendWeiXinTestMsg(appid, (String) ((Map) rMsg.get("FromUserName")).get("FromUserName"), authCodeSplit[1] + "_from_api");
                    return "";
                }


                if (StringUtil.contains(encryptMsg, "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                    String toUserName = (String) ((Map) rMsg.get("ToUserName")).get("ToUserName");
                    String fromUserName = (String) ((Map) rMsg.get("FromUserName")).get("FromUserName");
                    TextMessage textMessage = new TextMessage();
                    textMessage.setContent("TESTCOMPONENT_MSG_TYPE_TEXT_callback");
                    textMessage.setFromUserName(toUserName);
                    textMessage.setToUserName(fromUserName);
                    textMessage.setCreateTime(new Integer(timestamp).intValue());
                    String resultMsg = WeiXinMessageUtil.getInstance().textMessageToXml(textMessage);
                    logger.info("resultMsg is :" + resultMsg);
                    String encryResultMsg = weixinOauth2ShopService.encryptMsg(resultMsg, timestamp, nonce);
                    logger.info("encryResultMsg is : "+encryResultMsg);
                    return encryResultMsg;

                }
            }

            /**
             * 为第三方公众号与小程序代处理消息
             */

        }catch (Exception e){
            logger.error("receiveWeiXinOpenMsg Error: ",e);
        }
        return "";

    }

    /**
     * 商家通过我们自己生成一个中间链接二维码进到这里，这里要组合出真正的微信授权链接，并跳过去让商家授权
     * @param preAuthCode
     * @param shopid
     * @return
     */
    @RequestMapping(value = {"/wex/preAuth/{appid}/{weixinType}/{shopid}/{preAuthLinkCode}"}, method = {RequestMethod.GET})
    public String shopApplyPreAuth(@PathVariable("appid") String appid, @PathVariable("weixinType") Integer weixinType,
                                   @PathVariable("preAuthLinkCode") String preAuthCode,@PathVariable("shopid") String shopid,Model model) {
        String resultUrl = "weixin/login_error";
        try{
            String redirectUrl = "http://www.ydjs360.com/wex/authShop/"+weixinType+"/"+shopid;
            String url = weixinOauth2ShopService.createWeixinAuthLink(shopid,preAuthCode,appid, weixinType,redirectUrl);
            resultUrl = "redirect:"+url;
        }catch (YdException yex){
            model.addAttribute("errorInfo",yex.getMessage());
            logger.error("shopApplyPreAuth is error: ",yex);
        } catch (Exception e) {
            model.addAttribute("errorInfo","授权失败，请联系服务商！");
            logger.error("shopApplyPreAuth is error: ",e);
        }
        return resultUrl;
    }

    /**
     * 用户授权成功后，微信回调到此接口上来，将小程序或者公众号授权的数据进行绑定(appid与shopid进行绑定:yd_weixin_service_config表写入shopid）
     * @param shopid
     * @param auth_code
     * @param expires_in
     * @param model
     * @return
     */

    @RequestMapping(value = {"/wex/authShop/{weixinType}/{shopid}"}, method = {RequestMethod.GET})
    public String authShop(@PathVariable("weixinType") Integer weixinType,@PathVariable("shopid") String shopid,
                                 @RequestParam(required = false) String auth_code,@RequestParam(required = false) Integer expires_in,
                                 Model model) {
        String resultUrl = "weixin/login_error";
        try{
            boolean isOk = weixinOauth2ShopService.authShop(shopid, WeiXinTypeEnum.nameOf(weixinType),auth_code);
            if(!isOk){
                model.addAttribute("errorInfo","创建商城失败，请联系服务商！");
            }
            model.addAttribute("errorInfo","恭喜您，线上商城创建成功！");
        }catch (YdException yex){
            model.addAttribute("errorInfo",yex.getMessage());
            logger.error("authShop is error: ",yex);
        } catch (Exception e) {
            model.addAttribute("errorInfo","创建商城失败，请联系服务商！");
            logger.error("authShop is error: ",e);
        }
        return resultUrl;
    }




    /**
     * 用于全网测试
     * @param content
     */
    @Async
    protected void sendWeiXinTestMsg(String appid,String toUser,String content){
        try {
            String accessToken = weixinOauth2ShopService.getCPWeiXinAccesssToken(appid,this.querAuthCode);
            CustomTextMsg customTextMsg = new CustomTextMsg();
            Map<String,String> textMsg = new HashMap<>();
            textMsg.put("content",content);
            customTextMsg.setMsgtype("text");
            customTextMsg.setTouser(toUser);
            customTextMsg.setText(textMsg);
            weixinOauth2ShopService.sendCustormMessage(accessToken, JSON.toJSONString(customTextMsg));
        }catch (Exception e){
            logger.error("sendWeiXinTestMsg is error: ",e);
        }

    }


    /**
     * 微信支付通知接口
     * @param request
     * @return
     */
    @RequestMapping(value = {"/wex/paycallback/{orderid}"}, method = RequestMethod.POST)
    @ResponseBody
    public String weixinPayCallBack(@PathVariable("orderid") String orderid, @RequestBody String requestXml,
                                                  HttpServletRequest request) {

        return weiXinPayService.calbackResultPay(orderid,requestXml);
    }

    /**
     * 微信扫码支付回调接口
     * @param request
     * @return
     */
    @RequestMapping(value = {"/wex/qrpay/callback"}, method = RequestMethod.POST)
    @ResponseBody
    public String weixinBizPayCallBack(@RequestBody String requestXml,
                                    HttpServletRequest request) {

        return weiXinPayService.bizPayCallBack(requestXml);
    }

//    /**
//     * 获取小程序码
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = {"/wex/getWxAcodeUnlimit.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Object> getWxAcodeUnlimit(@RequestParam String shopid,@RequestParam String scene, @RequestParam String page,
//                                    HttpServletRequest request) {
//        Result<Object> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            result.setResult(weixinSamll2ShopService.getWxAcodeUnlimit(shopid,scene,page));
//
//        }catch (Exception ex){
//            logger.error("getWxAcodeUnlimit is error :",ex);
//            result.setSuccess(false);
//        }
//
//        return result;
//
//    }


}
