package com.yd.ydsp.web.controllers.weixin;

import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.web.auth.passport.PageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zengyixun on 17/5/26.
 */

@PageSource(sourceType = SourceEnum.WEIXIN2B)
@Controller
@RequestMapping(value = "/weixin/my")
public class my {

    @RequestMapping(value = {"/bindMobile"}, method = RequestMethod.GET)
    public String mobile(HttpServletRequest request)
    {
        return "weixin/my/mobile";
    }

}
