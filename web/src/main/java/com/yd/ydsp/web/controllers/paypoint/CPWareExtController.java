package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.cp.WareExtService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.paypoint.WareSkuPicVO;
import com.yd.ydsp.client.domian.paypoint.YdCpParameterInfoVO;
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
import java.util.Set;

@Controller
@RequestMapping(value = "/paypoint/cp")
public class CPWareExtController {

    public static final Logger logger = LoggerFactory.getLogger(CPWareExtController.class);

    @Resource
    private WareExtService wareExtService;

    /**
     * 之所以注销掉，是因为直接在增加与修改参数的时候，就应该把这些加好了
     * @param request
     * @param parameterid
     * @return
     */
//    @PageSource(sourceType= SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/addWareParamInfo.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<String> addWareParamInfo(HttpServletRequest request, @RequestBody YdCpParameterInfoVO cpParameterInfoVO)
//    {
//        Result<String> result = new Result<>();
//        result.setSuccess(true);
//        try{
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(wareExtService.addWareParamInfo(userSession.getOpenid(),cpParameterInfoVO));
//
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("addWareParamInfo is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("addWareParamInfo is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//
//        return result;
//    }
//
//    @PageSource(sourceType= SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/addWareParamInfoList.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<Set> addWareParamInfoList(HttpServletRequest request, @RequestBody Set<YdCpParameterInfoVO> cpParameterInfoVOs)
//    {
//        Result<Set> result = new Result<>();
//        result.setSuccess(true);
//        try{
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setResult(wareExtService.addWareParamInfos(userSession.getOpenid(),cpParameterInfoVOs));
//
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("addWareParamInfoList is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("addWareParamInfoList is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//
//        return result;
//    }


    @RequestMapping(value = {"/public/queryWareParamInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryWareParamInfo(HttpServletRequest request, @RequestParam String parameterid)
    {
        Result<Object> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareExtService.queryWareParamInfo(parameterid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryWareParamInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("queryWareParamInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @RequestMapping(value = {"/public/queryWareParamInfoList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryWareParamInfoList(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareExtService.queryWareParamInfoList(skuid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryWareParamInfoList is error: ",yex);

        }catch (Exception ex){
            logger.error("queryWareParamInfoList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * ------------------店铺商品的单位管理--------------------
     */

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addWareUnitName.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> addWareUnitName(HttpServletRequest request, @RequestParam String shopid, @RequestParam String unitName)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareExtService.addWareUnitName(userSession.getOpenid(),shopid,unitName));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addWareUnitName is error: ",yex);

        }catch (Exception ex){
            logger.error("addWareUnitName is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/queryUnitName.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> queryUnitName(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<Map> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareExtService.queryUnitName(userSession.getOpenid(),shopid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryUnitName is error: ",yex);

        }catch (Exception ex){
            logger.error("queryUnitName is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/deleteUnitName.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> deleteUnitName(HttpServletRequest request, @RequestParam String shopid, @RequestParam String unitName)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareExtService.deleteUnitName(userSession.getOpenid(),shopid,unitName));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("deleteUnitName is error: ",yex);

        }catch (Exception ex){
            logger.error("deleteUnitName is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * ------------------店铺商品的图文详情管理--------------------
     */

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updatePicDetails.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updatePicDetails(HttpServletRequest request, @RequestBody List<WareSkuPicVO> wareSkuPicVOS)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareExtService.updatePicDetails(userSession.getOpenid(),wareSkuPicVOS));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updatePicDetails is error: ",yex);

        }catch (Exception ex){
            logger.error("updatePicDetails is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/deleteAllPicDetails.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> deleteAllPicDetails(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<Boolean> result = new Result<>();
        result.setSuccess(true);
        try{
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(wareExtService.deleteAllPicDetails(userSession.getOpenid(),skuid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("deleteAllPicDetails is error: ",yex);

        }catch (Exception ex){
            logger.error("deleteAllPicDetails is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    @RequestMapping(value = {"/public/queryPicDetails.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryPicDetails(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<List> result = new Result<>();
        result.setSuccess(true);
        try{
            result.setResult(wareExtService.queryPicDetails(skuid));

        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryPicDetails is error: ",yex);

        }catch (Exception ex){
            logger.error("queryPicDetails is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

}
