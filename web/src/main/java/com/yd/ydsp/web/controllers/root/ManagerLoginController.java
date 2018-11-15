package com.yd.ydsp.web.controllers.root;

import com.yd.ydsp.biz.cp.CpMonitorService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/17
 */

@Controller
@RequestMapping(value = "/yd/manager/login")
public class ManagerLoginController {

    public static final Logger logger = LoggerFactory.getLogger(ManagerLoginController.class);

    @Resource
    private UserinfoService userinfoService;

    @Resource
    private CpMonitorService cpMonitorService;


    /**
     * 设置超级管理员的手机验证码，3小时有效，3小时内在相关超级功能里带上正确的验证码，通行无阻
     * @param mobile
     * @param request
     * @return
     */
    @RequestMapping(value = {"/mobile/setMobileCheckCode.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setMobileCheckCode(@RequestParam String mobile, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        try{
            result.setSuccess(true);
            result.setResult(userinfoService.sendCheckCodeByRootUser(mobile));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }


    @RequestMapping(value = {"/getOssAuthentication.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Map> getOssAuthentication(@RequestParam String mobile, @RequestParam String code, @RequestParam(required = false) String dir, HttpServletRequest request)
    {
        Result<Map> result = new Result<>();
        try{
            result.setSuccess(true);
            result.setResult(cpMonitorService.getOssAuthentication(mobile,code,dir));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }

}
