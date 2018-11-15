package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArray {
    private byte[] bytes;
    private int offset;
    private int length;

    public ByteArray(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public byte[] toByteArray() {
        byte[] copy = new byte[this.length];
        System.arraycopy(this.bytes, this.offset, copy, 0, this.length);
        return copy;
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.bytes, this.offset, this.length);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.bytes, this.offset, this.length);
    }
}
