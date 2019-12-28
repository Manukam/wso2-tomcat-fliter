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
