package com.wso2.sample.filter;

import com.wso2.sample.filter.utils.BufferResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a sample implementation of how to pass additional parameters from SCImM2 request to the user core level using
 * a ThreadLocal. Also, how to send additional parameters from user core level to the SCIM2 response as HTTP headers.
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

        Map<String, String> additionalAttributes;
        String headersString;
        String[] headers;
        String value;

        if (getUserThreadLocal() != null) {
            getUserThreadLocal().clear();
            log.debug("SCIM2 filter cleared the ThreadLocal");
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        BufferResponseWrapper wrapper = null;

        // In flow
        if (filterConfig.getInitParameter(REQ_HEADERS_LIST) != null && !filterConfig.getInitParameter(REQ_HEADERS_LIST).trim().isEmpty()
                && filterConfig.getInitParameter(HTTP_METHODS) != null && !filterConfig.getInitParameter(HTTP_METHODS).trim().isEmpty()) {

            // If the request's HTTP method is included in the defined set, proceed further.
            String httpMethodsString = filterConfig.getInitParameter(HTTP_METHODS);
            String[] httpMethods = httpMethodsString.split(",");
            boolean isHttpMethodMatched = false;
            for (String httpMethod : httpMethods) {
                if (httpMethod.equalsIgnoreCase(httpServletRequest.getMethod())) {
                    isHttpMethodMatched = true;
                    break;
                }
            }

            // If the defined HTTP headers are there in the request, set them to the ThreadLocal.
            if (isHttpMethodMatched) {
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
                setUserThreadLocal(additionalAttributes);
                wrapper = new BufferResponseWrapper(httpServletResponse);
            }
        }


        // Continue the filter chain and the WSO2 internals. (user-core etc.)
        if (wrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, wrapper);
        }

        // Out flow
        if (getUserThreadLocal() != null && !getUserThreadLocal().isEmpty() && filterConfig.getInitParameter(RES_HEADERS_LIST) != null
                && !filterConfig.getInitParameter(RES_HEADERS_LIST).trim().isEmpty()) {

            servletResponse.getOutputStream().write(wrapper.getWrapperBytes());

            // Check if thread local has any defined response headers. If so, set them as HTTP headers in the response.
            additionalAttributes = getUserThreadLocal();
            headersString = filterConfig.getInitParameter(RES_HEADERS_LIST);
            headers = headersString.split(",");
            for (String header : headers) {
                header = header.trim();
                value = additionalAttributes.get(header);
                if (value != null) {
                    httpServletResponse.setHeader(header, value);
                }
            }
        }

        if (getUserThreadLocal() != null) {
            getUserThreadLocal().clear();
            log.debug("SCIM2 filter cleared the ThreadLocal");
        }
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
