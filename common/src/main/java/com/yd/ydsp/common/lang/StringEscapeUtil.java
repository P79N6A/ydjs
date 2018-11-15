package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */


import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.i18n.LocaleUtil;
import com.yd.ydsp.common.lang.internal.Entities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.BitSet;

public class StringEscapeUtil {
    private static final BitSet ALPHA = new BitSet(256);
    private static final BitSet ALPHANUM;
    private static final BitSet MARK;
    private static final BitSet RESERVED;
    private static final BitSet UNRESERVED;
    private static int[] HEXADECIMAL;

    public StringEscapeUtil() {
    }

    public static String escapeJava(String str) {
        return escapeJavaStyleString(str, false, false);
    }

    public static String escapeJava(String str, boolean strict) {
        return escapeJavaStyleString(str, false, strict);
    }

    public static void escapeJava(String str, Writer out) throws IOException {
        escapeJavaStyleString(str, false, out, false);
    }

    public static void escapeJava(String str, Writer out, boolean strict) throws IOException {
        escapeJavaStyleString(str, false, out, strict);
    }

    public static String escapeJavaScript(String str) {
        return escapeJavaStyleString(str, true, false);
    }

    public static String escapeJavaScript(String str, boolean strict) {
        return escapeJavaStyleString(str, true, strict);
    }

    public static void escapeJavaScript(String str, Writer out) throws IOException {
        escapeJavaStyleString(str, true, out, false);
    }

