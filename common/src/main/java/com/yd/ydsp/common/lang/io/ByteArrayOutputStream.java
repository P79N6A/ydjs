package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayOutputStream extends OutputStream {
    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 8192;
    private byte[] buffer;
    private int index;
    private int capacity;
    private boolean closed;
    private boolean shared;

    public ByteArrayOutputStream() {
        this(8192);
    }

    public ByteArrayOutputStream(int initialBufferSize) {
        this.capacity = initialBufferSize;
        this.buffer = new byte[this.capacity];
    }

    public void write(int datum) throws IOException {
        if(this.closed) {
            throw new IOException("Stream closed");
        } else {
            if(this.index >= this.capacity) {
                this.capacity = this.capacity * 2 + 1;
                byte[] tmp = new byte[this.capacity];
                System.arraycopy(this.buffer, 0, tmp, 0, this.index);
                this.buffer = tmp;
                this.shared = false;
            }

            this.buffer[this.index++] = (byte)datum;
        }
    }

    public void write(byte[] data, int offset, int length) throws IOException {
        if(data == null) {
            throw new NullPointerException();
        } else if(offset >= 0 && offset + length <= data.length && length >= 0) {
            if(this.closed) {
                throw new IOException("Stream closed");
            } else {
                if(this.index + length > this.capacity) {
                    this.capacity = this.capacity * 2 + length;
                    byte[] tmp = new byte[this.capacity];
                    System.arraycopy(this.buffer, 0, tmp, 0, this.index);
                    this.buffer = tmp;
                    this.shared = false;
                }

                System.arraycopy(data, offset, this.buffer, this.index, length);
                this.index += length;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void close() {
        this.closed = true;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buffer, 0, this.index);
    }

    public ByteArray toByteArray() {
        this.shared = true;
        return new ByteArray(this.buffer, 0, this.index);
    }

    public InputStream toInputStream() {
        this.shared = true;
        return new ByteArrayInputStream(this.buffer, 0, this.index);
    }

    public void reset() throws IOException {
        if(this.closed) {
            throw new IOException("Stream closed");
        } else {
            if(this.shared) {
                this.buffer = new byte[this.capacity];
                this.shared = false;
            }

            this.index = 0;
        }
    }
}
