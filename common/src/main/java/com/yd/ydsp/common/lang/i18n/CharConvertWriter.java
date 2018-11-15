package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class CharConvertWriter extends FilterWriter {
    private CharConverter converter;

    public CharConvertWriter(Writer out, String converterName) {
        this(out, (CharConverter)CharConverter.getInstance(converterName));
    }

    public CharConvertWriter(Writer out, CharConverter converter) {
        super(out);
        this.converter = converter;
        if(converter == null) {
            throw new NullPointerException("converter is null");
        }
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] newbuf = new char[len];
        System.arraycopy(cbuf, off, newbuf, 0, len);
        this.converter.convert(newbuf, 0, len);
        super.write(newbuf, 0, len);
    }

    public void write(int c) throws IOException {
        super.write(this.converter.convert((char)c));
    }

    public void write(String str, int off, int len) throws IOException {
        super.write(this.converter.convert(str, off, len), 0, len);
    }
}
