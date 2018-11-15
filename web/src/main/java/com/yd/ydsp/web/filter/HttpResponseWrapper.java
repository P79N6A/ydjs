package com.yd.ydsp.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * Created by fachao.zfc on 2016/2/26.
 */
public class HttpResponseWrapper extends HttpServletResponseWrapper {
    public static final Logger logger = LoggerFactory.getLogger(HttpResponseWrapper.class);

    //输出缓存
    private ByteArrayOutputStream buffer = null;

    //输出流
    private ServletOutputStream output = null;

    //字符串输出器
    private PrintWriter writer = null;

    /**
     * 构造函数，传入
     *
     * @param response
     */
    public HttpResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        buffer = new ByteArrayOutputStream();
        output = new WapperedOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, "gbk"));
    }

    /**
     * 重写父类获取字符输出流的方法
     *
     * @return
     * @throws java.io.IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return this.writer;
    }

    /**
     * 重写父类获取字节输出流的方法
     *
     * @return
     * @throws java.io.IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.output;
    }

    /**
     * 重写父类刷新缓存的方法
     *
     * @throws java.io.IOException
     */
    @Override
    public void flushBuffer() throws IOException {
        if (output != null) {
            output.flush();
        }

        if (writer != null) {
            writer.flush();
        }
    }

    /**
     * 重写父类重置缓存的方法
     */
    @Override
    public void reset() {
        this.buffer.reset();
    }

    /**
     * 自定义方法，获取输出流的内容
     *
     * @return
     * @throws java.io.IOException
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    //内部类
    class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
