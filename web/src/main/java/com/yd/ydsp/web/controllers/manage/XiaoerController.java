package com.yd.ydsp.web.controllers.manage;

import com.yd.ydsp.biz.config.DiamondYdXiaoerConfigHolder;
import com.yd.ydsp.biz.cp.CpChannelService;
import com.yd.ydsp.biz.cp.CpService;
import com.yd.ydsp.biz.manage.YdXiaoerService;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/10/20.
 */
@Controller
@RequestMapping(value = "/yd/manager")
public class XiaoerController {
    public static final Logger logger = LoggerFactory.getLogger(XiaoerController.class);

    @Resource
    UserinfoService userinfoService;
    @Resource
    CpChannelService cpChannelService;
    @Resource
    CpService cpService;
    @Resource
    YdXiaoerService ydXiaoerService;
    @Resource
    private UserSessionService userSessionService;

    @RequestMapping(value = {"/login/loginByMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> loginByMobile(@RequestParam String mobile, @RequestParam(required = false) String password,
                                         HttpServletRequest request,
                                         HttpServletResponse response)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            WeixinUserInfo weixinUserInfo = ydXiaoerService.loginByMobile(mobile,password);
            if(weixinUserInfo==null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "登录失败！");
            }
            if(StringUtil.isNotEmpty(weixinUserInfo.getOpenid())){
                result.setResult(true);
                /**
                 * 生成session
                 */
                String yid = userSessionService.newSession(weixinUserInfo.getOpenid(), weixinUserInfo.getUnionid(), weixinUserInfo.getMobile(), null, CookieUtils.getIpAddress(request), SourceEnum.WEIXINXIAOER, DiamondYdXiaoerConfigHolder.getInstance().getLoginExpin());
                CookieUtils.addCookie(response, CookieConstantTable.yidm, yid, null, true, DiamondYdXiaoerConfigHolder.getInstance().getLoginExpin(), "/", false);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController loginByMobile is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController loginByMobile is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/logout.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> loginOut(HttpServletRequest request,
                                         HttpServletResponse response)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            result.setResult(userSessionService.deleteSession(userSession.getYid()));
            CookieUtils.delCookie(response,CookieUtils.getCookie(request,CookieConstantTable.yidm));
//            CookieUtils.delCookie(response,CookieUtils.getCookie(request,CookieConstantTable.yidt));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController loginOut is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController loginOut is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/mobile/bindUserMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> bindUserMobile(@RequestParam String mobile,@RequestParam String code, @RequestParam(required = false) String password, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            boolean isSuccess = ydXiaoerService.bindManageMobil(userSession.getOpenid(), mobile,password, code);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController bindUserMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController bindUserMobile is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/mobile/generateBindCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> generateBindCode(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            boolean isSuccess = ydXiaoerService.generateManageUserMobileCode(userSession.getOpenid(), mobile);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController generateBindCode is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController generateBindCode is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/agentinfo/agentApplyAudit.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> agentApplyAudit(@RequestParam String agentid, @RequestParam boolean isReject, @RequestParam(required = false) String rejectDesc,
                                           @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date contractDateBegin,
                                           @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date contractDateEnd,
                                           HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            boolean isSuccess = ydXiaoerService.agentApplyAudit(userSession.getOpenid(), agentid,isReject,rejectDesc,contractDateBegin,contractDateEnd);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController agentApplyAudit is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController agentApplyAudit is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/agentinfo/setAgentApplyStatus.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setAgentApplyStatus(@RequestParam String agentid,@RequestParam(required = false) String desc,@RequestParam Integer status,
                                           HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            boolean isSuccess = ydXiaoerService.setAgentApplyStatus(userSession.getOpenid(), agentid,status,desc);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController setAgentApplyStatus is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController setAgentApplyStatus is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/agentinfo/queryAgentApplyInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryAgentApplyInfo(@RequestParam String agentid, HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            Object obj = ydXiaoerService.queryYdAgentApplyInfo(userSession.getOpenid(), agentid);
            result.setResult(obj);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController queryAgentApplyInfo is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController queryAgentApplyInfo is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryAgentInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryAgentInfo(@RequestParam String agentid, HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            Object obj = ydXiaoerService.queryYdAgentInfo(userSession.getOpenid(), agentid);
            result.setResult(obj);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController queryAgentInfo is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController queryAgentInfo is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/agentinfo/queryAgentContractCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> queryAgentContractCode(@RequestParam String agentid, HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidm);
            String code = ydXiaoerService.queryAgentContractCode(userSession.getOpenid(), agentid);
            result.setResult(code);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("XiaoerController queryAgentContractCode is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("XiaoerController queryAgentContractCode is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryAgentApplyInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryAgentApplyInfoList(@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                               HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.selectAgentApplyInfo(userSession.getOpenid(),pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryAgentApplyInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryAgentApplyInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryAgentInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryAgentInfoList(@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                                  HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.selectAgentInfo(userSession.getOpenid(),pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryAgentInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryAgentInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryCpApplyInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryCpApplyInfoList(@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                            HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.selectCpApplyInfo(userSession.getOpenid(),pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryCpApplyInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryCpApplyInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryShopInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopInfoList(@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                         HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.selectShopInfo(userSession.getOpenid(),pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryShopInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryShopInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryShopInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopInfo(@RequestParam String shopid,
                                            HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getYdCpShopInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryShopInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryShopInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/getShopWorker.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getShopWorker(@RequestParam String shopid, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            Map resultMap = userinfoService.getShopWorker(userSession.getOpenid(), shopid, SourceEnum.WEIXINXIAOER);
            result.setResult(resultMap);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getShopWorker is error: ",yex);

        }catch (Exception ex){
            logger.error("getShopWorker is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryChannelList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryChannelList(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setSuccess(true);
            result.setResult(cpChannelService.queryChannelList(userSession.getOpenid(),shopid,SourceEnum.WEIXINXIAOER));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryChannelList is error: ",yex);

        }catch (Exception ex){
            logger.error("queryChannelList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryDeviceInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryDeviceInfoList(HttpServletRequest request, @RequestParam String shopid, @RequestParam String devicetype,@RequestParam(required = false) String printtype)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            TypeEnum deviceType = TypeEnum.nameOf(devicetype);
            if(deviceType!=TypeEnum.PRINTER&&deviceType!=TypeEnum.MONITOR){
                result.setResult(null);
                result.setSuccess(false);
                result.setMsgInfo("设备类型传值不正确");
                return result;
            }
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            if(deviceType!=TypeEnum.PRINTER) {
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,null,SourceEnum.WEIXINXIAOER));
            }else {
                PrintTypeEnum printTypeEnum = PrintTypeEnum.nameOf(printtype);
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,printTypeEnum,SourceEnum.WEIXINXIAOER));

            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryDeviceInfoList is error: ",yex);

        }catch (Exception ex){
            logger.error("queryDeviceInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/getDefaultXiaoer.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getDefaultXiaoer(HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            Map resultMap = ydXiaoerService.getDefaultXiaoer(userSession.getOpenid());
            result.setResult(resultMap);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getShopWorker is error: ",yex);

        }catch (Exception ex){
            logger.error("getShopWorker is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/queryApplyInfoRecordList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryApplyInfoRecordList(@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                               HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getApplyInfoRecordList(userSession.getOpenid(),pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService queryApplyInfoRecordList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService queryApplyInfoRecordList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getCategory.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getCategory(@RequestParam String appid,
                                                   HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getCategory(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getCategory is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getCategory is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getPage.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getPage(@RequestParam String appid,
                                      HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getPage(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getPage is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getPage is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getTesterList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getTesterList(@RequestParam String appid,
                                  HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getTesterList(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getTesterList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getTesterList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/bindTester.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> bindTester(@RequestParam String appid,@RequestParam String wechatid,
                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.bindTester(userSession.getOpenid(),appid,wechatid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService bindTester is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService bindTester is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/unBindTester.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> unBindTester(@RequestParam String appid,@RequestParam String wechatid,
                                     HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.unBindTester(userSession.getOpenid(),appid,wechatid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService unBindTester is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService unBindTester is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/uploadWeiXinSmallCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> uploadWeiXinSmallCode(@RequestParam String appid,@RequestParam String version,
                                                @RequestParam Integer templateid, @RequestParam String desc,
                                       HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.uploadWeiXinSmallCode(userSession.getOpenid(),appid,templateid,version,desc));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService uploadWeiXinSmallCode is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService uploadWeiXinSmallCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/submitWeiXinSmallCodeAudit.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> submitWeiXinSmallCodeAudit(@RequestParam String appid,@RequestBody String body,
                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.submitWeiXinSmallCodeAudit(userSession.getOpenid(),appid,body));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService submitWeiXinSmallCodeAudit is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService submitWeiXinSmallCodeAudit is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/undoCodeAudit.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> undoCodeAudit(@RequestParam String appid,
                                                     HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.undoCodeAudit(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService undoCodeAudit is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService undoCodeAudit is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getXinSmallCodeAuditResult.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getXinSmallCodeAuditResult(@RequestParam String appid,
                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getXinSmallCodeAuditResult(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getXinSmallCodeAuditResult is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getXinSmallCodeAuditResult is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/releaseWeiXinSmallCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> releaseWeiXinSmallCode(@RequestParam String appid,
                                                     HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.releaseWeiXinSmallCode(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService releaseWeiXinSmallCode is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService releaseWeiXinSmallCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/grayReleaseWeiXinSmallCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> grayReleaseWeiXinSmallCode(@RequestParam String appid,@RequestParam Integer percentage,
                                                 HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.grayReleaseWeiXinSmallCode(userSession.getOpenid(),appid,percentage));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService grayReleaseWeiXinSmallCode is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService grayReleaseWeiXinSmallCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/cancelGrayReleaseWeiXinSmallCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> cancelGrayReleaseWeiXinSmallCode(@RequestParam String appid,
                                                     HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.cancelGrayReleaseWeiXinSmallCode(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService cancelGrayReleaseWeiXinSmallCode is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService cancelGrayReleaseWeiXinSmallCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getGrayReleaseWeiXinSmallCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getGrayReleaseWeiXinSmallCode(@RequestParam String appid,
                                                           HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getGrayReleaseWeiXinSmallCode(userSession.getOpenid(),appid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getGrayReleaseWeiXinSmallCode is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getGrayReleaseWeiXinSmallCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getTemplateDraftList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getTemplateDraftList(
                                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getTemplateDraftList(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getTemplateDraftList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getTemplateDraftList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/getTemplateList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getTemplateList(
                                               HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.getTemplateList(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService getTemplateList is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService getTemplateList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/addToTemplate.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> addToTemplate( @RequestParam Integer draftid,
                                          HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.addToTemplate(userSession.getOpenid(),draftid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService addToTemplate is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService addToTemplate is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/deleteTemplate.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> deleteTemplate( @RequestParam Integer templateid,
                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.deleteTemplate(userSession.getOpenid(),templateid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService deleteTemplate is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService deleteTemplate is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/weixinsmall/modifySmallDomain.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> modifySmallDomain( @RequestParam String appid,@RequestParam String action,@RequestParam(required = false) String domain,
                                          HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.modifySmallDomain(userSession.getOpenid(),appid,action,domain));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService modifySmallDomain is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService modifySmallDomain is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */

    @PageSource(sourceType= SourceEnum.WEIXINXIAOER)
    @RequestMapping(value = {"/releaseFile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> releaseFile(@RequestParam String ids,
                                        HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidm);
            result.setResult(ydXiaoerService.releaseFile(userSession.getOpenid(),ids));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("ydXiaoerService releaseFile is error: ",yex);
        }catch (Exception ex){
            logger.error("ydXiaoerService releaseFile is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }
}
