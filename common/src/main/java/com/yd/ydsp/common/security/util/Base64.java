package com.yd.ydsp.common.security.util;

import java.io.UnsupportedEncodingException;

/**
 * Comment of Base62
 *
 * @author axman.wang
 */
public class Base64 {
    private static byte[] encTable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+',
            '/'                   };

    private static byte[] decTable = new byte[256];
    static {
        for (int i = 0; i < encTable.length; i++) {
            decTable[encTable[i]] = (byte) i;
        }
    }

    /**
     * encode原始实现，byte[]输入到byte输出，不关注任何字符转换。
     *
     * @param data
     * @param wrap 是否换行
     * @return
     */
    public static byte[] encodeBytes(byte[] data, boolean wrap) {
        int len = ((data.length % 3 == 0) ? data.length / 3 : data.length / 3 + 1) * 4;
        int col = 0;
        if (wrap) {
            len += (len / 76) * 2;
        }
        byte[] tmp = new byte[len];
        int pos = 0, val = 0, off = 0;
        for (int i = 0; i < data.length; i++) {
            val = (val << 8) | (data[i] & 0xFF);
            pos += 8;
            while (pos > 5) {
                tmp[off++] = encTable[val >> (pos -= 6)];
                if (wrap) {
                    col++;
                    if (col >= 76) {
                        tmp[off++] = '\r';
                        tmp[off++] = '\n';
                        col = 0;
                    }
                }
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            tmp[off++] = encTable[val << (6 - pos)];
            if (wrap) {
                col++;
                if (col >= 76) {
                    tmp[off++] = '\r';
                    tmp[off++] = '\n';
                    col = 0;
                }
            }
            if (pos == 2) {
                tmp[off++] = '=';
                tmp[off++] = '=';
            } else if (pos == 4) {
                tmp[off++] = '=';
            }
        }
        return tmp;
    }

    /**
     * 包装encodeBytes
     *
     * @param data
     * @param wrap 是否换行
     * @return 返回的是编码后的基本64个字符组成的字符串，不需要字符集
     */
    public static String encode(byte[] data, boolean wrap) {
        return new String(encodeBytes(data, wrap));
    }

    /**
     * 重载String encode(byte[] data,boolean wrap),默认不换行
     *
     * @param data
     * @return 返回的是编码后的基本64个字符组成的字符串，不需要字符集
     */
    public static String encode(byte[] data) {
        return encode(data, false);
    }

    /**
     * 包装String encode(byte[] data,boolean wrap)
     *
     * @param input
     * @param enc 用该字符订对input字符串进行getBytes转换
     * @return 返回的是编码后的基本64个字符组成的字符串，不需要字符集
     */
    public static String encode(String input, String enc, boolean wrap) {
        byte[] data = null;
        try {
            data = input.getBytes(enc);
        } catch (Exception e) {
            return null;
        }
        return encode(data, wrap);
    }

    /**
     * 重载 String encode(String input, String enc, boolean wrap)，默认不换行 用系统当前字符集
     *
     * @param input
     * @param enc 用该字符订对input字符串进行getBytes转换
     * @return 返回的是编码后的基本64个字符组成的字符串，不需要字符集
     */
    public static String encode(String input, String enc) {
        return encode(input, enc, false);
    }

    /**
     * 重载 String encode(String input, String enc, boolean wrap)，默认不换行
     *
     * @param input
     * @param enc 用该字符订对input字符串进行getBytes转换
     * @return 返回的是编码后的基本64个字符组成的字符串，不需要字符集
     */
    public static String encode(String input) {
        byte[] data = null;
        try {
            data = input.getBytes();
        } catch (Exception e) {
            return null;
        }
        return encode(data, false);
    }

    /**
     * decode原始实现，byte[]输入到byte输出，不关注任何字符转换
     *
     * @param data
     * @return
     */
    public static byte[] decodeBytes(byte[] data) {
        byte[] tmp = new byte[data.length];
        //因为不知道data中是否包含了\r\n,所以无法精确算出decode后的长度。
        int offset = 0;
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            byte c = data[i];
            if (c == '=') {
                break;
            }
            if (c == '\r' || c == '\n') {
                continue;
            }
            val = (val << 6) | decTable[c];
            pos += 6;
            while (pos > 7) {
                tmp[offset++] = (byte) (val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        byte[] rt = new byte[offset];
        System.arraycopy(tmp, 0, rt, 0, offset);
        return rt;
    }

    /**
     * 包装 decodeBytes
     *
     * @param src
     * @param enc 以此字符集将decodeBytes返回值byte[]生成字符串
     * @return
     */
    public static String decode(byte[] src, String enc) {
        try {
            return new String(decodeBytes(src), enc);
        } catch (UnsupportedEncodingException e1) {
            return null;
        }
    }

    /**
     * 包装 String decode(byte[] src, String enc)
     *
     * @param src 为64个基础字符组成原始字符串，不需要关注字符集
     * @param enc 以此字符集将decodeBytes返回值byte[]生成字符串
     * @return
     */
    public static String decode(String src, String enc) {
        return decode(src.getBytes(), enc);
    }

    /**
     * 重载String decode(String src, String enc)，以默认字符集包装返回的数组。
     * @param src
     * @return
     */
    public static String decode(String src) {
        return new String(decodeBytes(src.getBytes()));
    }

    public static void main(String[] args){
        String encode = "春节即将到来之际，如何放假，放哪几天成为了公众关心的热点问题。而根据国务院下发的相关通知，2011年春节放假安排已经出炉。具体日期从2月2日，即大年三十开始放2011年1月27日...甄子丹与太太汪诗诗，26日到海洋公园出席《海洋公园梦幻水都揭幕礼》，联同特首曾荫权、海洋公园主席盛智文等一起主持揭幕仪式。盛智文很花心思，特别...";
        System.out.println(decode(encode(encode)).equals(encode));
    }

}
