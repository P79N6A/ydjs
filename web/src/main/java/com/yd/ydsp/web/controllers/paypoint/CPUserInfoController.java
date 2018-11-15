package com.yd.ydsp.web.controllers.paypoint;

import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
import com.yd.ydsp.client.domian.UserInfoVO;
import com.yd.ydsp.client.domian.paypoint.OwnerInfoVO;
import com.yd.ydsp.client.domian.paypoint.WorkerInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/9/1.
 */

@Controller
@RequestMapping(value = "/paypoint/cpuserinfo")
public class CPUserInfoController {

    public static final Logger logger = LoggerFactory.getLogger(CPUserInfoController.class);

    @Resource
    UserinfoService userinfoService;

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mobile/getCpUserCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> getCpUserCode(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidb);
            boolean isSuccess = userinfoService.generateCPUserMobileCode(userSession.getOpenid(), mobile, CheckMobileCodeTypeEnum.BINDMOBILE);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getCpUserCode is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getCpUserCode is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mobile/bindCpUserMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> bindCpUserMobile(@RequestParam String mobile,@RequestParam String code, @RequestParam(required = false) String password, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidb);
            boolean isSuccess = userinfoService.checkCPUserMobileCode(userSession.getOpenid(), mobile,code,password,CheckMobileCodeTypeEnum.BINDMOBILE);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("bindCpUserMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("bindCpUserMobile is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/ownerinfo/getMobileCheckCode.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> getMobileCheckCode( HttpServletRequest request,@RequestParam String mobile)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(userinfoService.generateCPUserMobileCode(userSession.getOpenid(),mobile, CheckMobileCodeTypeEnum.BINDOWNERMOBILE));
        }catch (YdException yex){
            result.setResult(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("paypoint/cpuserinfo/ownerinfo/getMobileCheckCode is error: ",yex);
        }catch (Exception ex){
            logger.error("paypoint/cpuserinfo/ownerinfo/getMobileCheckCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/ownerinfo/new/getMobileCheckCode.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> getNewOwnerMobileCheckCode( HttpServletRequest request,@RequestParam String mobile)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(userinfoService.generateCPUserMobileCode(userSession.getOpenid(),mobile, CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE));
        }catch (YdException yex){
            result.setResult(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("paypoint/cpuserinfo/ownerinfo/new/getMobileCheckCode is error: ",yex);
        }catch (Exception ex){
            logger.error("paypoint/cpuserinfo/ownerinfo/new/getMobileCheckCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }



    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mobile/removeCpUserMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> removeCpUserMobile(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession) request.getAttribute(CookieConstantTable.yidb);
            boolean isSuccess = userinfoService.unBindCPUserMobile(userSession.getOpenid(),mobile);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("removeCpUserMobile is error: ",yex);
        }catch (Exception ex){
            result.setResult(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("removeCpUserMobile is error: ",ex);
        }
        return result;
    }



//    @RequestMapping(value = {"/mobile/bindMobile.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public String registerUserByMobile(@RequestParam String mobile,@RequestParam String code, @RequestParam String password, HttpServletRequest request)
//    {
//        return "weixin/my/mobile";
//    }

    /**
     * 取负责人下的店铺
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopsByOwner.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List<String>> getShopsByOwner(HttpServletRequest request)
    {
        Result<List<String>> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            List<String> shops = userinfoService.getShopsByOwner(userSession.getOpenid());
            result.setSuccess(true);
            result.setResult(shops);

        }catch (Exception ex){
            logger.error("getShopsByOwner is error: ",ex);
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getCpUserInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getCpUserInfo(@RequestParam(required = false) String shopid, HttpServletRequest request)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            Object userInfoVO = userinfoService.getCpUserInfo(userSession.getOpenid(), shopid);
            result.setResult(userInfoVO);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getCpUserInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getCpUserInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addShopWorker.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> addShopWorker(@RequestBody WorkerInfoVO workerInfo, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean isSuccess = userinfoService.addShopWorker(userSession.getOpenid(), workerInfo);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addShopWoker is error: ",yex);

        }catch (Exception ex){
            logger.error("addShopWoker is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delShopWorker.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> delShopWorker(@RequestBody WorkerInfoVO workerInfo, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean isSuccess = userinfoService.delShopWorker(userSession.getOpenid(), workerInfo);
            result.setResult(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delShopWorker is error: ",yex);

        }catch (Exception ex){
            logger.error("delShopWorker is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopWorker.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getShopWorker(@RequestParam String shopid, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            Map resultMap = userinfoService.getShopWorker(userSession.getOpenid(), shopid);
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

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyShopOwnerWithStep1.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> modifyShopOwnerWithStep1(@RequestParam String shopid,
                                                    @RequestParam String mobile,
                                                    @RequestParam String checkCode,
                                                    HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean isOK = userinfoService.modifyShopOwnerWithStep1(userSession.getOpenid(), shopid,mobile,checkCode);
            result.setResult(isOK);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyShopOwnerWithStep1 is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyShopOwnerWithStep1 is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyShopOwnerWithStep2.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> modifyShopOwnerWithStep2(@RequestBody OwnerInfoVO ownerInfo, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean isOK = userinfoService.modifyShopOwnerWithStep2(userSession.getOpenid(), ownerInfo);
            result.setResult(isOK);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyShopOwnerWithStep2 is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyShopOwnerWithStep2 is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/youCanModifyOwnerInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> youCanModifyOwnerInfo(@RequestParam String shopid, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean isOK = userinfoService.youCanModifyOwnerInfo(userSession.getOpenid(), shopid);
            result.setResult(isOK);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("youCanModifyOwnerInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("youCanModifyOwnerInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

}
