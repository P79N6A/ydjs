package com.yd.ydsp.web.controllers.paypoint;

import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.cp.model.ApplyShopInfoVO;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
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
 * Created by zengyixun on 17/9/4.
 * CP申请店铺入驻
 * 入驻步骤：
 * 1､填写申请店铺基本信息，同时绑定CP公众号中微信帐号的手机
 * 2､选择套餐，生成支付订单，并支付成功
 * 3､物料（打印机、二维码标牌等）邮寄送货。
 * 4､签收，并注册打印机调试成功。（从此时起，开始计时、计费）
 */

@PageSource(sourceType = SourceEnum.WEIXIN2B)
@Controller
@RequestMapping(value = "/paypoint/shop")
public class CPApplyFlowController {

    public static final Logger logger = LoggerFactory.getLogger(CPApplyFlowController.class);

    @Resource
    ShopInfoService shopInfoService;
    @Resource
    UserinfoService userinfoService;


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getMobileCheckCode.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> getMobileCheckCode( HttpServletRequest request,@RequestParam String mobile)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(userinfoService.generateCPUserMobileCode(userSession.getOpenid(),mobile, CheckMobileCodeTypeEnum.BINDSHOPMOBILE));
        }catch (YdException yex){
            result.setResult(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("paypoint/shop/getMobileCheckCode is error: ",yex);
        }catch (Exception ex){
            logger.error("paypoint/shop/getMobileCheckCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ApplyShopInfoVO> getApplyStepOneInfo(HttpServletRequest request,@RequestParam String shopid)
    {
        Result<ApplyShopInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            ApplyShopInfoVO applyShopInfoVO = shopInfoService.getApplyShopInfoByShopId(shopid);
            result.setResult(applyShopInfoVO);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResult(null);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("paypoint/shop/getShopInfo is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getShopInfo is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getApplyingShopInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ApplyShopInfoVO> getApplyingShopInfo(HttpServletRequest request)
    {
        Result<ApplyShopInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            ApplyShopInfoVO applyShopInfoVO = shopInfoService.getApplyingShopInfo(userSession.getOpenid());
            result.setResult(applyShopInfoVO);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResult(null);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("paypoint/shop/getApplyingShopInfo is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getApplyingShopInfo is error: ",ex);
        }
        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/submitShopInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> submitShopInfo(HttpServletRequest request,@RequestParam(required = false) String contractid,@RequestBody ApplyShopInfoVO applyShopData)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            String shopidResult = shopInfoService.applyShopJoin(userSession.getOpenid(),contractid,applyShopData);
            result.setResult(shopidResult);
            if(shopidResult==null){
                result.setSuccess(false);
            }
        }catch (YdException yex){
            result.setResult(null);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("submitShopInfo is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setResult(null);
            result.setMsgInfo(ex.getMessage());
            logger.error("submitShopInfo is input: "+applyShopData);
            logger.error("submitShopInfo is error: ",ex);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/createOrderBySetMeal.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> createOrderBySetMeal(HttpServletRequest request,@RequestParam String shopid,@RequestParam String setMealType, @RequestParam Integer monthNum)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            Map setMealMap = shopInfoService.createOrderBySetMeal(userSession.getOpenid(),shopid,setMealType,monthNum,userSession.getUserIp());
            result.setResult(setMealMap);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("createOrderBySetMeal is error: ",yex);

        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("createOrderBySetMeal is error: ",ex);
        }
        return result;

    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/setCPDefaultShop.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> setCPDefaultShop(HttpServletRequest request,@RequestParam String shopid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            boolean setResult = userinfoService.setCPDefaultShop(userSession.getOpenid(),shopid);
            result.setResult(setResult);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("setCPDefaultShop is error: ",ex);
        }
        return result;
    }


    @RequestMapping(value = {"/public/getSetMealInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String,Object>>> getSetMealInfo(HttpServletRequest request)
    {
        Result<List<Map<String,Object>>> result = new Result<>();
        result.setSuccess(true);
        try {
            List<Map<String,Object>> setMeals = shopInfoService.getSetMealInfo();
            result.setResult(setMeals);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getSetMealInfo is error: ",ex);
        }
        return result;
    }

    @RequestMapping(value = {"/public/getTotalAmountBySetMeal.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getTotalAmountBySetMeal(HttpServletRequest request,@RequestParam String setMealType, @RequestParam Integer monthNum)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopInfoService.getTotalAmountBySetMeal(setMealType,monthNum,false));
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getTotalAmountBySetMeal is error: ",ex);
        }
        return result;
    }

}
//getSetMealInfo