package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.cp.ShopIndexPageService;
import com.yd.ydsp.biz.openshop.ShopPageService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.openshop.components.PageInfoVO;
import com.yd.ydsp.client.domian.openshop.components.ShopIndexPageVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/paypoint/cp")
public class ShopPageInfoController {
    public static final Logger logger = LoggerFactory.getLogger(ShopPageInfoController.class);

    @Resource
    private ShopIndexPageService shopIndexPageService;
    @Resource
    private ShopPageService shopPageService;


//    @PageSource(sourceType= SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/updateShopIndexPageInfo.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<String> updateShopIndexPageInfo(HttpServletRequest request, @RequestBody ShopIndexPageVO shopIndexPageVO)
//    {
//        Result<String> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(shopIndexPageService.updateShopIndexPageInfo(userSession.getOpenid(),shopIndexPageVO));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("updateShopIndexPageInfo is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("updateShopIndexPageInfo is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

//    @RequestMapping(value = {"/public/getShopIndexPageData2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public Result<Object> getShopIndexPageData2C(HttpServletRequest request, @RequestParam String shopid)
//    {
//        Result<Object> result = new Result<>();
//        result.setSuccess(true);
//        try {
//            result.setResult(shopIndexPageService.getShopIndexPageData2C(shopid));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("getShopIndexPageData2C is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("getShopIndexPageData2C is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updatePageInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> updatePageInfo(HttpServletRequest request, @RequestBody PageInfoVO pageInfoVO)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopPageService.updatePageInfo(userSession.getOpenid(),pageInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updatePageInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("updatePageInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/setShopIndexPage.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> setShopIndexPage(HttpServletRequest request, @RequestParam String pageid,@RequestParam String shopid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopPageService.setShopIndexPage(userSession.getOpenid(),shopid,pageid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("setShopIndexPage is error: ",yex);

        }catch (Exception ex){
            logger.error("setShopIndexPage is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/releasePageInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> releasePageInfo(HttpServletRequest request, @RequestParam String pageid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(shopPageService.releasePageInfo(userSession.getOpenid(),pageid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("releasePageInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("releasePageInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryPageInfoByPageId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryPageInfoByPageId(HttpServletRequest request, @RequestParam String pageid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopPageService.queryPageInfo(pageid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryPageInfoByPageId is error: ",yex);

        }catch (Exception ex){
            logger.error("queryPageInfoByPageId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/queryPageInfoByShopid.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryPageInfoByShopid(HttpServletRequest request, @RequestParam String shopid,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam Integer pageIndex, @RequestParam Integer count)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try {
            if(status==null){
                result.setResult(shopPageService.queryPageInfo(shopid,pageIndex,count));
            }else {
                result.setResult(shopPageService.queryPageInfo(shopid,status,pageIndex,count));
            }
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryPageInfoByShopid is error: ",yex);

        }catch (Exception ex){
            logger.error("queryPageInfoByShopid is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/getPageInfo2C.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getPageInfo2C(HttpServletRequest request, @RequestParam String pageid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopPageService.getPageInfo2C(pageid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getPageInfo2C is error: ",yex);

        }catch (Exception ex){
            logger.error("getPageInfo2C is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/getPageInfo2BReview.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> getPageInfo2BReview(HttpServletRequest request, @RequestParam String pageid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try {
            result.setResult(shopPageService.getPageInfo2BReview(pageid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getPageInfo2BReview is error: ",yex);

        }catch (Exception ex){
            logger.error("getPageInfo2BReview is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }



    @RequestMapping(value = {"/public/page/{pageid}"}, method = {RequestMethod.GET})
    public String authShop(@PathVariable("pageid") String pageid,
                           Model model) {
        String resultUrl = "shoppage/page";
        try{
            model.addAttribute("pageName","页面ID为："+pageid);
        }catch (YdException yex){
            resultUrl = "shoppage/page_error";
            model.addAttribute("errorInfo",yex.getMessage());
            logger.error("authShop is error: ",yex);
        } catch (Exception e) {
            resultUrl = "shoppage/page_error";
            model.addAttribute("errorInfo","创建商城失败，请联系服务商！");
            logger.error("authShop is error: ",e);
        }
        return resultUrl;
    }
}
