package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.cp.*;
import com.yd.ydsp.biz.cp.model.GoodsInfoVO;
import com.yd.ydsp.biz.cp.model.ShopMainPageVO;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.openshop.ShopCashRechargeService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.YDUserCpAddressService;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.client.domian.ContractInfoVO;
import com.yd.ydsp.client.domian.ShopInfoVO;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.openshop.ShopCashRechargeConfigVO;
import com.yd.ydsp.client.domian.openshop.YdCpMemberCardVO;
import com.yd.ydsp.client.domian.paypoint.*;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.dal.entity.YdTableQrcode;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/27
 */
@Controller
@RequestMapping(value = "/paypoint/cp")
public class CPOptionController {
    public static final Logger logger = LoggerFactory.getLogger(CPOptionController.class);

    @Resource
    private YDUserCpAddressService ydUserCpAddressService;

    @Resource
    private CpService cpService;

    @Resource
    private CpBillService cpBillService;

    @Resource
    private CpMallServie cpMallService;

    @Resource
    private WeixinOauth2Service weixinOauth2Service2b;

    @Resource
    private ShopInfoService shopInfoService;

    @Resource
    private ShopActivityService shopActivityService;

    @Resource
    private ShopCashRechargeService shopCashRechargeService;

    /**
     * 取微信js接口调用权限
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/weixin/getJsapisig.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getJsapisig(HttpServletRequest request,@RequestParam String url)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(weixinOauth2Service2b.getJsapiTicket(url));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getJsapisig is error: ",yex);

        }catch (Exception ex){
            logger.error("getJsapisig is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 查询自己店铺的支付订单信息
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryOrderIdByCP.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryOrderIdByCP(HttpServletRequest request,@RequestParam String orderid)
    {
        Result<Map> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(shopInfoService.queryOrderIdByCP(userSession.getOpenid(),orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryOrderIdByCP is error: ",yex);

        }catch (Exception ex){
            logger.error("queryOrderIdByCP is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 查询默认的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryDefaultAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<UserAddressInfoVO> queryDefaultAddress(HttpServletRequest request)
    {
        Result<UserAddressInfoVO> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(ydUserCpAddressService.selectDefaultAddress(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryDefaultAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("queryDefaultAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 查询自己的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryConsigneeAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryConsigneeAddress(HttpServletRequest request)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(ydUserCpAddressService.selectByOpendId(userSession.getOpenid()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryConsigneeAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("queryConsigneeAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 设置一个默认的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/setDefaultAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setDefaultAddress(HttpServletRequest request,@RequestParam String addressid)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(ydUserCpAddressService.setDefaultAddress(userSession.getOpenid(),addressid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setDefaultAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("setDefaultAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 更新自己的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updateAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> updateAddress(HttpServletRequest request,@RequestParam String addressid,
                                         @RequestParam(required = false) String mobile,
                                         @RequestParam(required = false)String name,
                                         @RequestParam(required = false)String address,
                                         @RequestParam(required = false)String zipcode,
                                         @RequestParam(required = false)String district,
                                         @RequestParam(required = false)String city,
                                         @RequestParam(required = false)String province,
                                         @RequestParam(required = false)String country)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            int count = ydUserCpAddressService.update(userSession.getOpenid(),addressid,mobile,name,address,zipcode,district,city,province,country);
            if(count>0) {
                result.setResult(true);
            }else {
                result.setResult(false);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("updateAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 增加一个自己的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> addAddress(HttpServletRequest request,
                                         @RequestParam String mobile,
                                         @RequestParam String name,
                                         @RequestParam String address,
                                         @RequestParam(required = false)String zipcode,
                                         @RequestParam String district,
                                         @RequestParam String city,
                                         @RequestParam String province,
                                         @RequestParam(required = false)String country)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            int count = ydUserCpAddressService.insert(userSession.getOpenid(),mobile,name,address,zipcode,district,city,province,country);
            if(count>0) {
                result.setResult(true);
            }else {
                result.setResult(false);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("addAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 删除自己的收货地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> delAddress(HttpServletRequest request,@RequestParam String addressid)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            if(ydUserCpAddressService.deleteByAddressid(userSession.getOpenid(),addressid)>0) {
                result.setResult(true);
            }else{
                result.setResult(false);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("delAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addDevice.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> addDevice(HttpServletRequest request, @RequestParam String shopid,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false)String sn,@RequestParam(required = false)String key,
                                    @RequestParam String devicetype,
                                    @RequestParam(required = false)String description,
                                    @RequestParam(required = false)Integer times,
                                    @RequestParam(required = false) String printtype)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            TypeEnum deviceType = TypeEnum.nameOf(devicetype);
            if(deviceType==null||(deviceType!=TypeEnum.PRINTER&&deviceType!=TypeEnum.MONITOR)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"设备类型不正确");
            }
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            if(deviceType==TypeEnum.PRINTER){
                PrintTypeEnum printType = PrintTypeEnum.nameOf(printtype);
                if(printType==null){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"打印机类型不正确");
                }
                result.setResult(cpService.addPrinter(userSession.getOpenid(), shopid, name,sn,key,printType,times,description));
            }else {
                result.setResult(cpService.addDevice(userSession.getOpenid(), shopid, name, sn, deviceType, description));
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addDevice is error: ",yex);

        }catch (Exception ex){
            logger.error("addDevice is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyDevice.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> modifyDevice(HttpServletRequest request, @RequestParam String shopid,
                                        @RequestParam String deviceid,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false)String sn, @RequestParam(required = false)String key,
                                        @RequestParam String devicetype,
                                    @RequestParam(required = false)String description,
                                        @RequestParam(required = false)Integer times,
                                    @RequestParam(required = false) String printtype)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            TypeEnum dType = TypeEnum.nameOf(devicetype);
            if(dType==null||(dType!=TypeEnum.PRINTER&&dType!=TypeEnum.MONITOR)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"设备类型不正确");
            }
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            if(dType==TypeEnum.PRINTER){
                PrintTypeEnum ptType = PrintTypeEnum.nameOf(printtype);
                if(ptType==null){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"打印机类型不正确");
                }
                result.setResult(cpService.modifyPrinter(userSession.getOpenid(),deviceid, shopid,name,sn,key,times,ptType,description));
            }else {
                result.setResult(cpService.modifyDevice(userSession.getOpenid(),deviceid, shopid, name, sn, dType, description));
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyDevice is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyDevice is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delDevice.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> delDevice(HttpServletRequest request, @RequestParam String shopid,
                                        @RequestParam String deviceid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.delDevice(userSession.getOpenid(),shopid,deviceid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delDevice is error: ",yex);

        }catch (Exception ex){
            logger.error("delDevice is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryDeviceInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryDeviceInfo(HttpServletRequest request, @RequestParam String shopid, @RequestParam String devicetype,
                                     @RequestParam String deviceid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            TypeEnum deviceType = TypeEnum.nameOf(devicetype);
            if(deviceType!=TypeEnum.PRINTER&&deviceType!=TypeEnum.MONITOR){
                result.setResult(null);
                result.setSuccess(false);
                result.setMsgInfo("设备类型传值不正确");
                return result;
            }
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryDeviceInfo(userSession.getOpenid(),shopid,deviceid,deviceType));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryDeviceInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("queryDeviceInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
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
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            if(deviceType!=TypeEnum.PRINTER) {
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,null));
            }else {
                PrintTypeEnum printTypeEnum = PrintTypeEnum.nameOf(printtype);
                result.setResult(cpService.queryDeviceInfoList(userSession.getOpenid(), shopid, deviceType,printTypeEnum));

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



    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/batchBindPrinter.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> batchBindPrinter(HttpServletRequest request, @RequestParam String shopid, @RequestParam String printerid,@RequestBody List<String> tables)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.batchBindPrinter(userSession.getOpenid(),shopid,printerid,tables));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("batchBindPrinter is error: ",yex);

        }catch (Exception ex){
            logger.error("batchBindPrinter is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/shop/setPayModel.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setPayModel(HttpServletRequest request, @RequestParam String shopid, @RequestParam Boolean isPayNow)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.setPayModel(userSession.getOpenid(),shopid,isPayNow));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setPayModel is error: ",yex);

        }catch (Exception ex){
            logger.error("setPayModel is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/shop/setUserPayModel.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setUserPayModel(HttpServletRequest request, @RequestParam String shopid, @RequestParam Boolean isPayNow)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.setUserPayModel(userSession.getOpenid(),shopid,isPayNow));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setUserPayModel is error: ",yex);

        }catch (Exception ex){
            logger.error("setUserPayModel is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/shop/isPayNow.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> isPayNow(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopInfoService.isPayNow(shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("isPayNow is error: ",yex);

        }catch (Exception ex){
            logger.error("isPayNow is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/shop/isUserPayNow.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> isUserPayNow(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopInfoService.userPayNow(shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("isUserPayNow is error: ",yex);

        }catch (Exception ex){
            logger.error("isUserPayNow is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addTable.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> addTable(HttpServletRequest request, @RequestBody DiningtableVO tableVO)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
//            DiningtableVO diningtableVO = JSON.parseObject(tableVO,DiningtableVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.addTable(userSession.getOpenid(),tableVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addTable is error: ",yex);

        }catch (Exception ex){
            logger.error("addTable is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyTable.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> modifyTable(HttpServletRequest request, @RequestBody DiningtableVO tableVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
//            DiningtableVO diningtableVO = JSON.parseObject(tableVO,DiningtableVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.modifyTable(userSession.getOpenid(),tableVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyTable is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyTable is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyTableQrcodes.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> modifyTableQrcodes(HttpServletRequest request, @RequestBody List<YdTableQrcode> tableQrcodeList)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.modifyQrcode(userSession.getOpenid(),tableQrcodeList));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyTableQrcodes is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyTableQrcodes is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delTable.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> delTable(HttpServletRequest request, @RequestParam String shopid, @RequestParam String tableid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.delTable(userSession.getOpenid(),shopid,tableid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delTable is error: ",yex);

        }catch (Exception ex){
            logger.error("delTable is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryTableByShopId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryTableByShopId(HttpServletRequest request, @RequestParam String shopid, @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryTableByShopId(userSession.getOpenid(),shopid,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryTableByShopId is error: ",yex);

        }catch (Exception ex){
            logger.error("queryTableByShopId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryTableByTableId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<DiningtableVO> queryTableByTableId(HttpServletRequest request, @RequestParam String shopid, @RequestParam String tableid)
    {
        Result<DiningtableVO> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpService.queryTableByTableId(shopid,tableid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryTableByTableId is error: ",yex);

        }catch (Exception ex){
            logger.error("queryTableByTableId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryTableByQrcode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<DiningtableVO> queryTableByQrcode(HttpServletRequest request, @RequestParam String qrcode)
    {
        Result<DiningtableVO> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpService.queryTableByQrcode(qrcode));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryTableByQrcode is error: ",yex);

        }catch (Exception ex){
            logger.error("queryTableByQrcode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getQrcodes.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getQrcodes(HttpServletRequest request, @RequestParam String tableid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult( cpService.getQrcodes(userSession.getOpenid(),tableid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getQrcodes is error: ",yex);

        }catch (Exception ex){
            logger.error("getQrcodes is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取商品信息表
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getGoodsInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getGoodsInfo(HttpServletRequest request,@RequestParam(required = false) String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.getGoodsInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getGoodsInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getGoodsInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 计算产品金额
     * @return
     */
    @RequestMapping(value = {"/public/getTotalAmountByGoods.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<GoodsInfoVO> getTotalAmountByGoods(HttpServletRequest request, @RequestBody GoodsInfoVO goodsInfoVO)
    {
        Result<GoodsInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpMallService.getTotalAmountByGoods(goodsInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getTotalAmountByGoods is error: ",yex);

        }catch (Exception ex){
            logger.error("getTotalAmountByGoods is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 创建订单（也就是进入前端订单确认页面）
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/submitOrder.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<CpMallOrderVO> submitOrder(HttpServletRequest request, @RequestBody GoodsInfoVO goodsInfo)
    {
        Result<CpMallOrderVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.submitOrder(userSession.getOpenid(),goodsInfo));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("shopingmall submitOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("shopingmall submitOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 保存买家留言
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/saveBuyerMessage.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> saveBuyerMessage(HttpServletRequest request,@RequestParam String orderid,@RequestParam(required = false) String buyerMessage)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.saveBuyerMessage(userSession.getOpenid(),orderid,buyerMessage));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("saveBuyerMessage is error: ",yex);

        }catch (Exception ex){
            logger.error("saveBuyerMessage is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 关闭（取消）订单
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/cancelOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> cancelOrder(HttpServletRequest request,@RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.cancelOrder(userSession.getOpenid(),orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cancelOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("cancelOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 设置订单收货的地址
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/setDeliveryAddress.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setDeliveryAddress(HttpServletRequest request,@RequestParam String orderid, @RequestParam String deliveryAddress)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.setDeliveryAddress(userSession.getOpenid(),orderid,deliveryAddress));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setDeliveryAddress is error: ",yex);

        }catch (Exception ex){
            logger.error("setDeliveryAddress is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 取订单列表，pageIndex最小为1，count为每页订单数量
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mall/getOrderList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrderList(HttpServletRequest request,@RequestParam(required = false) Integer pageIndex, @RequestParam(required = false) Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.getOrderList(userSession.getOpenid(),pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrderList is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrderList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取订单对象
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mall/getOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<CpMallOrderVO> getOrder(HttpServletRequest request,@RequestParam String orderid)
    {
        Result<CpMallOrderVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.getOrder(userSession.getOpenid(),orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }



    /**
     * 支付商城订单
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/mall/payMall.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> payMall(HttpServletRequest request,@RequestParam String orderid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpMallService.payMall(userSession.getOpenid(),orderid,userSession.getUserIp()));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("payMall is error: ",yex);

        }catch (Exception ex){
            logger.error("payMall is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取当前用户默认的店铺
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getDefaultShop.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShopSimpleInfoVO> getDefaultShop(HttpServletRequest request)
    {
        Result<ShopSimpleInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.getDefaultShop(userSession.getOpenid()));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getDefaultShop is error: ",yex);
        }catch (Exception ex){
            logger.error("getDefaultShop is error: ",ex);
            result.setMsgInfo("获取失败！");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取当前用户默认的店铺
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopsByCpUser.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getShopsByCpUser(HttpServletRequest request)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);;
            result.setResult(shopInfoService.getShopsByCpUser(userSession.getOpenid()));

        }catch (Exception ex){
            logger.error("getShopsByCpUser is error: ",ex);
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 取消费者购物扫码前缀(扫码点商品的方式)，也就是没有qrcode的值的前缀
     * @param request
     * @return
     */
    @RequestMapping(value = {"/public/getQrPreUrl.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getQrPreUrl(HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(DiamondYdSystemConfigHolder.getInstance().getB2cPreUrl());
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getQrPreUrl is error: ",yex);

        }catch (Exception ex){
            logger.error("getQrPreUrl is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取消费者购物扫码前缀(线上商城渠道方式)，也就是没有qrcode的值的前缀
     * @param request
     * @return
     */
    @RequestMapping(value = {"/public/getChannelQrPreUrl.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getChannelQrPreUrl(HttpServletRequest request)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(DiamondYdSystemConfigHolder.getInstance().getCpChannelPreUrl());
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getChannelQrPreUrl is error: ",yex);

        }catch (Exception ex){
            logger.error("getChannelQrPreUrl is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 取一个Qrcode的随机值
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getRandomQrCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> getRandomQrCode(HttpServletRequest request,@RequestParam String shopid,@RequestParam(required = false) Integer codeType)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getRandomQrCode(userSession.getOpenid(),shopid,codeType));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getRandomQrCode is error: ",yex);

        }catch (Exception ex){
            logger.error("getRandomQrCode is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 取合约信息用于显示合约的名称及内容
     * @param request
     * @param contractId
     * @return
     */
    @RequestMapping(value = {"/public/getContractInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ContractInfoVO> getContractInfo(HttpServletRequest request, @RequestParam String contractId)
    {
        Result<ContractInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(cpService.getContractInfo(contractId));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getContractInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getContractInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 协议查看页面的显示列表及数据信息获取
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getUserContractInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getUserContractInfo(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getUserContractInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getUserContractInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getUserContractInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 店铺主页今日信息
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopMainPageData.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShopMainPageVO> getShopMainPageData(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<ShopMainPageVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getShopMainPageData(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getShopMainPageData is error: ",yex);

        }catch (Exception ex){
            logger.error("getShopMainPageData is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * B端订单管理页，分页查询，支持按日期与桌位
     * @param request
     * @param shopid
     * @param queryDate
     * @param tableid
     * @param pageIndex
     * @param count
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOrder2CByShop.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrder2CByShop(HttpServletRequest request, @RequestParam String shopid,
                                         @RequestParam(required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date queryDate,
                                         @RequestParam(required = false) String tableid,
                                         @RequestParam(required = false) Integer pageIndex,
                                         @RequestParam(required = false) Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getOrder2CByShop(userSession.getOpenid(),shopid,queryDate,tableid,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder2CByShop is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder2CByShop is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 店铺查订单详情
     * @param request
     * @param shopid
     * @param orderid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOrder2CDetailByShop.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Order2CVO> getOrder2CDetailByShop(HttpServletRequest request, @RequestParam String shopid,@RequestParam String orderid)
    {
        Result<Order2CVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getOrder2CDetailByShop(userSession.getOpenid(),shopid,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder2CDetailByShop is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder2CDetailByShop is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 店铺管理员在进行订单打折及退商品操作时计算价格
     * @param request
     * @param data
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/calculateOrder2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Order2CVO> calculateOrder2C(HttpServletRequest request, @RequestBody Order2CVO data)
    {
        Result<Order2CVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.calculateOrder2C(userSession.getOpenid(),data));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("calculateOrder2C is error: ",yex);

        }catch (Exception ex){
            logger.error("calculateOrder2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

//    /**
//     * 店铺管理员进行订单打折及退商品操作时提交数据
//     * @param request
//     * @param data
//     * @return
//     */
//    @PageSource(sourceType=SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/modifyOrder2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Boolean> modifyOrder2C(HttpServletRequest request, @RequestBody Order2CVO data)
//    {
//        Result<Boolean> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(cpService.modifyOrder2C(userSession.getOpenid(),data));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("modifyOrder2C is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("modifyOrder2C is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

//    /**
//     * 店铺管理员进行订单取消操作
//     * @param request
//     * @param shopid
//     * @param orderid
//     * @return
//     */
//    @PageSource(sourceType=SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/cancelOrder2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Boolean> cancelOrder2C(HttpServletRequest request, @RequestParam String shopid,@RequestParam String orderid)
//    {
//        Result<Boolean> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(cpService.cancelOrder2C(userSession.getOpenid(),shopid,orderid));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("cancelOrder2C is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("cancelOrder2C is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

//    /**
//     * 管理员或者服务员确认消费者已经使用现金支付了订单金额
//     * @param request
//     * @param shopid
//     * @param orderid
//     * @return
//     */
//    @PageSource(sourceType=SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/useCashPayWithOrder2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Boolean> useCashPayWithOrder2C(HttpServletRequest request, @RequestParam String shopid,@RequestParam String orderid)
//    {
//        Result<Boolean> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(cpService.useCashPayWithOrder2C(userSession.getOpenid(),shopid,orderid));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("useCashPayWithOrder2C is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("useCashPayWithOrder2C is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }


    /**
     * 店铺信息管理页数据获取，查看是可以的，不一定要owner，这里修改一下
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getShopInfoByInfoPage.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShopInfoVO> getShopInfoByInfoPage(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<ShopInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.getShopInfoByInfoPage(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getShopInfoByInfoPage is error: ",yex);

        }catch (Exception ex){
            logger.error("getShopInfoByInfoPage is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


//    /**
//     * 店铺信息管理页数据修改操作
//     * @param request
//     * @param shopInfoVO
//     * @return
//     */
//    @PageSource(sourceType=SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/setShopInfoByOwner.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<Boolean> setShopInfoByOwner(HttpServletRequest request, @RequestBody ShopInfoVO shopInfoVO)
//    {
//        Result<Boolean> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(shopInfoService.setShopInfoByOwner(userSession.getOpenid(),shopInfoVO));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("setShopInfoByOwner is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("setShopInfoByOwner is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }


    /**
     * 店铺信息管理页数据修改操作
     * @param request
     * @param shopInfoVO
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/setShopInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> setShopInfo(HttpServletRequest request, @RequestBody ShopInfoVO shopInfoVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.setShopInfo(userSession.getOpenid(),shopInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setShopInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("setShopInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 取银行卡信息，必须严格按openid shopid取得
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getBankInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BankInfoVO> getBankInfo(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<BankInfoVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.getBankInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getBankInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getBankInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 一个店铺的owner进行银行收款帐号信息提交
     * @param request
     * @param bankInfoVO
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/saveBankInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveBankInfo(HttpServletRequest request, @RequestBody BankInfoVO bankInfoVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.saveBankInfo(userSession.getOpenid(),bankInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("saveBankInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("saveBankInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 日报汇总数据
     * @param request
     * @param shopid
     * @param date
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getReportDataByDate.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BillReportVO> getReportDataByDate(HttpServletRequest request, @RequestParam String shopid,
                                                    @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date)
    {
        Result<BillReportVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getReportDataByDate(userSession.getOpenid(),shopid,date));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getReportDataByDate is error: ",yex);

        }catch (Exception ex){
            logger.error("getReportDataByDate is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 日报订单列表数据显示
     * @param request
     * @param shopid
     * @param date
     * @param pageIndex
     * @param count
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOrder2CByDate.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrder2CByDate(HttpServletRequest request, @RequestParam String shopid,
                                                    @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date,
                                                 @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getOrder2CByDate(userSession.getOpenid(),shopid,date,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder2CByDate is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder2CByDate is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 周报汇总数据
     * @param request
     * @param shopid
     * @param year
     * @param week
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getReportDataByWeek.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BillReportVO> getReportDataByWeek(HttpServletRequest request, @RequestParam String shopid,
                                                    @RequestParam Integer year, @RequestParam Integer week)
    {
        Result<BillReportVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getReportDataByWeek(userSession.getOpenid(),shopid,year,week));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getReportDataByWeek is error: ",yex);

        }catch (Exception ex){
            logger.error("getReportDataByWeek is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 周报订单列表数据显示
     * @param request
     * @param shopid
     * @param year
     * @param week
     * @param pageIndex
     * @param count
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOrder2CByWeek.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrder2CByWeek(HttpServletRequest request, @RequestParam String shopid,
                                         @RequestParam Integer year, @RequestParam Integer week,
                                         @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getOrder2CByWeek(userSession.getOpenid(),shopid,year,week,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder2CByWeek is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder2CByWeek is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 月报汇总数据
     * @param request
     * @param shopid
     * @param year
     * @param month
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getReportDataByMonth.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BillReportVO> getReportDataByMonth(HttpServletRequest request, @RequestParam String shopid,
                                                    @RequestParam Integer year, @RequestParam Integer month)
    {
        Result<BillReportVO> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getReportDataByMonth(userSession.getOpenid(),shopid,year,month));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getReportDataByMonth is error: ",yex);

        }catch (Exception ex){
            logger.error("getReportDataByMonth is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 周报订单列表数据显示
     * @param request
     * @param shopid
     * @param year
     * @param month
     * @param pageIndex
     * @param count
     * @return
     */
    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOrder2CByMonth.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrder2CByMonth(HttpServletRequest request, @RequestParam String shopid,
                                         @RequestParam Integer year, @RequestParam Integer month,
                                         @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpBillService.getOrder2CByMonth(userSession.getOpenid(),shopid,year,month,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getOrder2CByMonth is error: ",yex);

        }catch (Exception ex){
            logger.error("getOrder2CByMonth is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * ----------------------会员卡管理-----------------------
     *
     */

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updateMemberCardInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> updateMemberCardInfo(HttpServletRequest request, @RequestBody YdCpMemberCardVO cpMemberCardVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopInfoService.updateMemberCardInfo(userSession.getOpenid(),cpMemberCardVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateMemberCardInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("updateMemberCardInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryMemberCardInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryMemberCardInfoList(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopInfoService.queryMemberCardInfoList(shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryMemberCardInfoList is error: ",yex);

        }catch (Exception ex){
            logger.error("queryMemberCardInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryMemberCardInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryMemberCardInfo(HttpServletRequest request, @RequestParam String cardid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopInfoService.queryMemberCardInfo(cardid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryMemberCardInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("queryMemberCardInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * ------------------------------------独立的商城模式-------------------------------------------
     */

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/calculateUserOrder2C.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ShopOrder2C> calculateUserOrder2C(HttpServletRequest request, @RequestBody ShopOrder2C shopOrder2C)
    {
        Result<ShopOrder2C> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.calculateUserOrder2C(userSession.getOpenid(),shopOrder2C));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("calculateUserOrder2C is error: ",yex);

        }catch (Exception ex){
            logger.error("calculateUserOrder2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyUserOrder2C.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> modifyUserOrder2C(HttpServletRequest request, @RequestBody ShopOrder2C shopOrder2C)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.modifyUserOrder2C(userSession.getOpenid(),shopOrder2C));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyUserOrder2C is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyUserOrder2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/cancelUserOrder2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> cancelUserOrder2C(HttpServletRequest request, @RequestParam String shopid, @RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.cancelUserOrder2C(userSession.getOpenid(),shopid,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cancelUserOrder2C is error: ",yex);

        }catch (Exception ex){
            logger.error("cancelUserOrder2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/offlineToPay.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> offlineToPay(HttpServletRequest request, @RequestParam String shopid, @RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.offlineToPay(userSession.getOpenid(),shopid,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("offlineToPay is error: ",yex);

        }catch (Exception ex){
            logger.error("offlineToPay is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/userOrderTaking.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> userOrderTaking(HttpServletRequest request, @RequestParam String shopid, @RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.userOrderTaking(userSession.getOpenid(),shopid,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("userOrderTaking is error: ",yex);

        }catch (Exception ex){
            logger.error("userOrderTaking is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/userOrderSendOut.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> userOrderSendOut(HttpServletRequest request, @RequestParam String shopid, @RequestParam String orderid,
                                            @RequestParam(required = false) String desc)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.userOrderSendOut(userSession.getOpenid(),shopid,orderid,desc));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("userOrderSendOut is error: ",yex);

        }catch (Exception ex){
            logger.error("userOrderSendOut is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryShopOrderIndoor.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryShopOrderIndoor(HttpServletRequest request, @RequestParam String shopid, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date queryDate,
                                                @RequestParam(required = false) String tableid,@RequestParam(required = false) String status,
                                                @RequestParam Integer pageIndex,
                                                @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryShopOrderIndoor(userSession.getOpenid(), shopid, queryDate,tableid,status,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopOrderIndoor is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopOrderIndoor is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryShopAllMoneyOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryShopAllMoneyOrder(HttpServletRequest request, @RequestParam String shopid, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date queryDate,
                                             @RequestParam(required = false) String status,
                                             @RequestParam Integer pageIndex,
                                             @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryShopAllMoneyOrder(userSession.getOpenid(), shopid, queryDate,status,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopAllMoneyOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopAllMoneyOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryShopOrderOnline.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryShopOrderOnline(HttpServletRequest request, @RequestParam String shopid, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date queryDate,
                                             @RequestParam(required = false) String channelid,@RequestParam(required = false) String status,
                                             @RequestParam Integer pageIndex,
                                             @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryShopOrderOnline(userSession.getOpenid(), shopid, queryDate,channelid,status,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopOrderOnline is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopOrderOnline is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryShopAllOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryShopAllOrder(HttpServletRequest request, @RequestParam String shopid, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date queryDate,
                                             @RequestParam(required = false) String entercode,@RequestParam(required = false) String status,
                                             @RequestParam Integer pageIndex,
                                             @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryShopAllOrder(userSession.getOpenid(), shopid, queryDate,entercode,status,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopAllOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopAllOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 查询用户订单详情
     * @param request
     * @param orderid
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryUserOrderDetail.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryUserOrderDetail(HttpServletRequest request, @RequestParam String shopid, @RequestParam String orderid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryUserOrderDetail(userSession.getOpenid(),shopid,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryUserOrderDetail is error: ",yex);

        }catch (Exception ex){
            logger.error("queryUserOrderDetail is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 识别code是什么类型
     * @param request
     * @param code
     * @return
     */
    @RequestMapping(value = {"/public/distinguishTypeEnum.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Integer> distinguishTypeEnum(HttpServletRequest request, @RequestParam String code)
    {
        Result<Integer> result = new Result<>();
        result.setSuccess(true);
        try {
            TypeEnum typeEnum = TypeEnum.getTypeOfSN(code);
            if(typeEnum!=null) {
                result.setResult(typeEnum.getType());
            }else {
                result.setResult(null);
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("distinguishTypeEnum is error: ",yex);

        }catch (Exception ex){
            logger.error("distinguishTypeEnum is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 识别code是什么类型
     * @param request
     * @param activityid
     * @return
     */
    @RequestMapping(value = {"/public/queryShopActivity.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopActivity(HttpServletRequest request, @RequestParam String activityid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopActivityService.queryShopActivity(activityid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopActivity is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopActivity is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updateShopActivity.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> updateShopActivity(HttpServletRequest request, @RequestBody YdShopActivityInfoVO shopActivityInfoVO)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopActivityService.updateShopActivity(userSession.getOpenid(),shopActivityInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateShopActivity is error: ",yex);

        }catch (Exception ex){
            logger.error("updateShopActivity is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/closeShopActivity.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> closeShopActivity(HttpServletRequest request, @RequestParam String activityid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopActivityService.closeShopActivity(userSession.getOpenid(),activityid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("closeShopActivity is error: ",yex);

        }catch (Exception ex){
            logger.error("closeShopActivity is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/createMoneyOrder.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> createMoneyOrder(HttpServletRequest request, @RequestParam String shopid,@RequestParam BigDecimal money,
                                           @RequestParam String name,@RequestParam(required = false) String desc)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.createMoneyOrder(userSession.getOpenid(),shopid,money,name,desc));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("createMoneyOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("createMoneyOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/weixin/microPay.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> weixinMicroPay(HttpServletRequest request, @RequestParam String orderid,@RequestParam String authCode)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.microPay(userSession.getOpenid(),orderid,authCode));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("weixinMicroPay is error: ",yex);

        }catch (Exception ex){
            logger.error("weixinMicroPay is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/weixin/pay/queryOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryWeiXinPayOrder(HttpServletRequest request, @RequestParam String orderid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(cpService.queryWeiXinPayOrder(userSession.getOpenid(),orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryWeiXinPayOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("queryWeiXinPayOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updateShopCashRechargeConfig.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateShopCashRechargeConfig(HttpServletRequest request, @RequestBody ShopCashRechargeConfigVO rechargeConfigVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopCashRechargeService.updateShopCashRechargeConfig(userSession.getOpenid(),rechargeConfigVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateShopCashRechargeConfig is error: ",yex);

        }catch (Exception ex){
            logger.error("updateShopCashRechargeConfig is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryShopCashRechargeConfig.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopCashRechargeConfig(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopCashRechargeService.queryShopCashRechargeConfig(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopCashRechargeConfig is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopCashRechargeConfig is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryShopCashRechargeConfig2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryShopCashRechargeConfig2C(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopCashRechargeService.queryShopCashRechargeConfig2C(shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopCashRechargeConfig2C is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopCashRechargeConfig2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

}
