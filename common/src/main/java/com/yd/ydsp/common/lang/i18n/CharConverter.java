package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ClassInstantiationException;
import com.yd.ydsp.common.lang.ClassLoaderUtil;
import com.yd.ydsp.common.lang.i18n.CharConverterProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class CharConverter {
    public static final String SIMPLIFIED_TO_TRADITIONAL_CHINESE = "SimplifiedToTraditionalChinese";
    public static final String TRADITIONAL_TO_SIMPLIFIED_CHINESE = "TraditionalToSimplifiedChinese";
    private static final Map converters = Collections.synchronizedMap(new HashMap());

    public CharConverter() {
    }

    public static final CharConverter getInstance(String name) {
        CharConverter converter = (CharConverter)converters.get(name);
        if(converter == null) {
            CharConverterProvider provider;
            try {
                provider = (CharConverterProvider)ClassLoaderUtil.newServiceInstance("char.converter." + name);
            } catch (ClassInstantiationException var4) {
                throw new IllegalArgumentException("Failed to load char converter provider: " + name + ": " + var4.getMessage());
            } catch (ClassNotFoundException var5) {
                throw new IllegalArgumentException("Failed to load char converter provider: " + name + ": " + var5.getMessage());
            }

            converter = provider.createCharConverter();
            converters.put(name, converter);
        }

        return converter;
    }

    public abstract char convert(char var1);

    public String convert(CharSequence chars) {
        return this.convert((CharSequence)chars, 0, chars.length());
    }

    public String convert(CharSequence chars, int offset, int count) {
        if(offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        } else if(count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        } else {
            int end = offset + count;
            if(end > chars.length()) {
                throw new StringIndexOutOfBoundsException(offset + count);
            } else {
                StringBuffer buffer = new StringBuffer();

                for(int i = offset; i < end; ++i) {
                    buffer.append(this.convert(chars.charAt(i)));
                }

                return buffer.toString();
            }
        }
    }

    public void convert(char[] chars) {
        this.convert((char[])chars, 0, chars.length);
    }

    public void convert(char[] chars, int offset, int count) {
        if(offset < 0) {
            throw new ArrayIndexOutOfBoundsException(offset);
        } else if(count < 0) {
            throw new ArrayIndexOutOfBoundsException(count);
        } else {
            int end = offset + count;
            if(end > chars.length) {
                throw new ArrayIndexOutOfBoundsException(offset + count);
            } else {
                for(int i = offset; i < end; ++i) {
                    chars[i] = this.convert(chars[i]);
                }

            }
        }
    }
}
