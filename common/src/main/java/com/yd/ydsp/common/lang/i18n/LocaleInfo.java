package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ObjectUtil;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.i18n.CharsetMap;
import com.yd.ydsp.common.lang.i18n.LocaleUtil;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Locale;

public final class LocaleInfo implements Cloneable, Serializable {
    private static final long serialVersionUID = 3257847675461251635L;
    private static final CharsetMap CHARSET_MAP = new CharsetMap();
    private Locale locale;
    private String charset;

    LocaleInfo() {
        this.locale = Locale.getDefault();
        this.charset = LocaleUtil.getCanonicalCharset((new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding(), "ISO-8859-1");
    }

    public LocaleInfo(Locale locale) {
        this(locale, (String)null);
    }

    public LocaleInfo(Locale locale, String charset) {
        this(locale, charset, LocaleUtil.getDefault());
    }

    LocaleInfo(Locale locale, String charset, LocaleInfo defaultLocaleInfo) {
        if(locale == null) {
            locale = defaultLocaleInfo.getLocale();
            if(StringUtil.isEmpty(charset)) {
                charset = defaultLocaleInfo.getCharset();
            }
        }

        if(StringUtil.isEmpty(charset)) {
            charset = CHARSET_MAP.getCharSet(locale);
        }

        this.locale = locale;
        this.charset = LocaleUtil.getCanonicalCharset(charset, defaultLocaleInfo.getCharset());
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getCharset() {
        return this.charset;
    }

    public boolean equals(Object o) {
        if(o == null) {
            return false;
        } else if(o == this) {
            return true;
        } else if(!(o instanceof LocaleInfo)) {
            return false;
        } else {
            LocaleInfo otherLocaleInfo = (LocaleInfo)o;
            return ObjectUtil.equals(this.locale, otherLocaleInfo.locale) && ObjectUtil.equals(this.charset, otherLocaleInfo.charset);
        }
    }

    public int hashCode() {
        return ObjectUtil.hashCode(this.locale) ^ ObjectUtil.hashCode(this.charset);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new InternalError();
        }
    }

    public String toString() {
        return this.locale + ":" + this.charset;
    }
}
