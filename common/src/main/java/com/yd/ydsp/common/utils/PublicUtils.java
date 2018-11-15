package com.yd.ydsp.common.utils;

import com.yd.ydsp.common.lang.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public class PublicUtils {
    /**
     * 手机号验证
     *
     * @param mobile
     * @return 验证通过返回true
     */
    public static boolean isMobile(String mobile) {
        if(StringUtil.isBlank(mobile)){
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(mobile);
        b = m.matches();
        return b;
    }



    /**
     * 电话号码验证
     *
     * @param phone
     * @return 验证通过返回true
     */
    public static boolean isPhone(String phone) {
        if(StringUtil.isBlank(phone)){
            return false;
        }
        Pattern p1 = null, p2 = null;
        Matcher m  = null;
        boolean b  = false;
        p1 = compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (phone.length() > 9) {
            m = p1.matcher(phone);
            b = m.matches();
        } else {
            m = p2.matcher(phone);
            b = m.matches();
        }
        return b;
    }
}
