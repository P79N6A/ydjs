package com.yd.ydsp.web.controllers.paypoint;


import com.yd.ydsp.biz.cp.CpChannelService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.openshop.YdCpChannelVO;
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

@Controller
@RequestMapping(value = "/paypoint/cp")
public class CpChannelController {
    public static final Logger logger = LoggerFactory.getLogger(CpChannelController.class);

    @Resource
    CpChannelService cpChannelService;

    /**
     * 新增渠道
     * @return
     */
    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/channel/addChannel.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<String> addChannel(HttpServletRequest request, @RequestBody YdCpChannelVO cpChannelVO)
    {
        Result<String> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.addChannel(userSession.getOpenid(),cpChannelVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("addChannel is error: ",yex);

        }catch (Exception ex){
            logger.error("addChannel is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/channel/modifyChannel.do"}, method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> modifyChannel(HttpServletRequest request, @RequestBody YdCpChannelVO cpChannelVO)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.modifyChannel(userSession.getOpenid(),cpChannelVO));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("modifyChannel is error: ",yex);

        }catch (Exception ex){
            logger.error("modifyChannel is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/channel/delChannel.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> delChannel(HttpServletRequest request, @RequestParam String channelid)
    {
        Result<Boolean> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.disableChannel(userSession.getOpenid(),channelid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("delChannel is error: ",yex);

        }catch (Exception ex){
            logger.error("delChannel is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/channel/queryChannelInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Object> queryChannelInfo(HttpServletRequest request, @RequestParam String channelid)
    {
        Result<Object> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.queryChannelInfo(userSession.getOpenid(),channelid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("queryChannelInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("queryChannelInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/channel/queryChannelList.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> queryChannelList(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.queryChannelList(userSession.getOpenid(),shopid));
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

    @PageSource(sourceType= SourceEnum.WEIXIN2B)
    @RequestMapping(value = {"/getDeliveryConfigInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<List> getDeliveryConfigInfo(HttpServletRequest request, @RequestParam String shopid)
    {
        Result<List> result = new Result<>();
        try {
            UserSession userSession = (UserSession)  request.getAttribute(CookieConstantTable.yidb);
            result.setSuccess(true);
            result.setResult(cpChannelService.getDeliveryConfigInfo(userSession.getOpenid(),shopid));
        }catch (YdException yex){
            result.setSuccess(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("getDeliveryConfigInfo is error: ",yex);

        }catch (Exception ex){
            logger.error("getDeliveryConfigInfo is error: ",ex);
            result.setMsgInfo(ex.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


}
