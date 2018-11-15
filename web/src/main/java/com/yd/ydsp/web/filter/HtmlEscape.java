package com.yd.ydsp.web.filter;

import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.security.util.SecurityUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by fachao.zfc on 2016/2/26.
 */
public class HtmlEscape implements Filter {
    public static final Logger logger = LoggerFactory.getLogger(HtmlEscape.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化html反转义过滤器...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //日志记录
        logger.info("包装原始ServletResponse，sourceType:" + servletRequest.getClass() + ",targetType：" + HttpResponseWrapper.class);

        //包装处理
        HttpResponseWrapper httpResponseWrapper = new HttpResponseWrapper((HttpServletResponse) servletResponse);

        //过滤器
        filterChain.doFilter(servletRequest, httpResponseWrapper);

        //处理response的内容
        logger.info("处理网页内容...");
        byte[] responseData = httpResponseWrapper.getResponseData();
        String dataStr = new String(responseData);
        logger.info("原始内容为：" + dataStr);
        PrintWriter writer = servletResponse.getWriter();

        //转义字符串
        String unescapeHtml = SecurityUtil.escapeHtml(dataStr);
        if(StringUtil.isNotEmpty(dataStr)){
            if(StringUtil.equals(dataStr.substring(0, 1),"{")||StringUtil.equals(dataStr.substring(0, 1),"[")){
                unescapeHtml = SecurityUtil.escapeJson(dataStr);
            }
        }
                //StringEscapeUtils.unescapeHtml(dataStr);

        writer.write(unescapeHtml);
        writer.flush();

        //记录日志
        logger.info("response处理结束,html反转义过滤器！");
    }

    @Override
    public void destroy() {
        logger.info("结束！");
    }
}
