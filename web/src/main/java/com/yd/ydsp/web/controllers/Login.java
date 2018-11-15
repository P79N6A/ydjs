package com.yd.ydsp.web.controllers;


import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdAgentConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdXiaoerConfigHolder;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.manage.YdXiaoerService;
import com.yd.ydsp.biz.process.JobProcessor;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.WeixinOauth2ShopService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinUserStatusEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.dal.entity.YdConsumerInfo;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zengyixun on 17/4/7.
 */

@Controller
public class Login {

    @Resource
    private WeixinOauth2Service weixinOauth2Service2b;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2c;
    @Resource
    private UserSessionService userSessionService;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private CpAgentService cpAgentService;
    @Resource
    private YdXiaoerService ydXiaoerService;
    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;
    @Resource
    private WeixinOauth2ShopService weixinOauth2ShopService;
    @Resource
    private JobProcessor getWeinXinUserInfoProcess;

    private static final Logger logger = LoggerFactory.getLogger(Login.class);



//    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
////    @ResponseBody
//    public String login(
//                        HttpServletRequest request, HttpSession session){
////        session.setAttribute("yid", "haha");
////        session.setMaxInactiveInterval(30);
//        return null;
//    }


//    @RequestMapping(value = {"/smallApp/login"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Map<String,String>> login(@RequestParam String code,
//            HttpServletRequest request, HttpSession session){
//
//        Result<Map<String,String>> result = new Result<>();
//        Result<WeixinAppCode2SessionResult> weixinSmallAppAuthorize = weixinOauth2Service.weixinSmallAppAuthorize(code);
//        try {
//            if (!weixinSmallAppAuthorize.isSuccess()) {
//                result.setSuccess(false);
//                return result;
//            }
//            Map<String, String> response = new HashMap<>();
//            Map<String, String> appSmallCache = new HashMap<>();
//            String uid = UUIDGenerator.getUUID();
//            response.put(CookieConstantTable.yid, uid);
//            appSmallCache.put("session_key", weixinSmallAppAuthorize.getResult().getSession_key());
//            appSmallCache.put("openid", weixinSmallAppAuthorize.getResult().getOpenid());
//            appSmallCache.put("unionid", weixinSmallAppAuthorize.getResult().getUnionid());
//            redisManager.set(SerializeUtils.serialize(uid), SerializeUtils.serialize(appSmallCache),CookieConstantTable.COOKIE_MAX_AGE);
//            result.setResult(response);
//            result.setSuccess(true);
//        }catch (Exception ex){
//            log.error("smallApp onLogin is error: ",ex);
//            result.setSuccess(false);
//        }
//        return result;
//    }



