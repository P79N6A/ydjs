package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class CharConvertReader extends FilterReader {
    private CharConverter converter;

    public CharConvertReader(Reader in, String converterName) {
        this(in, (CharConverter)CharConverter.getInstance(converterName));
    }

    public CharConvertReader(Reader in, CharConverter converter) {
        super(in);
        this.converter = converter;
        if(converter == null) {
            throw new NullPointerException("converter is null");
        }
    }

    public int read() throws IOException {
        int ch = super.read();
        return ch < 0?ch:this.converter.convert((char)ch);
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = super.read(cbuf, off, len);
        if(count > 0) {
            this.converter.convert(cbuf, off, count);
        }

        return count;
    }
}
