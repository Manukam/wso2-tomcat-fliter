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

package com.wso2.sample.filter.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class BufferResponseWrapper extends HttpServletResponseWrapper {

    CustomServletOutputStream stream = new CustomServletOutputStream();

    public BufferResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
    }

    public ServletOutputStream getOutputStream() throws IOException
    {
        return stream;
    }

    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(stream);
    }

    public byte[] getWrapperBytes()
    {
        return stream.getBytes();
    }
}