    @RequestMapping(value = {"/paypoint/weinxin/b2b/logout"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> logoutb2b(HttpServletRequest request, @RequestParam String openid){
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            if(!StringUtil.isBlank(openid)){
                result.setResult(userSessionService.deleteSessionByOpenid(openid,SourceEnum.WEIXIN2B));
            }else {
                result.setSuccess(false);
                result.setMsgInfo("openid不能为空!");
            }
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
            result.setResult(false);
            result.setMsgInfo("失败!");
        }
        return result;
    }

    @RequestMapping(value = {"/paypoint/weinxin/b2c/logout"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> logoutb2c(HttpServletRequest request, @RequestParam String openid){
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            if(!StringUtil.isBlank(openid)){
                result.setResult(userSessionService.deleteSessionByOpenid(openid,SourceEnum.WEIXIN2C));
            }else {
                result.setSuccess(false);
                result.setMsgInfo("openid不能为空!");
            }
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
            result.setResult(false);
            result.setMsgInfo("失败!");
        }
        return result;
    }



    private Result<Map> setPassportError(Result<Map> result,String errorMsg){
        result.setSuccess(false);
        if(StringUtil.isNotEmpty(errorMsg)) {
            result.setMsgInfo(errorMsg);
        }
        result.setResultCode(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode());
        result.setMsgInfo(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        return result;

    }

    @RequestMapping(value = {"/account/passportError"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<Map> passportError(@RequestParam( required=false) String errorMsg,HttpServletRequest request,HttpSession session){
        Result<Map> result = new Result<>();
        result = setPassportError(result,errorMsg);
        return result;
    }


    @RequestMapping(value = {"/account/weinxin/b2b/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> loginWeiXinB2B(@RequestParam(required = false) String state,
                                   @RequestParam String code,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        Result<Map> result = new Result<>();
        boolean isSuccess = true;
        String yid = null;
        String errorMsg = "";
        Map<String,String> map = new HashMap<String,String>();
        SourceEnum sourceEnum = SourceEnum.WEIXIN2B;
        try {
            if(StringUtil.isBlank(code)){
//                return setPassportError(result,"缺少code");
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}","缺少code"));
                this.setPassportError(result,"参错错误！");
                return result;
            }
            //开始验证微信登录
            Result<WeixinTokenInfo> weixinTokenInfoResult = weixinOauth2Service2b.authorize(code);
            if(weixinTokenInfoResult==null){
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}","登录方式错误!"));
                this.setPassportError(result,"登录方式错误！");
                return result;
            }
            Result<WeixinTokenInfo> weixinTokenInfoRefreshResult = weixinOauth2Service2b.refreshToken(weixinTokenInfoResult.getResult().getRefresh_token());
            if(weixinTokenInfoRefreshResult==null){
                weixinTokenInfoRefreshResult = weixinTokenInfoResult;
            }
            logger.info("weixinTokenInfoResult B2B:"+ JSON.toJSONString(weixinTokenInfoRefreshResult));
            WeixinTokenInfo tokenInfo = weixinTokenInfoRefreshResult.getResult();
            if(!weixinTokenInfoRefreshResult.isSuccess()){
//                return setPassportError(result,tokenInfo.getErrmsg());
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}",tokenInfo.getErrmsg()));
                this.setPassportError(result,tokenInfo.getErrmsg());
                return result;
            }

                YdPaypointCpuserInfo cpuserInfo = userinfoService.select2BByOpenId(tokenInfo.getOpenid());
                if(cpuserInfo==null){
                    cpuserInfo = new YdPaypointCpuserInfo();
                    cpuserInfo.setWeixinAccessToken(tokenInfo.getAccess_token());
                    cpuserInfo.setWeixinRefreshToken(tokenInfo.getRefresh_token());
                    cpuserInfo.setOpenid(tokenInfo.getOpenid());
                    cpuserInfo.setStatus(WeiXinUserStatusEnum.AUTHORIZATION.getType());
                    if(userinfoService.newCPUser(cpuserInfo)>0) {
                        /**
                         * 更新用户信息
                         */
                        Map<String,Object> in = new HashMap<>();
                        in.put("weixinType", sourceEnum.getName());
                        in.put("openid",cpuserInfo.getOpenid());
                        in.put("isSns",false);
                        getWeinXinUserInfoProcess.process(in);
                        /**
                         * 生成session
                         */
                        yid = userSessionService.newSession(cpuserInfo.getOpenid(), cpuserInfo.getUnionid(), cpuserInfo.getMobile(), cpuserInfo.getEmail(), CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2B,tokenInfo.getExpires_in());
                    } else {
//                        response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "服务错误，请稍后再试！"));
                        this.setPassportError(result,"数据库错误！");
                        return result;
                    }

                } else {
                    if(StringUtil.isBlank(cpuserInfo.getUnionid())){
                        Map<String,Object> in = new HashMap<>();
                        in.put("weixinType", sourceEnum.getName());
                        in.put("openid",cpuserInfo.getOpenid());
                        in.put("isSns",false);
                        getWeinXinUserInfoProcess.process(in);
                    }
                    userinfoService.updateCPUserToken(cpuserInfo.getOpenid(),tokenInfo.getAccess_token(),tokenInfo.getRefresh_token());
                    /**
                     * 生成session
                     */
                    yid = userSessionService.newSession(cpuserInfo.getOpenid(),cpuserInfo.getUnionid(),cpuserInfo.getMobile(),cpuserInfo.getEmail(), CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2B,tokenInfo.getExpires_in());
                }

        }catch (Exception ex){
            logger.error("",ex);
            isSuccess = false;
            errorMsg = "未知错误，请查看日志";
        }finally {
            result.setMsgInfo(errorMsg);
            result.setSuccess(isSuccess);
            result.setResult(map);
            if ((!isSuccess)||StringUtil.isBlank(yid)) {
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", result.getMsgInfo()));
                this.setPassportError(result,result.getMsgInfo());
                return result;
            } else {
                response.sendRedirect(DiamondYdSystemConfigHolder.getInstance().getYdB2BLoginRedirectUrl()+yid);
                return result;
            }

        }
    }


    @RequestMapping(value = {"/account/weinxin/b2c/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> loginWeiXinB2C(@RequestParam(required = false) String state,
                            @RequestParam String code,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        Result<Map> result = new Result<>();
        boolean isSuccess = true;
        String yid = null;
        String errorMsg = "";
        Map<String,String> map = new HashMap<String,String>();
        SourceEnum sourceEnum = SourceEnum.WEIXIN2C;
        try {
            if(StringUtil.isEmpty(code)){
//                return setPassportError(result,"缺少code");
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}","缺少code"));
                this.setPassportError(result,"参数错误！");
                return result;
            }
            //开始验证微信登录
            Result<WeixinTokenInfo> weixinTokenInfoResult = weixinOauth2Service2c.authorize(code);
            if(weixinTokenInfoResult==null){
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}","登录方式错误!"));
                this.setPassportError(result,"登录方式错误！");
                return result;
            }
            Result<WeixinTokenInfo> weixinTokenInfoRefreshResult = weixinOauth2Service2c.refreshToken(weixinTokenInfoResult.getResult().getRefresh_token());
            if(weixinTokenInfoRefreshResult==null){
                weixinTokenInfoRefreshResult = weixinTokenInfoResult;
            }
            logger.info("weixinTokenInfoResult B2C:"+ JSON.toJSONString(weixinTokenInfoRefreshResult));
            WeixinTokenInfo tokenInfo = weixinTokenInfoRefreshResult.getResult();
            if(!weixinTokenInfoRefreshResult.isSuccess()){
//                return setPassportError(result,tokenInfo.getErrmsg());
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}",tokenInfo.getErrmsg()));
                this.setPassportError(result,tokenInfo.getErrmsg());
                return result;
            }

                YdConsumerInfo consumerInfo = userinfoService.select2CByOpenId(tokenInfo.getOpenid());
                /**
                 * 开始C端用户信息与会话记录
                 */
                if (consumerInfo == null) {
                    consumerInfo = new YdConsumerInfo();
                    consumerInfo.setWeixinAccessToken(tokenInfo.getAccess_token());
                    consumerInfo.setWeixinRefreshToken(tokenInfo.getRefresh_token());
                    consumerInfo.setOpenid(tokenInfo.getOpenid());
                    consumerInfo.setStatus(WeiXinUserStatusEnum.AUTHORIZATION.getType());
                    if(userinfoService.newUser2C(consumerInfo)>0) {
                        /**
                         * 更新用户信息
                         */
                        Map<String,Object> in = new HashMap<>();
                        in.put("weixinType", sourceEnum.getName());
                        in.put("openid",consumerInfo.getOpenid());
                        in.put("isSns",true);
                        getWeinXinUserInfoProcess.process(in);
                        /**
                         * 生成session
                         */
                        yid = userSessionService.newSession(consumerInfo.getOpenid(), consumerInfo.getUnionid(), consumerInfo.getMobile(), consumerInfo.getEmail(), CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2C,tokenInfo.getExpires_in());
                    } else {
//                        response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "服务错误，请稍后再试！"));
                        this.setPassportError(result,"数据库错误！");
                        return result;
                    }

                } else {
                    userinfoService.update2CUserToken(consumerInfo.getOpenid(),tokenInfo.getAccess_token(),tokenInfo.getRefresh_token());
                    /**
                     * 更新用户信息
                     */
                    Map<String,Object> in = new HashMap<>();
                    in.put("weixinType", sourceEnum.getName());
                    in.put("openid",consumerInfo.getOpenid());
                    in.put("isSns",true);
                    getWeinXinUserInfoProcess.process(in);
                    /**
                     * 生成session
                     */
                    yid = userSessionService.newSession(consumerInfo.getOpenid(),consumerInfo.getUnionid(),consumerInfo.getMobile(),consumerInfo.getEmail(), CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2C,tokenInfo.getExpires_in());
                }
        }catch (Exception e){
            logger.error("",e);
            isSuccess = false;
            errorMsg = "未知错误，请查看日志";
        }finally {
            result.setMsgInfo(errorMsg);
            result.setSuccess(isSuccess);
            result.setResult(map);
            if ((!isSuccess)||StringUtil.isBlank(yid)) {
//                response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", result.getMsgInfo()));
                this.setPassportError(result,result.getMsgInfo());
                return result;
            } else {
                response.sendRedirect(DiamondYdSystemConfigHolder.getInstance().getYdB2CLoginRedirectUrl()+yid);
                return result;
            }
        }
    }


    @RequestMapping(value = {"/account/weinxin/agent/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String loginAgentByWeixin(@RequestParam(required = false) String state,
                                      @RequestParam(required = false) String code,
                                     HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        String goToUrl = DiamondYdAgentConfigHolder.getInstance().getAgentMainPageUrl();
        try {
            WeixinUserInfo weixinUserInfo = cpAgentService.loginByWeiXinQrcode(code, state);
            if (weixinUserInfo == null) {
                goToUrl = DiamondYdAgentConfigHolder.getInstance().getLoginErrorPageUrl();
            } else {
                /**
                 * 先看看有没有默认的agentid
                 */
                String defualtAgentId = cpAgentService.getDefualtAgent(weixinUserInfo.getOpenid());
                if(defualtAgentId==null) {
                    Object agentApplyInfo = cpAgentService.queryYdAgentApplyInfo(weixinUserInfo.getOpenid(), null);
                    if (agentApplyInfo == null) {
                        goToUrl = DiamondYdAgentConfigHolder.getInstance().getAgentApplyPageUrl();
                    }
                    if (StringUtil.isEmpty(weixinUserInfo.getMobile())) {
                        goToUrl = DiamondYdAgentConfigHolder.getInstance().getBindMobilePageUrl();
                    }
                }
                /**
                 * 生成session
                 */
                String yid = userSessionService.newSession(weixinUserInfo.getOpenid(), weixinUserInfo.getUnionid(), weixinUserInfo.getMobile(), null, CookieUtils.getIpAddress(request), SourceEnum.WEIXINAGENT, DiamondYdAgentConfigHolder.getInstance().getLoginExpin());
                CookieUtils.addCookie(response, CookieConstantTable.yida, yid, null, true, DiamondYdAgentConfigHolder.getInstance().getLoginExpin(), "/", false);
            }
        }catch (Exception e){
            logger.error("登录服务商后台错误：",e);
            goToUrl = DiamondYdAgentConfigHolder.getInstance().getLoginErrorPageUrl();
        }
        return "redirect:"+goToUrl;

    }

    @RequestMapping(value = {"/account/weinxin/xiaoer/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String loginXiaoerByWeixin(@RequestParam(required = false) String state,
                                     @RequestParam(required = false) String code,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        String goToUrl = DiamondYdXiaoerConfigHolder.getInstance().getAgentMainPageUrl();
        try {
            WeixinUserInfo weixinUserInfo = ydXiaoerService.loginByWeiXinQrcode(code, state);
            if (weixinUserInfo == null) {
                goToUrl = DiamondYdXiaoerConfigHolder.getInstance().getLoginErrorPageUrl();
            } else {

                if (StringUtil.isEmpty(weixinUserInfo.getMobile())) {
                    goToUrl = DiamondYdXiaoerConfigHolder.getInstance().getBindMobilePageUrl();
                }
                /**
                 * 生成session
                 */
//                Integer yidt = RandomUtil.getNotSimple(4);
                String yid = userSessionService.newSession(weixinUserInfo.getOpenid(), weixinUserInfo.getUnionid(), weixinUserInfo.getMobile(), null, CookieUtils.getIpAddress(request), SourceEnum.WEIXINXIAOER, DiamondYdAgentConfigHolder.getInstance().getLoginExpin());
                CookieUtils.addCookie(response, CookieConstantTable.yidm, yid, null, true, DiamondYdAgentConfigHolder.getInstance().getLoginExpin(), "/", false);
//                CookieUtils.addCookie(response, CookieConstantTable.yidt, yidt.toString(), null, true, DiamondYdXiaoerConfigHolder.getInstance().getLoginExpin(), "/", false);

            }
        }catch (YdException yex){
            logger.error("loginXiaoerByWeixin",yex);
            goToUrl = DiamondYdXiaoerConfigHolder.getInstance().getLoginErrorPageUrl();
        }catch (Exception e){
            logger.error("loginXiaoerByWeixin：",e);
            goToUrl = DiamondYdXiaoerConfigHolder.getInstance().getLoginErrorPageUrl();
        }
        return "redirect:"+goToUrl;


    }


    /**
     * 为所有第三方的公众号的C端用户进行授权登录
     * @param state
     * @param code
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/account/weinxin/open/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String loginOpenByWeixin(@RequestParam(required = false) String appid,
            @RequestParam(required = false) String state,
                                      @RequestParam(required = false) String code,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        String goToUrl = DiamondWeiXinInfoConfigHolder.getInstance().getErrorPageUrl();
        try {
            if(StringUtil.isNotEmpty(code)&&StringUtil.isNotEmpty(appid)){
                Map<String,Object> loginResult = weixinOauth2ShopService.authorize(appid,code);
                if(loginResult.containsKey("success")) {
                    Boolean isOK = (Boolean) loginResult.get("success");
                    if (isOK) {
                        if (loginResult.containsKey("userSession")) {
                            UserSession userSession = (UserSession) loginResult.get("userSession");
                            String yid = userSessionService.newSession(userSession.getAppid(), userSession.getWeixinConfigId(), userSession.getOpenid(), userSession.getUnionid(), userSession.getMobile(), userSession.getEmail(),
                                    CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2C, 3600, userSession.getYid());
                            CookieUtils.addCookie(response, CookieConstantTable.yidc, yid, null, true, DiamondYdAgentConfigHolder.getInstance().getLoginExpin(), "/", false);
                            /**
                             * 授权成功则去主页面
                             */
                            goToUrl = DiamondWeiXinInfoConfigHolder.getInstance().getMainPageUrl();
                        }
                    }
                }
            }else {
                /**
                 * 说明用户拒绝授权
                 */
                goToUrl = DiamondWeiXinInfoConfigHolder.getInstance().getErrorPageUrl();
            }

        }catch (YdException yex){
            logger.error("loginOpenByWeixin",yex);
            goToUrl = DiamondWeiXinInfoConfigHolder.getInstance().getErrorPageUrl();
        }catch (Exception e){
            logger.error("loginOpenByWeixin：",e);
            goToUrl = DiamondWeiXinInfoConfigHolder.getInstance().getErrorPageUrl();
        }
        return "redirect:"+goToUrl;


    }



    /**
     * 为所有第三方的小程序的C端用户进行授权登录
     * @param state
     * @param code
     * @param request
     * @return
     */
    @RequestMapping(value = {"/account/weinxin/samll/login"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> loginByWeixinSmall(@RequestParam(required = false) String appid,
                                    @RequestParam(required = false) String state,
                                    @RequestParam(required = false) String code,
                                    HttpServletRequest request) {

        Result<Map> result = new Result<>();
        result.setSuccess(true);

        try {
            Map<String,Object> loginResult = weixinSamll2ShopService.loginUserByWeixinSmall(appid, code);
            if(loginResult.containsKey("success")){
                Boolean isOK = (Boolean)loginResult.get("success");
                if(isOK){
                    if(loginResult.containsKey("userSession")){
                        UserSession userSession = (UserSession)loginResult.get("userSession");
                        String yid = userSessionService.newSession(userSession.getAppid(),userSession.getWeixinConfigId(),userSession.getOpenid(),userSession.getUnionid(),userSession.getMobile(),userSession.getEmail(),
                                CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2C,3600,userSession.getYid());
                        loginResult.put(CookieConstantTable.yidc,yid);
                        loginResult.remove("userSession");
                    }
                }
            }
            result.setResult(loginResult);
            logger.info("loginByWeixinSmall result :"+JSON.toJSONString(loginResult));
        }catch (Exception e){
            logger.error("loginByWeixinSmall：",e);
            result.setSuccess(false);
        }


        return result;
    }


    /**
     * 为所有第三方的小程序的C端用户进行session刷新
     * @param request
     * @return
     */
    @RequestMapping(value = {"/account/weinxin/samll/getSession"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getSessionByWeixinSmall(@RequestParam(required = false) String appid,
                                          @RequestParam(required = false) String openid,
                                                  @RequestParam(required = false) String yidc,
                                          HttpServletRequest request) {

        Result<String> result = new Result<>();
        result.setSuccess(true);

        try {
            UserSession userSession = weixinSamll2ShopService.getNewSession(appid, openid,yidc);
            String yid = userSessionService.newSession(userSession.getAppid(),userSession.getWeixinConfigId(),userSession.getOpenid(),userSession.getUnionid(),userSession.getMobile(),userSession.getEmail(),
                    CookieUtils.getIpAddress(request), SourceEnum.WEIXIN2C,3600,userSession.getYid());
            result.setResult(yid);
        }catch (Exception e){
            logger.error("getSessionByWeixinSmall：",e);
            result.setSuccess(false);
        }


        return result;
    }


}
