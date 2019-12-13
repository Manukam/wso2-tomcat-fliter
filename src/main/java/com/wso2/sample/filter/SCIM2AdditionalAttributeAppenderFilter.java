package com.wso2.sample.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class SCIM2AdditionalAttributeAppenderFilter implements Filter {

    private FilterConfig filterConfig = null;
    private static final ThreadLocal<Map> userThreadLocal = new ThreadLocal();
    private static final String REQ_HEADERS_LIST = "reqHeadersList";
    public static final String RES_HEADERS_LIST = "resHeadersList";
    private static final String HTTP_METHODS = "HTTPMethods";

    private static final Log log = LogFactory.getLog(SCIM2AdditionalAttributeAppenderFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        Map<String, String> additionalAttributes = null;
        String headersString;
        String[] headers;
        String value;

        // Out flow
        if (getUserThreadLocal() != null && !getUserThreadLocal().isEmpty() &&
                filterConfig.getInitParameter(RES_HEADERS_LIST) != null && !filterConfig.getInitParameter(RES_HEADERS_LIST).trim().isEmpty()) {
            additionalAttributes = getUserThreadLocal();
            headersString = filterConfig.getInitParameter(RES_HEADERS_LIST);
            headers = headersString.split(",");
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            for (String header : headers) {
                header = header.trim();
                value = additionalAttributes.get(header);
                if (value != null) {
                    httpServletResponse.setHeader(header, value);
                }
            }
            getUserThreadLocal().clear();
            filterChain.doFilter(servletRequest, httpServletResponse);
            return;
        }

        // In flow
        if (filterConfig.getInitParameter(REQ_HEADERS_LIST) != null && !filterConfig.getInitParameter(REQ_HEADERS_LIST).trim().isEmpty()
                && filterConfig.getInitParameter(HTTP_METHODS) != null && !filterConfig.getInitParameter(HTTP_METHODS).trim().isEmpty()) {

            // If the request's HTTP method is not included in the defined set, return from the filter.
            String httpMethodsString = filterConfig.getInitParameter(HTTP_METHODS);
            String[] httpMethods = httpMethodsString.split(",");
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            for (String httpMethod : httpMethods) {
                if (httpMethod.equalsIgnoreCase(httpServletRequest.getMethod())) {
                    break;
                }
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            // If the defined HTTP headers are there in the request, set them to the ThreadLocal.
            additionalAttributes = new HashMap<>();
            headersString = filterConfig.getInitParameter(REQ_HEADERS_LIST);
            headers = headersString.split(",");
            for (String header : headers) {
                header = header.trim();
                value = httpServletRequest.getHeader(header);
                if (value != null) {
                    additionalAttributes.put(header, value);
                }
            }
        }
        setUserThreadLocal(additionalAttributes);
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    public static Map<String, String> getUserThreadLocal() {
        return userThreadLocal.get();
    }

    public static void setUserThreadLocal(Map<String, String> additionalAttributes) {
        userThreadLocal.set(additionalAttributes);
    }

    @Override
    public void destroy() {
    }
}
