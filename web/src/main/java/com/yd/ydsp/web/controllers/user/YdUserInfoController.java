package com.yd.ydsp.web.controllers.user;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.Userinfo2ShopService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.biz.weixin.model.WeixinSmallUserInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.fasttext.codec.JavaScriptEncode;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/yd/user")
public class YdUserInfoController {

    public static final Logger logger = LoggerFactory.getLogger(YdUserInfoController.class);

    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;
    @Resource
    private Userinfo2ShopService userinfo2ShopService;


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/weixinsmall/save.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Boolean> saveWeiXinSmallUserInfo(HttpServletRequest request, @RequestParam String appid, @RequestBody WeixinSmallUserInfo weixinUserInfo)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            logger.info("userSession is :"+ JSON.toJSONString(userSession));
            if(!StringUtil.equals(weixinUserInfo.getOpenid(),userSession.getOpenid())||
                    !StringUtil.equals(appid,userSession.getAppid())){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
            boolean isSuccess = weixinSamll2ShopService.saveUserInfo(appid,weixinUserInfo);
            result.setResult(isSuccess);
            result.setSuccess(isSuccess);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("saveWeiXinSmallUserInfo is error: ",yex);
        }catch (Exception ex){
            logger.error("saveWeiXinSmallUserInfo is error:",ex);
            result.setResult(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/newAddress2C.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<String> newAddress2C(HttpServletRequest request, @RequestBody UserAddressInfoVO userAddressInfoVO)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            String addressId = userinfo2ShopService.newAddress(userSession.getUnionid(),userAddressInfoVO);
            result.setResult(addressId);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("newAddress2C is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("newAddress2C is error:",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/updateAddress2C.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Boolean> updateAddress2C(HttpServletRequest request, @RequestBody UserAddressInfoVO userAddressInfoVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            Boolean isok = userinfo2ShopService.updateAddress(userSession.getUnionid(),userAddressInfoVO);
            result.setResult(isok);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateAddress2C is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("updateAddress2C is error:",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/deleteByAddressid.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Boolean> deleteByAddressid(HttpServletRequest request, @RequestParam String addressid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            Boolean isok = userinfo2ShopService.deleteByAddressid(userSession.getUnionid(),addressid);
            result.setResult(isok);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("deleteByAddressid is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("deleteByAddressid is error:",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/selectByAddressid.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<UserAddressInfoVO> selectByAddressid(HttpServletRequest request, @RequestParam String addressid)
    {
        Result<UserAddressInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            UserAddressInfoVO userAddressInfoVO = userinfo2ShopService.selectByAddressid(userSession.getUnionid(),addressid);
            result.setResult(userAddressInfoVO);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("selectByAddressid is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("selectByAddressid is error:",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/getAddressList2C.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<List> getAddressList2C(HttpServletRequest request)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            logger.info("userSession is :"+JSON.toJSONString(userSession));
            result.setResult(userinfo2ShopService.selectByUnionId(userSession.getUnionid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getAddressList2C is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("getAddressList2C is error:",ex);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/setDefaultAddress.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Boolean> setDefaultAddress(HttpServletRequest request, @RequestParam String addressid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            Boolean isok = userinfo2ShopService.setDefaultAddress(userSession.getUnionid(),addressid);
            result.setResult(isok);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setDefaultAddress is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("setDefaultAddress is error:",ex);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/address/selectDefaultAddress.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<UserAddressInfoVO> selectDefaultAddress(HttpServletRequest request)
    {
        Result<UserAddressInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            UserAddressInfoVO userAddressInfoVO = userinfo2ShopService.selectDefaultAddress(userSession.getUnionid());
            result.setResult(userAddressInfoVO);
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("selectDefaultAddress is error: ",yex);
        }catch (Exception ex){
            result.setSuccess(false);
            result.setMsgInfo(ex.getMessage());
            logger.error("selectDefaultAddress is error:",ex);
        }
        return result;
    }


}
