package com.yd.ydsp.web.controllers.paypoint;

import com.yd.ydsp.biz.cp.AgentApplyCacheService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.web.auth.cookie.CookieConstantTable;
import com.yd.ydsp.web.auth.cookie.CookieUtils;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;

/**
 * Created by zengyixun on 17/10/19.
 */
@Controller
@RequestMapping(value = "/paypoint/agent")
public class AgentInfoController {


    @Resource
    AgentApplyCacheService agentApplyCacheService;
    @Resource
    UserinfoService userinfoService;

    public static final Logger logger = LoggerFactory.getLogger(com.yd.ydsp.web.controllers.paypoint.CPApplyFlowController.class);


    /**
     * 最早期的扫码点餐商家信息收集页面的接口
     * @param request
     * @param name
     * @param companyname
     * @param phone
     * @param address
     * @return
     */
    @RequestMapping(value = {"/apply/add.do"}, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Result<Boolean> newApply(HttpServletRequest request, @RequestParam String name,@RequestParam(required = false) String companyname,@RequestParam String phone,
                                    @RequestParam(required = false) String address,@RequestParam(required = false)String applyType,@RequestParam(required = false)String description)
    {
        Result<Boolean> result = new Result<>();
        try {
            if(StringUtil.isEmpty(applyType)){
                applyType = "代理商";
            }
            if(StringUtil.isNotEmpty(description)){
                if(description.length()>99){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"备注信息太长！");
                }
            }
            YdPaypointAgentapplyinfo paypointAgentapplyinfo = new YdPaypointAgentapplyinfo();
            paypointAgentapplyinfo.setName(name);
            paypointAgentapplyinfo.setCompanyname(companyname);
            paypointAgentapplyinfo.setPhone(phone);
            paypointAgentapplyinfo.setAddress(address);
            paypointAgentapplyinfo.setCreateDate(Date.from(Instant.now()));
            paypointAgentapplyinfo.setModifyDate(Date.from(Instant.now()));
            paypointAgentapplyinfo.setApplyType(applyType);
            paypointAgentapplyinfo.setDescription(description);
            String ipStr = CookieUtils.getIpAddress(request);
            paypointAgentapplyinfo.setIp(ipStr);
            agentApplyCacheService.setAgentipinfo(ipStr);
            boolean isSuccess = agentApplyCacheService.addAgentApplyInfo(ipStr,paypointAgentapplyinfo);
            result.setResult(isSuccess);
            result.setSuccess(isSuccess);
            logger.info("ipStr is :"+ipStr);
        }catch (YdException yex){
            result.setResult(false);
            result.setResultCode(yex.getErrorCode());
            result.setMsgInfo(yex.getMessage());
            logger.error("agentNewApply is error: ",yex);
        }catch (Exception ex){
            logger.error("agentNewApply is error:",ex);
            result.setResult(false);
            result.setMsgInfo("保存失败，请稍候再试！");
        }
        return result;
    }

}
