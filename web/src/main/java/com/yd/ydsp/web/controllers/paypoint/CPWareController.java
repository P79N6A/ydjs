package com.yd.ydsp.web.controllers.paypoint;

import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.paypoint.WareItemVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
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
 * @author zengyixun
 * @date 18/1/9
 */

@Controller
@RequestMapping(value = "/paypoint/cp")
public class CPWareController {
    public static final Logger logger = LoggerFactory.getLogger(CPWareController.class);

    @Resource
    private WareService wareService;


    @RequestMapping(value = {"/public/getWareItem.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<WareItemVO> getWareItem(HttpServletRequest request, @RequestParam String itemid)
    {
        Result<WareItemVO> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareItem(itemid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareItem is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareItem is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @RequestMapping(value = {"/public/getWareItemByShopId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getWareItemByShopId(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareItemByShopId(shopid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareItemByShopId is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareItemByShopId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @RequestMapping(value = {"/public/getWareSku.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getWareSku(HttpServletRequest request, @RequestParam String shopid,@RequestParam String wareitemid,
                                   @RequestParam(required = false) Boolean is2c)
    {
        Result<List> result = new Result<>();
        if(is2c==null){
            is2c = true;
        }
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareSku(shopid,wareitemid,is2c));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareSku is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareSku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @RequestMapping(value = {"/public/getWareSkuBySkuid.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<WareSkuVO> getWareSkuBySkuid(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<WareSkuVO> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareSku(skuid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareSkuBySkuid is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareSkuBySkuid is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getWareInfoByShopId.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getWareInfoByShopId(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareService.getWareInfoByShopId(shopid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getWareInfoByShopId is error: ",yex);

        }catch (Exception ex){
            logger.error("getWareInfoByShopId is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addWareItem.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<String> addWareItem(HttpServletRequest request, @RequestParam String shopid,
                                      @RequestParam String name,@RequestParam Integer seq,
                                      @RequestParam(required = false)Boolean putaway)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            if(putaway==null){
                putaway = true;
            }
            result.setResult(wareService.addWareItem(userSession.getOpenid(),shopid,name,seq,putaway));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addWareItem is error: ",yex);

        }catch (Exception ex){
            logger.error("addWareItem is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyWareItem.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> modifyWareItem(HttpServletRequest request, @RequestParam String shopid,
                                          @RequestParam String name,@RequestParam String wareitemid,
                                          @RequestParam(required = false)Boolean putaway)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            if(putaway==null){
                putaway = true;
            }
            result.setResult(wareService.modifyWareItem(userSession.getOpenid(),shopid,wareitemid,name,putaway));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyWareItem is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyWareItem is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }


    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delWareItem.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> delWareItem(HttpServletRequest request, @RequestParam String shopid,@RequestParam String wareitemid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.delWareItem(userSession.getOpenid(),shopid,wareitemid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delWareItem is error: ",yex);

        }catch (Exception ex){
            logger.error("delWareItem is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/sortWareItem.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> sortWareItem(HttpServletRequest request, @RequestParam String shopid, @RequestBody List<WareItemVO> wareItemVOList)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
//            List<WareItemVO> wareItemVOList = JSON.parseArray(wareItemVos,WareItemVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.sortWareItem(userSession.getOpenid(),shopid,wareItemVOList));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("sortWareItem is error: ",yex);

        }catch (Exception ex){
            logger.error("sortWareItem is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addWareSku.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> addWareSku(HttpServletRequest request,@RequestBody WareSkuVO wareSkuVO)
    {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        try{
//            WareSkuVO wareSkuVO = JSON.parseObject(wareSku,WareSkuVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.addWareSku(userSession.getOpenid(),wareSkuVO));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addWareSku is error: ",yex);

        }catch (Exception ex){
            logger.error("addWareSku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/modifyWareSku.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> modifyWareSku(HttpServletRequest request,@RequestBody WareSkuVO wareSkuVO)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
//            WareSkuVO wareSkuVO = JSON.parseObject(wareSku,WareSkuVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.modifyWareSku(userSession.getOpenid(),wareSkuVO));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyWareSku is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyWareSku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/delWareSku.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> delWareSku(HttpServletRequest request, @RequestParam String shopid,@RequestParam String skuid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.delWareSku(userSession.getOpenid(),shopid,skuid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delWareSku is error: ",yex);

        }catch (Exception ex){
            logger.error("delWareSku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType=SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/sortWareSku.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> sortWareSku(HttpServletRequest request, @RequestParam String shopid,@RequestParam String wareitemid, @RequestBody List<WareSkuVO> wareSkuVOList)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
//            List<WareSkuVO> wareSkuVOList = JSON.parseArray(wareSkuVOs,WareSkuVO.class);
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.sortWareSku(userSession.getOpenid(),shopid,wareitemid,wareSkuVOList));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("sortWareSku is error: ",yex);

        }catch (Exception ex){
            logger.error("sortWareSku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getOssAuthentication.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getOssAuthentication(@RequestParam String shopid, @RequestParam(required = false) String dir, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareService.getOssAuthentication(userSession.getOpenid(),shopid,dir));
        }catch (Exception ex){
            logger.error("",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

}
