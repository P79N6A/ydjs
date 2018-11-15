package com.yd.ydsp.common.fasttext.psoriasis;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.yd.ydsp.common.fasttext.codec.HtmlDecodeRef;

/**
 * 使用HTMLParser進行html tag過濾，在html不規範時也可以正確過濾，性能和 質量都高於Swing的html parser。 类HTMLParserExtractText.java的实现描述：TODO 类实现描述
 * 
 * @author guolin.zhuanggl 2008-7-31 下午01:52:40
 */

public class HTMLParserExtractText implements MappedExtractText {

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param html 输入html文本
     * @param ignoreCase 结果是否需要转换为小写。
     * @return 转换后的文本
     */
    public String getText(String html, boolean ignoreCase) {
        if (html == null) {
            return html;
        }
        try {
            Parser parser = new Parser();
            parser.setInputHTML(html);
            HTMLVisitor visitor = new HTMLVisitor();
            parser.visitAllNodesWith(visitor);
            String ret = decodeText(visitor.toString());
            return ignoreCase ? ret.toLowerCase() : ret;
        } catch (ParserException e) {
        }
        return null;
    }

    // decode html 转义字符
    public String decodeText(String str) {
        if (str == null) {
            return str;
        }
        HtmlDecodeRef hdr = HtmlDecodeRef.getInstance();
        StringBuilder sb = new StringBuilder();
        char ch;
        int maxLen = 10;        //html转义字符最大长度
        int lastPos = 0;
        boolean startWithAmp = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch == '&') {
                sb.append(str.substring(lastPos, i));
                lastPos = i;
                startWithAmp = true;
            } else if (startWithAmp == true && ch == ';') {
                startWithAmp= false;
                
                String ss = str.substring(lastPos, i + 1);
                if(ss.length() > maxLen) continue;
                Character cc = hdr.getChar(ss);
                if (cc != null) {
                    sb.append(cc);
                    lastPos = i + 1;
                }
            }
        }
        sb.append(str.substring(lastPos));
        return sb.toString();
    }

    public MappedCharArray decodeText(MappedCharArray mca) {
        HtmlDecodeRef hdr = HtmlDecodeRef.getInstance();
        int[] map = mca.getMap();
        int[] scrMap = mca.getMap().clone();
        char[] str = mca.getTarget();
        char[] scrStr = mca.getTarget().clone();
        int strLen = mca.getCharCount();
        int maxLen = 10;        //html转义字符最大长度
        int ampSrcPos = 0;
        int ampTarPos = 0;
        int current = 0;
        boolean startWithAmp = false;
        for (int i = 0; i < strLen; i++, current++) {
            str[current] = scrStr[i];
            if (i < scrMap.length) {
                map[current] = scrMap[i];
            }
            if(scrStr[i] == '&') {
                startWithAmp = true;
                ampSrcPos = i;
                ampTarPos = current;
            } else if (startWithAmp == true && scrStr[i] == ';') {
                startWithAmp= false;
                
                int len = i - ampSrcPos + 1;
                if(len > maxLen) {
                    continue;
                }
                String ss = new String(scrStr, ampSrcPos, len);
                Character cc = hdr.getChar(ss);
                if (cc != null) {
                    current = ampTarPos;
                    str[current] = cc;
                    map[current] = scrMap[ampSrcPos];
                }
            }
        }
        mca.decreaseCharCount(mca.getCharCount() - current);
        return mca;
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等
     * 
     * @param src 输入html文本，提供映射功能
     * @return 转换后的文本
     */
    public MappedCharArray getText(MappedCharArray src) {
        return getText(src, false);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等，保留大小写信息。
     * 
     * @param src 输入html文本
     * @return 转换后的文本
     */
    public String getText(String src) {
        return getText(src, false);
    }

    /**
     * 利用HTMLParser过滤html文本中的tag字符串，保留所有文本内容，包括脚本等。 提供字符串映射功能。
     */
    public MappedCharArray getText(MappedCharArray src, boolean ignoreCase) {
        if (src == null) {
            return src;
        }
        try {
            Parser parser = new Parser();
            String content = new String(src.getTarget(), 0, src.getCharCount());
            if (ignoreCase) {
                content = content.toLowerCase();
            }
            parser.setInputHTML(content);
            parser.setNodeFactory(parser.getLexer());// 设置一个简单的NodeFactory
            HTMLVisitor visitor = new HTMLVisitor(src);
            parser.visitAllNodesWith(visitor);
            src.decreaseCharCount(src.getCharCount() - visitor.getCharCount());
            return decodeText(visitor.getResult());
        } catch (ParserException e) {
        }
        return null;
    }

    private class HTMLVisitor extends NodeVisitor {

        private StringBuilder   buffer  = new StringBuilder();
        private MappedCharArray mca;
        private int             current = 0;
        private int             excludeStart, excludeEnd;

        HTMLVisitor(){
            super();
        }

        HTMLVisitor(MappedCharArray mca){
            this();
            this.mca = mca;

        }

        public void visitStringNode(Text node) {
            int start = node.getStartPosition();
            if (start > excludeStart && start < excludeEnd) {
                return;
            }
            Node parent = node.getParent();
            if (parent != null
                && (parent.getClass().isAssignableFrom(ScriptTag.class) || parent.getClass().isAssignableFrom(
                                                                                                              StyleTag.class))) {
                Tag tag = (Tag) parent;
                Tag endTag = tag.getEndTag();
                int s = tag.getStartPosition();
                int e = endTag.getEndPosition();
                if (s < excludeStart) {
                    excludeStart = s;
                }
                if (e > excludeEnd) {
                    excludeEnd = e;
                }
                return;
            }
            int pos = node.getStartPosition();
            String str = node.getText();
            if (str != null) {
                if (mca != null) {
                    int[] map = mca.getMap();
                    char[] target = mca.getTarget();
                    for (int i = 0; i < str.length(); i++) {
                        target[current] = str.charAt(i);
                        if (pos < map.length) {
                            map[current++] = map[pos++];
                        }
                    }
                } else {
                    buffer.append(str);
                }
            }
        }

        public int getCharCount() {
            return current;
        }

        @Override
        public String toString() {
            return buffer.toString();
        }

        public MappedCharArray getResult() {
            return mca;
        }
    }

}
