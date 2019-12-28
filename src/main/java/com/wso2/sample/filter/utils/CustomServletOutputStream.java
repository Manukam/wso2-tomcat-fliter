package com.wso2.sample.filter.utils;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public void write(int b) throws IOException
    {
        out.write(b);
    }

    public byte[] getBytes()
    {
        return out.toByteArray();
    }
}
