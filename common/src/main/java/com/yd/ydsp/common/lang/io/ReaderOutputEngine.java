package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.*;

public class ReaderOutputEngine implements OutputEngine {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private Reader reader;
    private String encoding;
    private OutputStreamFactory factory;
    private char[] buffer;
    private Writer writer;

    public ReaderOutputEngine(Reader reader) {
        this(reader, (OutputStreamFactory)null, (String)null, 8192);
    }

    public ReaderOutputEngine(Reader reader, OutputStreamFactory factory) {
        this(reader, factory, (String)null, 8192);
    }

    public ReaderOutputEngine(Reader reader, OutputStreamFactory factory, String encoding) {
        this(reader, factory, encoding, 8192);
    }

    public ReaderOutputEngine(Reader reader, OutputStreamFactory factory, String encoding, int bufferSize) {
        this.reader = reader;
        this.encoding = encoding;
        this.factory = factory == null?DEFAULT_OUTPUT_STREAM_FACTORY:factory;
        this.buffer = new char[bufferSize];
    }

    public void open(OutputStream out) throws IOException {
        if(this.writer != null) {
            throw new IOException("Already initialized");
        } else {
            this.writer = this.encoding == null?new OutputStreamWriter(this.factory.getOutputStream(out)):new OutputStreamWriter(this.factory.getOutputStream(out), this.encoding);
        }
    }

    public void execute() throws IOException {
        if(this.writer == null) {
            throw new IOException("Not yet initialized");
        } else {
            int amount = this.reader.read(this.buffer);
            if(amount < 0) {
                this.writer.close();
            } else {
                this.writer.write(this.buffer, 0, amount);
            }

        }
    }

    public void close() throws IOException {
        this.reader.close();
    }
}
