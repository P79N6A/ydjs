package com.yd.ydsp.web.auth.cookie;

import com.yd.ydsp.common.lang.StringUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zengyixun on 17/5/28.
 */
public class CookieUtils {
    /**
     * 添加一个新Cookie
     *
     * @author zifangsky
     * @param response
     *            HttpServletResponse
     * @param cookie
     *            新cookie
     *
     * @return null
     */
    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
            response.addCookie(cookie);
        }
    }

    /**
     * 添加一个新Cookie
     *
     * @author zifangsky
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            cookie名称
     * @param cookieValue
     *            cookie值
     * @param domain
     *            cookie所属的子域
     * @param httpOnly
     *            是否将cookie设置成HttpOnly
     * @param maxAge
     *            设置cookie的最大生存期
     * @param path
     *            设置cookie路径
     * @param secure
     *            是否只允许HTTPS访问
     *
     * @return null
     */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, String domain,
                                 boolean httpOnly, int maxAge, String path, boolean secure) throws UnsupportedEncodingException {
        if (cookieName != null && !cookieName.equals("")) {
            if (cookieValue == null) {
                cookieValue = "";
            }

            Cookie newCookie = new Cookie(cookieName, URLEncoder.encode(cookieValue,"utf-8"));
            if (StringUtil.isNotEmpty(domain)) {
                newCookie.setDomain(domain);
            }

            newCookie.setHttpOnly(httpOnly);

            if (maxAge > 0) {
                newCookie.setMaxAge(maxAge);
            }

            if (StringUtil.isEmpty(path)) {
                newCookie.setPath("/");
            }
            else {
                newCookie.setPath(path);
            }

            newCookie.setSecure(secure);

            addCookie(response, newCookie);
        }
    }

    /**
     * 添加一个新Cookie
     *
     * @author zifangsky
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            cookie名称
     * @param cookieValue
     *            cookie值
     * @param domain
     *            cookie所属的子域
     *
     * @return null
     */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, String domain) throws UnsupportedEncodingException {
        addCookie(response, cookieName, cookieValue, domain, true, CookieConstantTable.COOKIE_MAX_AGE, "/", false);
    }

    /**
     * 根据Cookie名获取对应的Cookie
     *
     * @author zifangsky
     * @param request
     *            HttpServletRequest
     * @param cookieName
     *            cookie名称
     *
     * @return 对应cookie，如果不存在则返回null
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookieName == null || cookieName.equals("")) {
            return null;
        }

        for (Cookie c : cookies) {
            if (StringUtil.equals(c.getName(),cookieName)) {
                return (Cookie) c;
            }
        }
        return null;
    }

    /**
     * 根据Cookie名获取对应的Cookie值
     *
     * @author zifangsky
     * @param request
     *            HttpServletRequest
     * @param cookieName
     *            cookie名称
     *
     * @return 对应cookie值，如果不存在则返回null
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }
        else {
            return cookie.getValue();
        }
    }

    /**
     * 删除指定Cookie
     *
     * @author zifangsky
     * @param response
     *            HttpServletResponse
     * @param cookie
     *            待删除cookie
     */
    public static void delCookie(HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setValue(null);

            response.addCookie(cookie);
        }
    }

    /**
     * 根据cookie名删除指定的cookie
     *
     * @author zifangsky
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            待删除cookie名
     */
    public static void delCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie c = getCookie(request, cookieName);
        if (c != null && c.getName().equals(cookieName)) {
            delCookie(response, c);
        }
    }

    /**
     * 根据cookie名修改指定的cookie
     *
     * @author zifangsky
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            cookie名
     * @param cookieValue
     *            修改之后的cookie值
     * @param domain
     *            修改之后的domain值
     */
    public static void editCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                  String cookieValue,String domain) throws UnsupportedEncodingException {
        Cookie c = getCookie(request, cookieName);
        if (c != null && cookieName != null && !cookieName.equals("") && c.getName().equals(cookieName)) {
            if(StringUtil.isEmpty(domain)) {
                addCookie(response, cookieName, cookieValue, c.getDomain());
            }else{
                addCookie(response, cookieName, cookieValue, domain);
            }
        }
    }

    /**
     * 在权限控制中放过特定ip地址（比如微信服务器IP List）请求的权限控制
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0){
            return null;
        }
        String[] ips = ip.split(",");
        for (String ipStr: ips) {
            ip = trimSpaces(ipStr);
            if(isIp(ip)){
                return ip;
            }

        }
        return ip;
    }


    /**
     * 去掉IP字符串前后所有的空格
     * @param IP
     * @return
     */
    public static String trimSpaces(String IP){
        while(IP.startsWith(" ")){
            IP= IP.substring(1,IP.length()).trim();
        }
        while(IP.endsWith(" ")){
            IP= IP.substring(0,IP.length()-1).trim();
        }
        return IP;
    }


    /**
     * 判断是否是一个IP
     * @param IP
     * @return
     */
    public static boolean isIp(String IP){
        boolean isIp = false;
        if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){
            String s[] = IP.split("\\.");
            if(Integer.parseInt(s[0])<255) {
                if (Integer.parseInt(s[1]) < 255) {
                    if (Integer.parseInt(s[2]) < 255) {
                        if (Integer.parseInt(s[3]) < 255) {
                            isIp = true;
                        }
                    }
                }
            }
        }
        return isIp;
    }

}