    public static void escapeJavaScript(String str, Writer out, boolean strict) throws IOException {
        escapeJavaStyleString(str, true, out, strict);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes, boolean strict) {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length() * 2);
                return escapeJavaStyleString(str, escapeSingleQuotes, e, strict)?e.toString():str;
            } catch (IOException var4) {
                return str;
            }
        }
    }

    private static boolean escapeJavaStyleString(String str, boolean escapeSingleQuote, Writer out, boolean strict) throws IOException {
        boolean needToChange = false;
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str == null) {
            return needToChange;
        } else {
            int length = str.length();

            for(int i = 0; i < length; ++i) {
                char ch = str.charAt(i);
                if(ch < 32) {
                    switch(ch) {
                        case '\b':
                            out.write(92);
                            out.write(98);
                            break;
                        case '\t':
                            out.write(92);
                            out.write(116);
                            break;
                        case '\n':
                            out.write(92);
                            out.write(110);
                            break;
                        case '\u000b':
                        default:
                            if(ch > 15) {
                                out.write("\\u00" + Integer.toHexString(ch).toUpperCase());
                            } else {
                                out.write("\\u000" + Integer.toHexString(ch).toUpperCase());
                            }
                            break;
                        case '\f':
                            out.write(92);
                            out.write(102);
                            break;
                        case '\r':
                            out.write(92);
                            out.write(114);
                    }

                    needToChange = true;
                } else if(strict && ch > 255) {
                    if(ch > 4095) {
                        out.write("\\u" + Integer.toHexString(ch).toUpperCase());
                    } else {
                        out.write("\\u0" + Integer.toHexString(ch).toUpperCase());
                    }

                    needToChange = true;
                } else {
                    switch(ch) {
                        case '\"':
                            out.write(92);
                            out.write(34);
                            needToChange = true;
                            break;
                        case '\'':
                            if(escapeSingleQuote) {
                                out.write(92);
                                needToChange = true;
                            }

                            out.write(39);
                            break;
                        case '\\':
                            out.write(92);
                            out.write(92);
                            needToChange = true;
                            break;
                        default:
                            out.write(ch);
                    }
                }
            }

            return needToChange;
        }
    }

    public static String unescapeJava(String str) {
        return unescapeJavaStyleString(str);
    }

    public static void unescapeJava(String str, Writer out) throws IOException {
        unescapeJavaStyleString(str, out);
    }

    public static String unescapeJavaScript(String str) {
        return unescapeJavaStyleString(str);
    }

    public static void unescapeJavaScript(String str, Writer out) throws IOException {
        unescapeJavaStyleString(str, out);
    }

    private static String unescapeJavaStyleString(String str) {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length());
                return unescapeJavaStyleString(str, e)?e.toString():str;
            } catch (IOException var2) {
                return str;
            }
        }
    }

    private static boolean unescapeJavaStyleString(String str, Writer out) throws IOException {
        boolean needToChange = false;
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str == null) {
            return needToChange;
        } else {
            int length = str.length();
            StringBuffer unicode = new StringBuffer(4);
            boolean hadSlash = false;
            boolean inUnicode = false;

            for(int i = 0; i < length; ++i) {
                char ch = str.charAt(i);
                if(inUnicode) {
                    unicode.append(ch);
                    if(unicode.length() == 4) {
                        String unicodeStr = unicode.toString();

                        try {
                            int e = Integer.parseInt(unicodeStr, 16);
                            out.write((char)e);
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                            needToChange = true;
                        } catch (NumberFormatException var11) {
                            out.write("\\u" + unicodeStr);
                        }
                    }
                } else if(hadSlash) {
                    hadSlash = false;
                    switch(ch) {
                        case '\"':
                            out.write(34);
                            needToChange = true;
                            break;
                        case '\'':
                            out.write(39);
                            needToChange = true;
                            break;
                        case '\\':
                            out.write(92);
                            needToChange = true;
                            break;
                        case 'b':
                            out.write(8);
                            needToChange = true;
                            break;
                        case 'f':
                            out.write(12);
                            needToChange = true;
                            break;
                        case 'n':
                            out.write(10);
                            needToChange = true;
                            break;
                        case 'r':
                            out.write(13);
                            needToChange = true;
                            break;
                        case 't':
                            out.write(9);
                            needToChange = true;
                            break;
                        case 'u':
                            inUnicode = true;
                            break;
                        default:
                            out.write(ch);
                    }
                } else if(ch == 92) {
                    hadSlash = true;
                } else {
                    out.write(ch);
                }
            }

            if(hadSlash) {
                out.write(92);
            }

            return needToChange;
        }
    }

    public static String escapeHtml(String str) {
        return escapeEntities(Entities.HTML40, str);
    }

    public static void escapeHtml(String str, Writer out) throws IOException {
        escapeEntities(Entities.HTML40, str, out);
    }

    public static String escapeXml(String str) {
        return escapeEntities(Entities.XML, str);
    }

    public static void escapeXml(String str, Writer out) throws IOException {
        escapeEntities(Entities.XML, str, out);
    }

    public static String escapeEntities(Entities entities, String str) {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length());
                return escapeEntitiesInternal(entities, str, e)?e.toString():str;
            } catch (IOException var3) {
                return str;
            }
        }
    }

    public static void escapeEntities(Entities entities, String str, Writer out) throws IOException {
        escapeEntitiesInternal(entities, str, out);
    }

    public static String unescapeHtml(String str) {
        return unescapeEntities(Entities.HTML40, str);
    }

    public static void unescapeHtml(String str, Writer out) throws IOException {
        unescapeEntities(Entities.HTML40, str, out);
    }

    public static String unescapeXml(String str) {
        return unescapeEntities(Entities.XML, str);
    }

    public static void unescapeXml(String str, Writer out) throws IOException {
        unescapeEntities(Entities.XML, str, out);
    }

    public static String unescapeEntities(Entities entities, String str) {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length());
                return unescapeEntitiesInternal(entities, str, e)?e.toString():str;
            } catch (IOException var3) {
                return str;
            }
        }
    }

    public static void unescapeEntities(Entities entities, String str, Writer out) throws IOException {
        unescapeEntitiesInternal(entities, str, out);
    }

    private static boolean escapeEntitiesInternal(Entities entities, String str, Writer out) throws IOException {
        boolean needToChange = false;
        if(entities == null) {
            throw new IllegalArgumentException("The Entities must not be null");
        } else if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str == null) {
            return needToChange;
        } else {
            for(int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                String entityName = entities.getEntityName(ch);
                if(entityName == null) {
                    out.write(ch);
                } else {
                    out.write(38);
                    out.write(entityName);
                    out.write(59);
                    needToChange = true;
                }
            }

            return needToChange;
        }
    }

    private static boolean unescapeEntitiesInternal(Entities entities, String str, Writer out) throws IOException {
        boolean needToChange = false;
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str == null) {
            return needToChange;
        } else {
            for(int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if(ch == 38) {
                    int semi = str.indexOf(59, i + 1);
                    if(semi != -1 && i + 1 < semi - 1) {
                        if(str.charAt(i + 1) == 35) {
                            int entityName = i + 2;
                            byte entityValue = 10;
                            if(entityName >= semi - 1) {
                                out.write(ch);
                                out.write(35);
                                ++i;
                                continue;
                            }

                            char firstChar = str.charAt(entityName);
                            if(firstChar == 120 || firstChar == 88) {
                                ++entityName;
                                entityValue = 16;
                                if(entityName >= semi - 1) {
                                    out.write(ch);
                                    out.write(35);
                                    ++i;
                                    continue;
                                }
                            }

                            try {
                                int e = Integer.parseInt(str.substring(entityName, semi), entityValue);
                                out.write(e);
                                needToChange = true;
                            } catch (NumberFormatException var11) {
                                out.write(ch);
                                out.write(35);
                                ++i;
                                continue;
                            }
                        } else {
                            String var12 = str.substring(i + 1, semi);
                            int var13 = -1;
                            if(entities != null) {
                                var13 = entities.getEntityValue(var12);
                            }

                            if(var13 == -1) {
                                out.write(38);
                                out.write(var12);
                                out.write(59);
                            } else {
                                out.write(var13);
                                needToChange = true;
                            }
                        }

                        i = semi;
                    } else {
                        out.write(ch);
                    }
                } else {
                    out.write(ch);
                }
            }

            return needToChange;
        }
    }

    public static String escapeSql(String str) {
        return StringUtil.replace(str, "\'", "\'\'");
    }

    public static void escapeSql(String str, Writer out) throws IOException {
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else {
            String result = StringUtil.replace(str, "\'", "\'\'");
            if(result != null) {
                out.write(result);
            }

        }
    }

    public static String escapeURL(String str) {
        try {
            return escapeURLInternal(str, (String)null, true);
        } catch (UnsupportedEncodingException var2) {
            return str;
        }
    }

    public static String escapeURL(String str, String encoding) throws UnsupportedEncodingException {
        return escapeURLInternal(str, encoding, true);
    }

    public static String escapeURL(String str, String encoding, boolean strict) throws UnsupportedEncodingException {
        return escapeURLInternal(str, encoding, strict);
    }

    public static void escapeURL(String str, String encoding, Writer out) throws IOException {
        escapeURLInternal(str, encoding, out, true);
    }

    public static void escapeURL(String str, String encoding, Writer out, boolean strict) throws IOException {
        escapeURLInternal(str, encoding, out, strict);
    }

    private static String escapeURLInternal(String str, String encoding, boolean strict) throws UnsupportedEncodingException {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length());
                return escapeURLInternal(str, encoding, e, strict)?e.toString():str;
            } catch (UnsupportedEncodingException var4) {
                throw var4;
            } catch (IOException var5) {
                return str;
            }
        }
    }

    private static boolean escapeURLInternal(String str, String encoding, Writer out, boolean strict) throws IOException {
        if(encoding == null) {
            encoding = LocaleUtil.getContext().getCharset();
        }

        boolean needToChange = false;
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str == null) {
            return needToChange;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(10);
            OutputStreamWriter writer = new OutputStreamWriter(baos, encoding);

            for(int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if(isSafeCharacter(ch, strict)) {
                    out.write(ch);
                } else if(ch == 32) {
                    out.write(43);
                    needToChange = true;
                } else {
                    try {
                        writer.write(ch);
                        writer.flush();
                    } catch (IOException var14) {
                        baos.reset();
                        continue;
                    }

                    byte[] bytes = baos.toByteArray();

                    for(int j = 0; j < bytes.length; ++j) {
                        byte toEscape = bytes[j];
                        out.write(37);
                        int low = toEscape & 15;
                        int high = (toEscape & 240) >> 4;
                        out.write(HEXADECIMAL[high]);
                        out.write(HEXADECIMAL[low]);
                    }

                    baos.reset();
                    needToChange = true;
                }
            }

            return needToChange;
        }
    }

    private static boolean isSafeCharacter(int ch, boolean strict) {
        return strict?UNRESERVED.get(ch):ch > 32 && !RESERVED.get(ch) && !Character.isWhitespace((char)ch);
    }

    public static String unescapeURL(String str) {
        try {
            return unescapeURLInternal(str, (String)null);
        } catch (UnsupportedEncodingException var2) {
            return str;
        }
    }

    public static String unescapeURL(String str, String encoding) throws UnsupportedEncodingException {
        return unescapeURLInternal(str, encoding);
    }

    public static void unescapeURL(String str, String encoding, Writer out) throws IOException {
        unescapeURLInternal(str, encoding, out);
    }

    private static String unescapeURLInternal(String str, String encoding) throws UnsupportedEncodingException {
        if(str == null) {
            return null;
        } else {
            try {
                StringWriter e = new StringWriter(str.length());
                return unescapeURLInternal(str, encoding, e)?e.toString():str;
            } catch (UnsupportedEncodingException var3) {
                throw var3;
            } catch (IOException var4) {
                return str;
            }
        }
    }

    private static boolean unescapeURLInternal(String str, String encoding, Writer out) throws IOException {
        if(encoding == null) {
            encoding = LocaleUtil.getContext().getCharset();
        }

        boolean needToChange = false;
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else {
            int length = str.length();
            byte[] buffer = null;
            int pos = 0;

            for(int i = 0; i < length; ++i) {
                char ch = str.charAt(i);
                if(ch < 256) {
                    if(buffer == null) {
                        buffer = new byte[length - i];
                    }

                    switch(ch) {
                        case '%':
                            if(i + 2 < length) {
                                try {
                                    buffer[pos] = (byte)Integer.parseInt(str.substring(i + 1, i + 3), 16);
                                    ++pos;
                                    i += 2;
                                    needToChange = true;
                                } catch (NumberFormatException var10) {
                                    buffer[pos++] = (byte)ch;
                                }
                            } else {
                                buffer[pos++] = (byte)ch;
                            }
                            break;
                        case '+':
                            buffer[pos++] = 32;
                            needToChange = true;
                            break;
                        default:
                            buffer[pos++] = (byte)ch;
                    }
                } else {
                    if(pos > 0) {
                        out.write(new String(buffer, 0, pos, encoding));
                        pos = 0;
                    }

                    out.write(ch);
                }
            }

            if(pos > 0) {
                out.write(new String(buffer, 0, pos, encoding));
                boolean var11 = false;
            }

            return needToChange;
        }
    }

    static {
        int i;
        for(i = 97; i <= 122; ++i) {
            ALPHA.set(i);
        }

        for(i = 65; i <= 90; ++i) {
            ALPHA.set(i);
        }

        ALPHANUM = new BitSet(256);
        ALPHANUM.or(ALPHA);

        for(i = 48; i <= 57; ++i) {
            ALPHANUM.set(i);
        }

        MARK = new BitSet(256);
        MARK.set(45);
        MARK.set(95);
        MARK.set(46);
        MARK.set(33);
        MARK.set(126);
        MARK.set(42);
        MARK.set(39);
        MARK.set(40);
        MARK.set(41);
        RESERVED = new BitSet(256);
        RESERVED.set(59);
        RESERVED.set(47);
        RESERVED.set(63);
        RESERVED.set(58);
        RESERVED.set(64);
        RESERVED.set(38);
        RESERVED.set(61);
        RESERVED.set(43);
        RESERVED.set(36);
        RESERVED.set(44);
        UNRESERVED = new BitSet(256);
        UNRESERVED.or(ALPHANUM);
        UNRESERVED.or(MARK);
        HEXADECIMAL = new int[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    }
}
