# wso2-tomcat-fliter
Tomcat filter to add additional attributes as Thread Local variables.

## Purpose
To preserve defined set of HTTP headers of a SCIM request in a thread local variable, so that those information
are available in the User Store manger level. 
And to set response attributes to a thread local in the User Store Manager level, so that those additional response
values can be injected in to the outgoing HTTP request's headers.

## Instructions
1. Build and place the jar inside the ```<IS_HOME>/repository/components/lib``` directory.
2. Register the new filter in the ```<IS_HOME>/repository/deployment/server/webapps/scim2/WEB-INF/web.xml``` as below.
`
<web-app>
...
    <filter>
        <filter-name>SCIM2-additional-attribute-filter</filter-name>
        <filter-class>com.wso2.sample.CustomFilter</filter-class>
        <init-param>
            <param-name>reqHeadersList</param-name>
            <param-value>x-forwarded-for, userID</param-value>
        </init-param>
        <init-param>
             <param-name>reqHeadersList</param-name>
             <param-value>x-forwarded-for, userID</param-value>
        </init-param>
    </filter>
    ...
</web-app>
3. In the `reqHeadersList` param, you can define all the headers(comma seperated) that should be preserved to an ThreadLocal.
4. In the `resHeadersList` param , you can define all the headers that should be populated in the out going response.
The ThreadLocal response object should have the same keys as defined in the `resHeadersList` list.
5. Your responsibility is to populate a thread local object from your Custom User Store manager with the key names
defined in the `resHeadersList`. So that, this filter can extract them from the ThreadLocal in the response flow and set
as HTTP headers in the outgoing response.
