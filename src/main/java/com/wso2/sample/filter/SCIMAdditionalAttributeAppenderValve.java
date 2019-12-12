/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.sample.filter;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;


public class SCIMAdditionalAttributeAppenderValve extends ValveBase{

    private ServletContext context = null;
    private static final ThreadLocal<Map> userThreadLocal = new ThreadLocal();
    private static final String REQ_HEADERS_LIST = "reqHeadersList";
    public static final String RES_HEADERS_LIST = "resHeadersList";

    private static final Log log = LogFactory.getLog(SCIMAdditionalAttributeAppenderValve.class);


    @Override
    protected void initInternal() throws LifecycleException {

    }


    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {

    }
}
