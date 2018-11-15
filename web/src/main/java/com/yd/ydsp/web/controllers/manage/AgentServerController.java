package com.yd.ydsp.web.controllers.manage;

import com.yd.ydsp.biz.config.DiamondYdAgentConfigHolder;
import com.yd.ydsp.biz.cp.CpChannelService;
import com.yd.ydsp.biz.cp.CpService;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.manage.model.CpAgentInfoVO;
import com.yd.ydsp.biz.openshop.ShopApplyService;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.manage.YDManageUserInfoVO;
import com.yd.ydsp.client.domian.openshop.YdShopApplyInfoVO;
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
@RequestMapping(value = "/yd/agent")
public class AgentServerController {
    public static final Logger logger = LoggerFactory.getLogger(AgentServerController.class);

    @Resource
    UserinfoService userinfoService;
    @Resource
    CpAgentService cpAgentService;
    @Resource
    CpChannelService cpChannelService;
    @Resource
    CpService cpService;
    @Resource
    private UserSessionService userSessionService;
    @Resource
    private ShopApplyService shopApplyService;

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/getOssAuthentication.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> getOssAuthentication(@RequestParam(required = false) String dir, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.getOssAuthentication(userSession.getOpenid(),dir));
        }catch (Exception ex){
            logger.error("",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/login/loginByMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> loginByMobile(@RequestParam String mobile, @RequestParam(required = false) String password,
                                         HttpServletRequest request,
                                         HttpServletResponse response)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            WeixinUserInfo weixinUserInfo = cpAgentService.loginByMobile(mobile,password);
            if(weixinUserInfo==null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "登录失败！");
            }
            if(StringUtil.isNotEmpty(weixinUserInfo.getOpenid())){
                result.setResult(true);
                /**
                 * 生成session
                 */
                String yid = userSessionService.newSession(weixinUserInfo.getOpenid(), weixinUserInfo.getUnionid(), weixinUserInfo.getMobile(), null, CookieUtils.getIpAddress(request), SourceEnum.WEIXINAGENT, DiamondYdAgentConfigHolder.getInstance().getLoginExpin());
                CookieUtils.addCookie(response, CookieConstantTable.yida, yid, null, true, DiamondYdAgentConfigHolder.getInstance().getLoginExpin(), "/", false);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService loginByMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService loginByMobile is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/logout.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> loginOut(HttpServletRequest request,
                                    HttpServletResponse response)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            result.setResult(userSessionService.deleteSession(userSession.getYid()));
            CookieUtils.delCookie(response,CookieUtils.getCookie(request,CookieConstantTable.yida));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService loginOut is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService loginOut is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/mobile/bindUserMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> bindUserMobile(@RequestParam String mobile,@RequestParam String code, @RequestParam(required = false) String password, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            boolean isSuccess = cpAgentService.bindManageMobil(userSession.getOpenid(), mobile,password, code);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService bindUserMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService bindUserMobile is error: ",ex);
        }
        return result;
    }

    @RequestMapping(value = {"/public/mobile/bindUserMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> bindPublicUserMobile(@RequestParam String mobile,@RequestParam String code, @RequestParam(required = false) String password, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            boolean isSuccess = cpAgentService.bindPublicManageMobile(mobile,password, code);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService bindUserMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService bindUserMobile is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/mobile/generateBindCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> generateBindCode(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            boolean isSuccess = cpAgentService.generateManageUserMobileCode(userSession.getOpenid(), mobile);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService generateBindCode is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService generateBindCode is error: ",ex);
        }
        return result;
    }

    @RequestMapping(value = {"/public/mobile/generateBindCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> generatePublicBindCode(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            boolean isSuccess = cpAgentService.generatePublicMobileCode(mobile);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService generatePublicBindCode is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService generatePublicBindCode is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/apply/queryStatus.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Integer> queryApplyStatus(@RequestParam(required = false) String agentid,HttpServletRequest request)
    {
        Result<Integer> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            Integer status = cpAgentService.queryYdAgentApplyStatus(userSession.getOpenid(),agentid);
            result.setResult(status);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryApplyStatus is error: ",yex);
        }catch (Exception ex){
            result.setResultCode("-1");
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService queryApplyStatus is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/apply/queryInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryApplyInfo(@RequestParam(required = false) String agentid,HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            Object object = cpAgentService.queryYdAgentApplyInfo(userSession.getOpenid(),agentid);
            result.setResult(object);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryApplyInfo is error: ",yex);
        }catch (Exception ex){
            result.setResultCode("-1");
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService queryApplyInfo is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryAgentInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryAgentInfo(@RequestParam(required = false) String agentid,HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            Object object = cpAgentService.queryYdAgentInfo(userSession.getOpenid(),agentid);
            result.setResult(object);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryAgentInfo is error: ",yex);
        }catch (Exception ex){
            result.setResultCode("-1");
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService queryAgentInfo is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/apply/submitApply.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> submitApply(@RequestBody CpAgentInfoVO cpAgentInfoVO, HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            String agentid = cpAgentService.setYdAgentApplyInfo(userSession.getOpenid(),cpAgentInfoVO);
            result.setResult(agentid);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService submitApply is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setResultCode("-1");
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService submitApply is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/apply/submitContractCode.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> submitContractCode(@RequestParam String contractCode, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yida);
            boolean isSuccess = cpAgentService.submitContractCode(userSession.getOpenid(),contractCode);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService submitContractCode is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("cpAgentService submitContractCode is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryManagerInfoByAgentId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryManagerInfoByAgentId(@RequestParam String agentid,HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.queryManagerInfoByAgentId(userSession.getOpenid(),agentid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryManagerInfoByAgentId is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService queryManagerInfoByAgentId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryAgentBaseInfoByManager.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryAgentBaseInfoByManager(HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.queryAgentBaseInfoByManager(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryAgentBaseInfoByManager is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService queryAgentBaseInfoByManager is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/role/addToAgentManager.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Map> addToAgentManager(@RequestParam String mobile,@RequestParam String agentid,
                                         HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.addToAgentManager(userSession.getOpenid(),mobile,agentid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService addToAgentManager is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService addToAgentManager is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/role/delAgentManager.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> delAgentManager(@RequestParam String mobile,@RequestParam String agentid,
                                         HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.delAgentManager(userSession.getOpenid(),mobile,agentid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService delAgentManager is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService delAgentManager is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/setDefualtAgent.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> setDefualtAgent(@RequestParam String agentid,
                                           HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.setDefualtAgent(userSession.getOpenid(),agentid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService setDefualtAgent is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService setDefualtAgent is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/getDefualtAgent.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getDefualtAgent(HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.getDefualtAgent(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService getDefualtAgent is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService getDefualtAgent is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/setYdCpApplyInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> setYdCpApplyInfo(@RequestBody YdShopApplyInfoVO ydShopApplyInfoVO,
                                           HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.setYdCpApplyInfo(userSession.getOpenid(),ydShopApplyInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService setYdCpApplyInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService setYdCpApplyInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }
    // 修改续约时间
    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/updateContractTime.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateContractTime(@RequestParam String shopid,@RequestParam String agentid,
                                             @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date contractDateBegin,
                                             @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date contractDateEnd,
                                             HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.updateContractDate(userSession.getOpenid(),shopid,agentid,contractDateBegin,contractDateEnd));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService updateContractTime is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService updateContractTime is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/getYdCpApplyInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getYdCpApplyInfo(@RequestParam String applyid,
                                            HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.getYdCpApplyInfo(userSession.getOpenid(),applyid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService getYdCpApplyInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService getYdCpApplyInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/delYdCpApplyInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> delYdCpApplyInfo(@RequestParam String applyid,
                                            HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.delYdCpApplyInfo(userSession.getOpenid(),applyid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService delYdCpApplyInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService delYdCpApplyInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

//    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
//    @RequestMapping(value = {"/backFirstStepWithCpApply.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<Boolean> backFirstStepWithCpApply(@RequestParam String applyid,
//                                            HttpServletRequest request)
//    {
//        Result<Boolean> result = new Result<>();
//        result.setSuccess(true);
//        try{
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
//            result.setResult(cpAgentService.backFirstStepWithCpApply(userSession.getOpenid(),applyid));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("cpAgentService backFirstStepWithCpApply is error: ",yex);
//        }catch (Exception ex){
//            logger.error("cpAgentService backFirstStepWithCpApply is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }


    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryCpApplyInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryCpApplyInfoList(@RequestParam String agentid,@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                                    HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.selectCpApplyInfo(userSession.getOpenid(),agentid,pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryCpApplyInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService queryCpApplyInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryShopInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopInfoList(@RequestParam String agentid,@RequestParam Integer pageNum,@RequestParam Integer pageSize,
                                            HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.selectShopInfo(userSession.getOpenid(),agentid,pageNum,pageSize));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cpAgentService queryShopInfoList is error: ",yex);
        }catch (Exception ex){
            logger.error("cpAgentService queryShopInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 商家申请公众号或者小程序管理授权给我们时，创建的一个临时二维码
     * @param shopid
     * @param request
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/cpApply/getAuthLink.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getAuthLink(@RequestParam String shopid,@RequestParam Integer weixinType,@RequestParam String appid,
                                          HttpServletRequest request){

        Result<String> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            String url = shopApplyService.getPreAuthLink(userSession.getOpenid(),shopid,weixinType,appid);
            result.setResult(url);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getAuthLink is error: ",ex);
        }

        return result;
    }

    /**
     * 获取服务商基本信息
     * @param
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/getBaseYdAgentInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<YDManageUserInfoVO> getBaseYdAgentInfo(HttpServletRequest request){

        Result<YDManageUserInfoVO> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.queryBaseYdAgentByOpenId(userSession.getOpenid()));
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getBaseYdAgentInfo is error: ",ex);
        }

        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryShopInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopInfo(@RequestParam String shopid,
                                        HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setResult(cpAgentService.getYdCpShopInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("ueryShopInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }



    @PageSource(sourceType=SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/getShopWorker.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getShopWorker(@RequestParam String shopid, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            Map resultMap = userinfoService.getShopWorker(userSession.getOpenid(), shopid, SourceEnum.WEIXINAGENT);
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

    @PageSource(sourceType=SourceEnum.WEIXINAGENT)
    @RequestMapping(value = {"/queryChannelList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryChannelList(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            result.setSuccess(true);
            result.setResult(cpChannelService.queryChannelList(userSession.getOpenid(),shopid,SourceEnum.WEIXINAGENT));
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

    @PageSource(sourceType=SourceEnum.WEIXINAGENT)
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
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yida);
            if(deviceType!=TypeEnum.PRINTER) {
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,null,SourceEnum.WEIXINAGENT));
            }else {
                PrintTypeEnum printTypeEnum = PrintTypeEnum.nameOf(printtype);
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,printTypeEnum,SourceEnum.WEIXINAGENT));

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
}
