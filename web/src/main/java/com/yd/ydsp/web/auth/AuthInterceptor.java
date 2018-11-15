package com.yd.ydsp.web.auth;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.dal.entity.YdConsumerInfo;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import com.yd.ydsp.web.auth.passport.AuthPassport;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Created by zengyixun on 17/5/5.
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisManager redisManager;

    @Resource
    private UserSessionService userSessionService;

    @Resource
    private WeixinOauth2Service weixinOauth2Service2b;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2c;
    @Resource
    private UserinfoService userinfoService;

    public static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获得请求路径的uri
        String uri = request.getRequestURI();

        System.out.println("*********************preHandle********************");
        System.out.println(handler.getClass());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        System.out.println(handlerMethod.getMethod());

        /**
         * 允许跨域请求
         */
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Accept,Exception");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", "*");//前端项目的域

        /**
         * 测试联调要使用的代码
         */
        if(StringUtil.isNotBlank(request.getParameter("openidTest"))){
            if(StringUtil.equals(request.getParameter("openidTest").trim().toLowerCase(),"undefined")){
                request.setAttribute("openidTest",null);
            }
        }
        if(StringUtil.isNotEmpty(request.getParameter("testToken"))&&StringUtil.isNotBlank(request.getParameter("openidTest"))) {
            if(DiamondYdSystemConfigHolder.getInstance().getTestSwitch()) {
                if (request.getParameter("testToken").equals(DiamondYdSystemConfigHolder.getInstance().testToken)) {
                    String yidTest;
                    String sourceTypeTest = request.getParameter("sourceTypeTest");
                    if (StringUtil.isEmpty(sourceTypeTest)) {
                        if(StringUtil.contains(request.getRequestURI(),"paypoint/customer")){
                            sourceTypeTest = "WEIXIN2C";
                        }else{
                            sourceTypeTest = "WEIXIN2B";
                        }
                    }
                    Cookie yidCookieTest = null;
                    SourceEnum sourceEnum = SourceEnum.nameOf(sourceTypeTest);
                    if(sourceEnum==SourceEnum.WEIXIN2B) {
                        yidCookieTest = CookieUtils.getCookie(request, CookieConstantTable.yidb);
                    }else if(sourceEnum==SourceEnum.WEIXIN2C) {
                        yidCookieTest = CookieUtils.getCookie(request, CookieConstantTable.yidc);
                    }else if(sourceEnum==SourceEnum.WEIXINAGENT) {
                        yidCookieTest = CookieUtils.getCookie(request, CookieConstantTable.yida);
                    }else if(sourceEnum==SourceEnum.WEIXINXIAOER) {
                        yidCookieTest = CookieUtils.getCookie(request, CookieConstantTable.yidm);
                    }

                    if(sourceEnum==null){
                        return false;
                    }

                    String openidTest = request.getParameter("openidTest");
                    logger.info("openidTest is : "+openidTest);
                    String unionidTest = request.getParameter("unionidTest");
                    String mobileTest = request.getParameter("mobileTest");
                    String emailTest = request.getParameter("emailTest");
                    String weixinConfigIdTest = request.getParameter("weixinConfigIdTest");
                    String appidTest = request.getParameter("appidTest");


                    if (yidCookieTest != null) {
                        yidTest = yidCookieTest.getValue();
                        if(StringUtil.isNotBlank(yidTest)){
                            yidTest = URLDecoder.decode(yidTest,"utf-8");
                        }
                        UserSession userSessionTest = userSessionService.getSession(yidTest);
                        if (userSessionTest == null) {
                            //说明有session，这时把seesion中的user信息设置request中以方便Controller使用
                            if(StringUtil.isEmpty(weixinConfigIdTest)&&StringUtil.isEmpty(appidTest)) {
                                yidTest = userSessionService.newSessionWithTest(openidTest, unionidTest, mobileTest, emailTest, CookieUtils.getIpAddress(request), SourceEnum.nameOf(sourceTypeTest), 600);
                            }else {
                                yidTest = userSessionService.newSessionWithTest(appidTest,weixinConfigIdTest,openidTest, unionidTest, mobileTest, emailTest, CookieUtils.getIpAddress(request), SourceEnum.nameOf(sourceTypeTest), 600);
                            }
//                            CookieUtils.addCookie(response, CookieConstantTable.yid, yidTest, null, true, CookieConstantTable.COOKIE_MAX_AGE, null, false);
                            userSessionTest = userSessionService.getSession(yidTest);
                        }

                        if(sourceEnum==SourceEnum.WEIXIN2B) {
                            request.setAttribute(CookieConstantTable.yidb, userSessionTest);
                            CookieUtils.addCookie(response, CookieConstantTable.yidb, userSessionTest.getYid(), null, true, CookieConstantTable.COOKIE_MAX_AGE, null, false);
                            return true;
                        }else if(sourceEnum==SourceEnum.WEIXIN2C) {
                            request.setAttribute(CookieConstantTable.yidc, userSessionTest);
                            CookieUtils.addCookie(response, CookieConstantTable.yidc, userSessionTest.getYid(), null, true, CookieConstantTable.COOKIE_MAX_AGE, null, false);
                            return true;
                        }else if(sourceEnum==SourceEnum.WEIXINAGENT) {
                            request.setAttribute(CookieConstantTable.yida, userSessionTest);
                            CookieUtils.addCookie(response, CookieConstantTable.yida, userSessionTest.getYid(), null, true, CookieConstantTable.COOKIE_MAX_AGE, null, false);
                            return true;
                        }else if(sourceEnum==SourceEnum.WEIXINXIAOER) {
                            request.setAttribute(CookieConstantTable.yidm, userSessionTest);
                            CookieUtils.addCookie(response, CookieConstantTable.yidm, userSessionTest.getYid(), null, true, CookieConstantTable.COOKIE_MAX_AGE, null, false);
                            return true;
                        }

                        return false;

                    }
                    if(StringUtil.isEmpty(weixinConfigIdTest)&&StringUtil.isEmpty(appidTest)) {
                        yidTest = userSessionService.newSessionWithTest(openidTest, unionidTest, mobileTest, emailTest, CookieUtils.getIpAddress(request), SourceEnum.nameOf(sourceTypeTest), 600);
                    }else {
                        yidTest = userSessionService.newSessionWithTest(appidTest,weixinConfigIdTest,openidTest, unionidTest, mobileTest, emailTest, CookieUtils.getIpAddress(request), SourceEnum.nameOf(sourceTypeTest), 600);
                    }
                    if(sourceEnum==SourceEnum.WEIXIN2B) {
                        CookieUtils.addCookie(response, CookieConstantTable.yidb, yidTest, null, true, CookieConstantTable.COOKIE_MAX_AGE, "/", false);
                    }else if(sourceEnum==SourceEnum.WEIXIN2C) {
                        CookieUtils.addCookie(response, CookieConstantTable.yidc, yidTest, null, true, CookieConstantTable.COOKIE_MAX_AGE, "/", false);
                    }else if(sourceEnum==SourceEnum.WEIXINAGENT) {
                        CookieUtils.addCookie(response, CookieConstantTable.yida, yidTest, null, true, CookieConstantTable.COOKIE_MAX_AGE, "/", false);
                    }else if(sourceEnum==SourceEnum.WEIXINXIAOER) {
                        CookieUtils.addCookie(response, CookieConstantTable.yidm, yidTest, null, true, CookieConstantTable.COOKIE_MAX_AGE, "/", false);
                    }

                    UserSession userSessionTest = userSessionService.getSession(yidTest);
                    if(sourceEnum==SourceEnum.WEIXIN2B) {
                        request.setAttribute(CookieConstantTable.yidb, userSessionTest);
                        return true;
                    }else if(sourceEnum==SourceEnum.WEIXIN2C) {
                        request.setAttribute(CookieConstantTable.yidc, userSessionTest);
                        return true;
                    }else if(sourceEnum==SourceEnum.WEIXINAGENT) {
                        request.setAttribute(CookieConstantTable.yida, userSessionTest);
                        return true;
                    }else if(sourceEnum==SourceEnum.WEIXINXIAOER) {
                        request.setAttribute(CookieConstantTable.yidm, userSessionTest);
                        return true;
                    }
                    return false;

                }
            }
        }
        /**
         * 测试联调代码结束
         */

        PageSource pageSource = ((HandlerMethod) handler).getMethodAnnotation(PageSource.class);
        AuthPassport authPassport = ((HandlerMethod) handler).getMethodAnnotation(AuthPassport.class);

        /**
         * 先判断超级用户验证码模式
         */
        if((pageSource != null)&&(authPassport != null)){
            for(SourceEnum type:pageSource.sourceType()){
                if(type==SourceEnum.ROOTMANAGER){
                    return this.prHandleRootManager(request,response,handler,uri,pageSource,authPassport);
                }
            }
        }
        //超级管理员方式验证结束

        Cookie yidCookie = null;

        UserSession userSession = null;
        /**
         * 看看接口请求来源，作出相应的cookies获取方式
         */
        SourceEnum targetSource = null;
        if(pageSource != null) {
            for (SourceEnum type : pageSource.sourceType()) {
                if (type == SourceEnum.WEIXIN2C) {
                    targetSource = SourceEnum.WEIXIN2C;
                    yidCookie = CookieUtils.getCookie(request, CookieConstantTable.yidc);
                    if (yidCookie != null) {
                        logger.info("yidCookies b2c is: " + JSON.toJSONString(yidCookie));
                    }
                }
                if (type == SourceEnum.WEIXIN2B) {
                    targetSource = SourceEnum.WEIXIN2B;
                    yidCookie = CookieUtils.getCookie(request, CookieConstantTable.yidb);
                    if (yidCookie != null) {
                        logger.info("yidCookies b2b is: " + JSON.toJSONString(yidCookie));
                    }
                }
                if (type == SourceEnum.WEIXINXIAOER) {
                    targetSource = SourceEnum.WEIXINXIAOER;
                    yidCookie = CookieUtils.getCookie(request, CookieConstantTable.yidm);
                    if (yidCookie != null) {
                        logger.info("yidCookies xiaoerManager is: " + JSON.toJSONString(yidCookie));
                    }
                }
                if (type == SourceEnum.WEIXINAGENT) {
                    targetSource = SourceEnum.WEIXINAGENT;
                    yidCookie = CookieUtils.getCookie(request, CookieConstantTable.yida);
                    if (yidCookie != null) {
                        logger.info("yidCookies agentManager is: " + JSON.toJSONString(yidCookie));
                    }
                }
            }
        }else {
            //没有来源的,没有声明需要权限,或者声明不验证权限
            if (authPassport == null || authPassport.validate() == false) {
                return true;
            }
            /**
             * 以下是判断没有来源，但要求有权限的场景
             */
//            response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "未知的来源!"));
            this.returnErrorMessage(response, ErrorCodeConstants.FAIL.getErrorCode(),"未知的来源!");
            return false;
        }

        /**
         * 先看是不是从微信直接访问的，如果是按微信方式直接处理
         */
        String yid = null;
        //正常

        if (yidCookie != null) {
            logger.info("yidCookie is : "+JSON.toJSONString(yidCookie));
            yid = yidCookie.getValue();
            userSession = userSessionService.getSession(yid);
            logger.info("yid is :"+yid);
            if (userSession != null) {
                //说明有session，这时把seesion中的user信息设置request中以方便Controller使用
                if(targetSource==SourceEnum.WEIXIN2B) {
                    request.setAttribute(CookieConstantTable.yidb, userSession);
                }

                if(targetSource==SourceEnum.WEIXIN2C) {
                    request.setAttribute(CookieConstantTable.yidc, userSession);
                }
                if(targetSource==SourceEnum.WEIXINXIAOER) {
                    request.setAttribute(CookieConstantTable.yidm, userSession);
                }
                if(targetSource==SourceEnum.WEIXINAGENT) {
                    request.setAttribute(CookieConstantTable.yida, userSession);
                }
//                    String newYid = userSessionService.updateSession(yid);
//                    if(StringUtil.isNotEmpty(newYid)) {
//                        CookieUtils.editCookie(request,response,CookieConstantTable.yid,newYid,null);
//                        return true;
//                    }
                return true;
            } else {
                /**
                 * 说明session过期了，需要登录
                 */
                if(targetSource==SourceEnum.WEIXIN2C||targetSource==SourceEnum.WEIXIN2B||
                        targetSource==SourceEnum.WEIXINXIAOER||targetSource==SourceEnum.WEIXINAGENT) {
//                    CookieUtils.delCookie(response, yidCookie);
//                    response.sendRedirect("/account/needLoginWeixin");
                    logger.error("cookies过期，所以需要登录！");
                    this.returnErrorMessage(response, ErrorCodeConstants.WEIXIN_NEED_LOGIN.getErrorCode(),ErrorCodeConstants.WEIXIN_NEED_LOGIN.getErrorMessage());
                    return false;
                }else {
                    return true;
                }
            }
        }else{
            if(targetSource==SourceEnum.WEIXIN2C||targetSource==SourceEnum.WEIXIN2B||
                    targetSource==SourceEnum.WEIXINXIAOER||targetSource==SourceEnum.WEIXINAGENT) {
//                response.sendRedirect("/account/needLoginWeixin");
                logger.error("cookies为空，所以需要登录！");
                this.returnErrorMessage(response, ErrorCodeConstants.NEED_LOGIN.getErrorCode(),ErrorCodeConstants.NEED_LOGIN.getErrorMessage());
                return false;
            }else{
                return true;
            }
        }

        //目前只有微信小程序方式，所以只在这里以小程序方式来进行验证
