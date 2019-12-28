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
```
<web-app>
...
    <filter>
        <filter-name>SCIM2-additional-attribute-filter</filter-name>
        <filter-class>com.wso2.sample.filter.SCIM2AdditionalAttributeAppenderFilter</filter-class>
        <init-param>
            <param-name>reqHeadersList</param-name>
            <param-value>header1, header2, header3</param-value>
        </init-param>
        <init-param>
             <param-name>resHeadersList</param-name>
             <param-value>res-header1, res-header2, res-header3</param-value>
        </init-param>
        <init-param>
             <param-name>HTTPMethods</param-name>
             <param-value>DELETE</param-value>
        </init-param>
     </filter>
     <filter-mapping>
        <filter-name>SCIM2-additional-attribute-filter</filter-name>
        <url-pattern>/Users/*</url-pattern>
    </filter-mapping>
    ...
</web-app>
```
3. In the `reqHeadersList` param, you can define all the headers(comma separated) that should be preserved to an ThreadLocal.
4. In the `resHeadersList` param , you can define all the headers that should be populated in the out going response.
5. In the `url-pattern` param, define the resource path you want thi filter to be engaged.
6. `HTTPMethods` value can be a comma separated HTTP methods where you want this filter to be engaged.
The ThreadLocal response object should have the same keys as defined in the `resHeadersList` list.
7. Your responsibility is to populate a thread local object from your Custom User Store manager with the key names
defined in the `resHeadersList`. So that, this filter can extract them from the ThreadLocal in the response flow and set
as HTTP headers in the outgoing response.
Sample implementation for `User store` side logic would be as follows.
```java
import com.wso2.sample.filter.SCIM2AdditionalAttributeAppenderFilter;

public class ReadWriteLDAPUserStoreManager extends ReadOnlyLDAPUserStoreManager {

    @Override
    public void doDeleteUser(String userName) throws UserStoreException {

        Map<String, String> additionalAttributes = SCIM2AdditionalAttributeAppenderFilter.getUserThreadLocal();
        for (String key : additionalAttributes.keySet()) {
            log.info(key + " : "  + additionalAttributes.get(key));
        }

        additionalAttributes.clear();
        additionalAttributes.put("res-header1", "response value 1");
        additionalAttributes.put("res-header2", "response value 2");
        SCIM2AdditionalAttributeAppenderFilter.setUserThreadLocal(additionalAttributes);
        ...

```

### P.S.
As the Tomcat filter's response is already committed, response
were not reflected in the out going HTTP response. Hence the additional utils. You can refer to below link for more details on that.
So : https://stackoverflow.com/questions/11025605/response-is-committing-and-dofilter-chain-is-broken
