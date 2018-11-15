package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.cp.SpecConfigService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecConfigInfoVO;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecInfoVO;
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
import java.util.Set;

@Controller
@RequestMapping(value = "/paypoint/cp")
public class CpSpecConfigController {
    public static final Logger logger = LoggerFactory.getLogger(CpSpecConfigController.class);

    @Resource
    private SpecConfigService specConfigService;


    /**
     * 新增规格基本信息
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/addSpecInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> addSpecInfo(HttpServletRequest request, @RequestBody YdCpSpecInfoVO cpSpecInfoVO)
    {
        Result<String> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.addSpecInfo(userSession.getOpenid(),cpSpecInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addSpecInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("addSpecInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 更新规格
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/updateSpecInfo.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateSpecInfo(HttpServletRequest request, @RequestBody YdCpSpecInfoVO cpSpecInfoVO)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.updateSpecInfo(userSession.getOpenid(),cpSpecInfoVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("updateSpecInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("updateSpecInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 删除规格
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/deleteSpecInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> deleteSpecInfo(HttpServletRequest request, @RequestParam String specid)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.deleteSpecInfo(userSession.getOpenid(),specid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("deleteSpecInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("deleteSpecInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 查询一个指定规格id的规格信息
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/querySpecInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> querySpecInfo(HttpServletRequest request, @RequestParam String specid)
    {
        Result<Object> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.querySpecInfo(userSession.getOpenid(),specid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("querySpecInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("querySpecInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 查询一个指定店铺下的规格列表
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/querySpecNameList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> querySpecNameList(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.querySpecNameList(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("querySpecNameList is error: ",yex);

        }catch (Exception ex){
            logger.error("querySpecNameList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * --------------------------------以下开始对一个商品进行具体的规格组合及价格设置---------------------------------
     */

    /**
     * 增加规格配置信息，如果一个配置已经存在，就进行更新
     * 此接口删除，原因是已经在商品增加与修改中一起进行规格配置的数据提交了
     * @return
     */
//    @PageSource(sourceType= SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/addSpecConfigInfo.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<Boolean> addSpecConfigInfo(HttpServletRequest request, @RequestBody Set<YdCpSpecConfigInfoVO> cpSpecConfigInfoVOS)
//    {
//        Result<Boolean> result = new Result<>();
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setSuccess(true);
//            result.setResult(specConfigService.addSpecConfigInfo(userSession.getOpenid(),cpSpecConfigInfoVOS));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("addSpecConfigInfo is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("addSpecConfigInfo is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

//    @PageSource(sourceType= SourceEnum.WEIXIN2B)
//    @RequestMapping(value = {"/deleteSpecConfigInfo.do"}, method = RequestMethod.POST)
//    @ResponseBody
//    public Result<Boolean> deleteSpecConfigInfo(HttpServletRequest request, @RequestBody Set<YdCpSpecConfigInfoVO> cpSpecConfigInfoVOS)
//    {
//        Result<Boolean> result = new Result<>();
//        try {
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
//            result.setSuccess(true);
//            result.setResult(specConfigService.deleteSpecConfigInfo(userSession.getOpenid(),cpSpecConfigInfoVOS));
//        }catch (YdException yex){
//            result.setSuccess(false);
//            result.setResultCode(yex.getErrorCode());
//            result.setMsgInfo(yex.getMessage());
//            logger.error("deleteSpecConfigInfo is error: ",yex);
//
//        }catch (Exception ex){
//            logger.error("deleteSpecConfigInfo is error: ",ex);
//            result.setMsgInfo(ex.getMessage());
//            result.setSuccess(false);
//        }
//        return result;
//    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/querySpecConfigInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Set> querySpecConfigInfo(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<Set> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(specConfigService.querySpecConfigInfo(userSession.getOpenid(),skuid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("querySpecConfigInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("querySpecConfigInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @RequestMapping(value = {"/public/getMainSpecNameList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Set> getMainSpecNameList(HttpServletRequest request, @RequestParam String skuid)
    {
        Result<Set> result = new Result<>();
        try {
            result.setSuccess(true);
            result.setResult(specConfigService.getMainSpecNameList(skuid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getMainSpecNameList is error: ",yex);

        }catch (Exception ex){
            logger.error("getMainSpecNameList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/getChildSpecNameList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Set> getChildSpecNameList(HttpServletRequest request, @RequestParam String skuid, @RequestParam String mainSpecName)
    {
        Result<Set> result = new Result<>();
        try {
            result.setSuccess(true);
            result.setResult(specConfigService.getChildSpecNameList(skuid,mainSpecName));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getChildSpecNameList is error: ",yex);

        }catch (Exception ex){
            logger.error("getChildSpecNameList is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    @RequestMapping(value = {"/public/getPriceBySku.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getPriceBySku(HttpServletRequest request, @RequestParam String skuid, @RequestParam(required = false) String mainSpecName,
                                     @RequestParam(required = false) String childSpecName)
    {
        Result<Map> result = new Result<>();
        try {
            result.setSuccess(true);
            result.setResult(specConfigService.getPriceBySku(skuid,mainSpecName,childSpecName));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getPriceBySku is error: ",yex);

        }catch (Exception ex){
            logger.error("getPriceBySku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = {"/public/getInfoBySku.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getInfoBySku(HttpServletRequest request, @RequestParam String skuid, @RequestParam(required = false) String mainSpecName,
                                           @RequestParam(required = false) String childSpecName)
    {
        Result<Map> result = new Result<>();
        try {
            result.setSuccess(true);
//            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setResult(specConfigService.getInfoBySku(skuid,mainSpecName,childSpecName));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getInfoBySku is error: ",yex);

        }catch (Exception ex){
            logger.error("getInfoBySku is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


}
