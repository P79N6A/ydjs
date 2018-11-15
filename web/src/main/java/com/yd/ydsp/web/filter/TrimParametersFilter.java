package com.yd.ydsp.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TrimParametersFilter implements Filter {
    public void init(FilterConfig arg0) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain fc) throws IOException, ServletException {
        if (request instanceof TrimParametersRequestWrapper) {
            fc.doFilter(request, response);
            return;
        }
        if (!(request instanceof HttpServletRequest)
                || !(response instanceof HttpServletResponse)) {
            throw new ServletException(
                    "TrimParametersFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        TrimParametersRequestWrapper trimRequest = new TrimParametersRequestWrapper(
                httpRequest);
        fc.doFilter(trimRequest, response);
    }

}
