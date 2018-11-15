package com.yd.ydsp.common.fasttext.text;

/**
 * <pre>
 * 当有比较多的字符不能在某个串中出现， 通过一个查表算法确定。比如识别［a,b,c,d］不能在一个string中出现。
 * 由于消耗比较多的内存， 最好使用单一实例。 初始化过程并非线程安全。 最好一次完成初始化的过程。
 * 
 * 定义XML允许出现的合法字符::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
 * 使用方法：
 *   private final static DetectDefinedChar      detectXMLChar        = new DetectDefinedChar();
 *   static {
 *      detectXMLChar.addAllowChar(0x9);
 *      detectXMLChar.addAllowChar(0xA);
 *      detectXMLChar.addAllowChar(0xD);
 *      for (int i = 0x20; i < (0xD7FF + 1); i++) {
 *          detectXMLChar.addAllowChar(i);
 *      }
 *      for (int i = 0xE000; i < (0xFFFD + 1); i++) {
 *          detectXMLChar.addAllowChar(i);
 *      }
 *      for (int i = 0x10000; i < (0x10FFFF + 1); i++) {
 *          detectXMLChar.addAllowChar(i);
 *      }
 *   }
 * </pre>
 * 
 * @author sdh5724
 */
public class DetectDefinedChar {

    byte[] masks = new byte[1024 * 8];

    public DetectDefinedChar(){}

    public DetectDefinedChar(char allows[]){
        addAllowChar(allows);
    }

    /**
     * 增加一个跳越字符
     * 
     * @param c
     */
    public void addAllowChar(char c) {
        int pos = c >> 3;
        masks[pos] = (byte) ((masks[pos] & 0xFF) | (1 << (c % 8)));
    }

    public void addAllowChar(int c) {
        addAllowChar((char) c);
    }

    /**
     * 增加一个string里的所有字符
     * 
     * @param str
     */
    public void addAllowChar(String str) {
        if (str != null) {
            char cs[] = str.toCharArray();
            for (char c : cs) {
                addAllowChar(c);
            }
        }
    }

    public void addAllowChar(char allows[]) {
        if (allows != null) {
            for (char c : allows) {
                addAllowChar(c);
            }
        }
    }

    public void removeAllowChar(char c) {
        int pos = c >> 3;
        masks[pos] = (byte) ((masks[pos] & 0xFF) & (~(1 << (c % 8))));
    }

    public boolean isAllowChar(char c) {
        int pos = c >> 3;
        int i = (masks[pos] & 0xFF) & (1 << (c % 8));
        return (i != 0);
    }

    public boolean hasAllowChar(char cs[]) {
        if (cs != null) {
            for (char c : cs) {
                if (isAllowChar(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAllowChar(String str) {
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                if (isAllowChar(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

}