//        try {
//
//            if (pageSource != null && pageSource.sourceType() == SourceEnum.WEIXINAPPSMALL) {
//                //小程序每次的请求都会带上yid
//                String yid = request.getParameter("yid");
//                if (StringUtil.isEmpty(yid)) {
//                    response.getWriter().write("用户登录信息不存在，请重新登录!");
//                    return false;
//                }
//                Map<String, String> appSmallCache = (HashMap) SerializeUtils.deserialize(redisManager.get(SerializeUtils.serialize(yid)));
////                request.getParameterMap().put("openid",appSmallCache.get("openid"));
//                request.setAttribute("openid",appSmallCache.get("openid"));
//                request.setAttribute("unionid",appSmallCache.get("unionid"));
//                return true;
//            }
//        }catch (Exception ex){
//            logger.error("appSmall AuthPassport is Error: ",ex);
//            return false;
//        }

        //以上是微信小程序的登录状验证结束

//        if(pageSource != null){
//            for(SourceEnum type:pageSource.sourceType()){
//                if(type==SourceEnum.WEIXIN2C){
//                    return this.prHandle2C(request,response,handler,uri,pageSource,authPassport);
//                }
//                if(type==SourceEnum.WEIXIN2B){
//                    return this.prHandle2B(request,response,handler,uri,pageSource,authPassport);
//                }
//            }
//            //没有来源的,没有声明需要权限,或者声明不验证权限
//            if (authPassport == null || authPassport.validate() == false) {
//                return true;
//            }
//            response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "未知的来源!"));
//            return false;
//        }
//        else {
//            YdPaypointCpuserInfo userInfo = null;
//
//            // 其他情况判断session中是否有key，有的话继续用户的操作
//            if (userInfo != null) {
//                if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
//
//                    //没有声明需要权限,或者声明不验证权限
//                    if (authPassport == null || authPassport.validate() == false)
//                        return true;
//                    else {
//                        //在这里实现自己的权限验证逻辑
//                        if (false)//如果验证成功返回true（这里直接写false来模拟验证失败的处理）
//                            return true;
//                        else//如果验证失败
//                        {
//                            //返回无权登录接口
//                            response.sendRedirect("/account/passportError");
//                            return false;
//                        }
//                    }
//                } else {
//                    return true;
//                }
//            }
//
//
//            //没有声明需要权限,或者声明不验证权限
//            if (authPassport == null || authPassport.validate() == false) {
//                return true;
//            }
//            // 最后的情况就是进入登录页面
//            response.sendRedirect(MessageFormat.format("{0}/account/weinxin/login?redirectUri={1}", request.getContextPath(), URLEncoder.encode(uri, "UTF-8")));
//            return false;
//
//        }

    }

    private boolean prHandleRootManager(HttpServletRequest request, HttpServletResponse response, Object handler,String uri,PageSource pageSource,AuthPassport authPassport) throws Exception {
        //authPassport必须为root
        String mobile = request.getParameter("mobile");
        String code = request.getParameter("code");
        if(StringUtil.isBlank(mobile)||StringUtil.isBlank(code)){
            response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "缺少参数！"));
            return false;
        }

        if(!userinfoService.checkCodeByRootUser(mobile,code)){
            response.sendRedirect(MessageFormat.format("/account/passportError?errorMsg={0}", "超级用户验证码错误！"));
            return false;
        }

        return true;
    }

    private void returnErrorMessage(HttpServletResponse response,String errorCode, String errorMessage) throws IOException {
        Result<Object> result = new Result();
        result.setSuccess(false);
        result.setResultCode(errorCode);
        result.setMsgInfo(errorMessage);
        response.setContentType("application/json");
//Get the printwriter object from response to write the required json object to the output stream
        PrintWriter out = response.getWriter();
        try {
//Assuming your json object is **jsonObject**, perform the following, it will return your json object
            String jsonOfRST = JSON.toJSONString(result);
            out.print(jsonOfRST);
        }finally {
            if(out!=null) {
                out.flush();
            }
        }

    }

}
