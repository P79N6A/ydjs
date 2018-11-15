package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.customer.ShoppingMall2CService;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2C;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.ShopInfoVO2C;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/paypoint/customer")
public class ShoppingMall2cController {

    public static final Logger logger = LoggerFactory.getLogger(ShoppingMall2cController.class);

    @Resource
    private ShoppingMall2CService shoppingMall2CService;
    @Resource
    private WareService wareService;

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/add2Cart.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ShoppingCartVO> add2Cart(HttpServletRequest request, @RequestParam String qrCode,@RequestBody Map<String, Integer> skuMap)
    {
        Result<ShoppingCartVO> result = new Result<>();
        result.setSuccess(true);
        try{
//            Map<String, Integer> skuMap = JSON.parseObject(goods, HashMap.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.updateCartInfo(userSession.getOpenid(),qrCode,skuMap));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("add2Cart is error: ",yex);

        }catch (Exception ex){
            logger.error("add2Cart is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getCartInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShoppingCartVO> getCartInfo(HttpServletRequest request, @RequestParam String qrCode)
    {
        Result<ShoppingCartVO> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            Map resultMap = shoppingMall2CService.getCartInfo(userSession.getOpenid(),qrCode);
            if(resultMap!=null){
                result.setResult((ShoppingCartVO)resultMap.get("cartVO"));
            }else {
                result.setResult(null);
            }

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getCartInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getCartInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/clearCartInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> clearCartInfo(HttpServletRequest request, @RequestParam String qrCode)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.clearShoppintCart(userSession.getOpenid(),qrCode));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("clearCartInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("clearCartInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/submitOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Order2CVO> submitOrder(HttpServletRequest request, @RequestParam String qrCode,@RequestParam(required = false) String description)
    {
        Result<Order2CVO> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.submitOrder(userSession.getOpenid(),qrCode,description));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("customer submitOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("customer submitOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/payConsumerOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> payConsumerOrder(HttpServletRequest request, @RequestParam String orderid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.payConsumerOrder(userSession.getOpenid(),orderid,userSession.getUserIp()));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("customer payConsumerOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("customer payConsumerOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Order2CVO> getOrder(HttpServletRequest request, @RequestParam String orderid)
    {
        Result<Order2CVO> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.getOrder(userSession.getOpenid(),orderid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("customer getOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("customer getOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getOrderList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getOrderList(HttpServletRequest request, @RequestParam(required = false) Integer status, @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.getOrderListByConsumerId(userSession.getOpenid(),status,pageIndex,count));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("customer getOrderList is error: ",yex);

        }catch (Exception ex){
            logger.error("customer getOrderList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getWareInfoByShopId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getWareInfoByShopId(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareInfo2CByShopId(shopid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareInfoByShopId@C is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareInfoByShopId2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getShopInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShopInfoVO2C> getShopInfo(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<ShopInfoVO2C> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(shoppingMall2CService.getShopInfoByCustomer(shopid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getShopInfoBy2C is error: ",yex);

        }catch (Exception ex){
            logger.error("getShopInfoBy2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * ------------------------------------以下为独立的C端商城模式-------------------------------------------
     */

    /**
     * 计算购卖的商品金额
     * @param request
     * @param shoppingOrderSkuVOS
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/calculateOrderMoney.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ShopOrder2C> calculateOrderMoney(HttpServletRequest request, @RequestBody List<ShoppingOrderSkuVO> shoppingOrderSkuVOS)
    {
        Result<ShopOrder2C> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.calculateOrderMoney(userSession,shoppingOrderSkuVOS));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("calculateOrderMoney is error: ",yex);

        }catch (Exception ex){
            logger.error("calculateOrderMoney is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 订单创建接口
     * @param request
     * @param addressid
     * @param shopOrder2C
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/createUserOrder.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<ShopOrder2C> createUserOrder(HttpServletRequest request, @RequestParam(required = false) String addressid, @RequestBody ShopOrder2C shopOrder2C)
    {
        Result<ShopOrder2C> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.createUserOrder(userSession, addressid,shopOrder2C));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("createUserOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("createUserOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getDeductionMoney.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getDeductionMoney(HttpServletRequest request, @RequestParam String shopid, @RequestParam BigDecimal orderMoney)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.getDeductionMoney(userSession,shopid,orderMoney));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getDeductionMoney is error: ",yex);

        }catch (Exception ex){
            logger.error("getDeductionMoney is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    //rewardConsumptionPoint

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/getRewardPoint.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Integer> getRewardPoint(HttpServletRequest request, @RequestParam String shopid, @RequestParam BigDecimal orderMoney)
    {
        Result<Integer> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.rewardConsumptionPoint(userSession,shopid,orderMoney));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getRewardPoint is error: ",yex);

        }catch (Exception ex){
            logger.error("getRewardPoint is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 消费者取消订单
     * @param request
     * @param orderid
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/cancelUserOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> cancelUserOrder(HttpServletRequest request, @RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.cancelUserOrder(userSession,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cancelUserOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("cancelUserOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 消费者确认收货
     * @param request
     * @param orderid
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/confirmUserOrder.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> confirmUserOrder(HttpServletRequest request, @RequestParam String orderid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.confirmUserOrder(userSession,orderid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("confirmUserOrder is error: ",yex);

        }catch (Exception ex){
            logger.error("confirmUserOrder is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    /**
     * 用户订单列表查询接口
     * @param request
     * @param shopid
     * @param pageIndex
     * @param count
     * @param searchType
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryUserOrderList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryUserOrderList(HttpServletRequest request, @RequestParam String shopid, @RequestParam Integer pageIndex, @RequestParam Integer count, @RequestParam Integer searchType)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryUserOrderList(userSession.getUnionid(),shopid,pageIndex,count,searchType));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryUserOrderList is error: ",yex);

        }catch (Exception ex){
            logger.error("queryUserOrderList is error: ",ex);
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
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryUserOrderDetail.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<ShopOrderExt2C> queryUserOrderDetail(HttpServletRequest request,@RequestParam String orderid)
    {
        Result<ShopOrderExt2C> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryUserOrderDetail(userSession.getUnionid(),orderid));
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
     * 查用户会员卡积分
     * @param request
     * @param shopid
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryCardPointValue.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryCardPointValue(HttpServletRequest request,@RequestParam String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryCardPointValue(userSession,shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryCardPointValue is error: ",yex);

        }catch (Exception ex){
            logger.error("queryCardPointValue is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    /**
     * 发起支付接口
     * @param request
     * @param orderid
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/callPay.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> callPay(HttpServletRequest request,@RequestParam String orderid,@RequestParam(required = false) Integer payType)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        if(payType==null){
            payType = 0;
        }
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.payUserOrder(userSession,orderid,payType));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("callPay is error: ",yex);

        }catch (Exception ex){
            logger.error("callPay is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 发起充值接口
     * @param request
     * @param shopid
     * @param cashType 1-6的充值配置
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/cashRecharge.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> cashRecharge(HttpServletRequest request,@RequestParam String shopid,@RequestParam(required = false) Integer cashType)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.cashRecharge(userSession,shopid,cashType));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("cashRecharge is error: ",yex);

        }catch (Exception ex){
            logger.error("cashRecharge is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/calculatePrice.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BigDecimal> calculatePrice(HttpServletRequest request,@RequestParam BigDecimal price,@RequestParam Integer count)
    {
        Result<BigDecimal> result = new Result<>();
        result.setSuccess(true);
        if(count.intValue()<0){
            count = 0;
        }
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.calculatePrice(userSession.getUnionid(),price,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("calculatePrice is error: ",yex);

        }catch (Exception ex){
            logger.error("calculatePrice is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryShopStauts.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryShopStauts(HttpServletRequest request,@RequestParam String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryShopStauts(userSession.getUnionid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryShopStauts is error: ",yex);

        }catch (Exception ex){
            logger.error("queryShopStauts is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/querPreCreateOrderInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> querPreCreateOrderInfo(HttpServletRequest request,
                                              @RequestParam String shopid,@RequestParam(required = false) String enterCode)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.querPreCreateOrderInfo(userSession.getUnionid(),shopid,enterCode));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("querPreCreateOrderInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("querPreCreateOrderInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryUserBillBalance.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<BigDecimal> queryUserBillBalance(HttpServletRequest request,@RequestParam String shopid)
    {
        Result<BigDecimal> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryUserBillBalance(userSession.getUnionid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryUserBillBalance is error: ",yex);

        }catch (Exception ex){
            logger.error("queryUserBillBalance is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2C)
    @RequestMapping(value = {"/queryUserBilling.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryUserBilling(HttpServletRequest request,@RequestParam String shopid,@RequestParam String year,@RequestParam String month,
                                         @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidc);
            result.setResult(shoppingMall2CService.queryUserBilling(userSession.getUnionid(),shopid,year,month,pageIndex,count));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryUserBilling is error: ",yex);

        }catch (Exception ex){
            logger.error("queryUserBilling is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

}
