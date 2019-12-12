package com.wso2.sample.filter;

import javax.servlet.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class CustomFilter implements Filter {

    private ServletContext context = null;
    private static final ThreadLocal<Map> userThreadLocal = new ThreadLocal();
    private static final String REQ_HEADERS_LIST = "reqHeadersList";
    public static final String RES_HEADERS_LIST = "resHeadersList";

    @Override
    public void init(FilterConfig filterConfig) {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        Map<String, String> additionalAttributes = null;
        String headersString;
        String[] headers;
        String value;

        // Out flow
        if(getUserThreadLocal() != null && !getUserThreadLocal().isEmpty() &&
                context.getInitParameter(RES_HEADERS_LIST) != null && !context.getInitParameter(RES_HEADERS_LIST).trim().isEmpty()) {
            additionalAttributes = getUserThreadLocal();
            headersString = context.getInitParameter(RES_HEADERS_LIST);
            headers = headersString.split(",");
            for (String header : headers) {
                value = additionalAttributes.get(header);
                if (value != null) {
                    // Well, now only I realized ServletResponse class doesn't provide a method to set additional HTTP
                    // headers to the response. Move this logic to a Valve.
                }
            }
            getUserThreadLocal().clear();
            return;
        }

        // In flow
        if(context.getInitParameter(REQ_HEADERS_LIST) != null && !context.getInitParameter(REQ_HEADERS_LIST).trim().isEmpty()) {
            additionalAttributes = new HashMap<>();
            headersString = context.getInitParameter(REQ_HEADERS_LIST);
            headers = headersString.split(",");
            for (String header : headers) {
                value = servletRequest.getParameter(header);
                if(value != null) {
                    additionalAttributes.put(header, value);
                }
            }
        }
        setUserThreadLocal(additionalAttributes);
    }

    public static Map<String, String> getUserThreadLocal() {
        return userThreadLocal.get();
    }

    public static void setUserThreadLocal(Map<String, String> additionalAttributes) {
        userThreadLocal.set(additionalAttributes);
    }

    @Override
    public void destroy() {}
}
