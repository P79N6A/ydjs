package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.io.OutputEngine;
import com.yd.ydsp.common.lang.io.OutputEngine.OutputStreamFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamOutputEngine implements OutputEngine {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private InputStream in;
    private OutputStreamFactory factory;
    private byte[] buffer;
    private OutputStream out;

    public InputStreamOutputEngine(InputStream in, OutputStreamFactory factory) {
        this(in, factory, 8192);
    }

    public InputStreamOutputEngine(InputStream in, OutputStreamFactory factory, int bufferSize) {
        this.in = in;
        this.factory = factory == null?DEFAULT_OUTPUT_STREAM_FACTORY:factory;
        this.buffer = new byte[bufferSize];
    }

    public void open(OutputStream out) throws IOException {
        if(this.out != null) {
            throw new IOException("Already initialized");
        } else {
            this.out = this.factory.getOutputStream(out);
        }
    }

    public void execute() throws IOException {
        if(this.out == null) {
            throw new IOException("Not yet initialized");
        } else {
            int amount = this.in.read(this.buffer);
            if(amount < 0) {
                this.out.close();
            } else {
                this.out.write(this.buffer, 0, amount);
            }

        }
    }

    public void close() throws IOException {
        this.in.close();
    }
}
