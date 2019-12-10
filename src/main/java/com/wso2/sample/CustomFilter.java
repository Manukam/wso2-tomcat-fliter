package com.wso2.sample;

import javax.servlet.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class CustomFilter implements Filter {

    private ServletContext context = null;
    public static final ThreadLocal userThreadLocal = new ThreadLocal();
    private static final String REQ_HEADERS_LIST = "reqHeadersList";
    public static final String RES_RESPONSE_LIST = "resHeadersList";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Map <String, String> additionalReqAttributes = new HashMap<>();

        if(context.getInitParameter(REQ_HEADERS_LIST) != null && !context.getInitParameter(REQ_HEADERS_LIST).trim().isEmpty()) {
            String headersString = context.getInitParameter(REQ_HEADERS_LIST);
            String[] headers = headersString.split(",");
            for (String header : headers) {
                String value = servletRequest.getParameter(header);
                if(value != null) {
                    additionalReqAttributes.put(header, value);
                }
            }
        }

        userThreadLocal.set(additionalReqAttributes);
    }

    @Override
    public void destroy() {

    }
}
