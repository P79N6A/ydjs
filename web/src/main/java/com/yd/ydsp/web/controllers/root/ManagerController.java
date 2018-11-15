package com.yd.ydsp.web.controllers.root;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.CpMonitorService;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.cp.model.CPMonitorVO;
import com.yd.ydsp.common.enums.RoleTypeEnum;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.web.auth.passport.AuthPassport;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zengyixun
 * @date 17/12/17
 */

@Controller
@RequestMapping(value = "/yd/manager")
public class ManagerController {
    public static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private CpMonitorService cpMonitorService;


    /**
     *
     * @param cpMonitorInfo
     * @param request
     * @return
     */
    @PageSource(sourceType= SourceEnum.ROOTMANAGER)
    @AuthPassport(roleType = RoleTypeEnum.ROOT)
    @RequestMapping(value = {"/setShopMonitorInfo.do"}, method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Result<Boolean> setShopMonitorInfo(@RequestBody String cpMonitorInfo, HttpServletRequest request)
    {
        Result<Boolean> result = new Result<>();
        try{
            result.setSuccess(true);
            logger.info("setShopMonitorInfo input data is: "+cpMonitorInfo);
            CPMonitorVO monitorVO = JSON.parseObject(cpMonitorInfo, CPMonitorVO.class);
            result.setResult(cpMonitorService.setShopMonitorInfo(monitorVO));
        }catch (Exception ex){
            logger.error("",ex);
            result.setSuccess(false);
        }
        return result;
    }
}
