package com.yd.ydsp.common.fasttext.html;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 类EmptyTagCompletion.java的实现描述：
 * 
 * <pre>
 * 对形如&lt;img &gt;没有closed的标签自动加上/,形成完整的&lt;img/&gt;。
 * 对需要完成的标签需要在构建时提供，例如img,li,link etc.
 * </pre>
 * 
 * @author guolin.zhuanggl 2008-9-3 上午10:48:44
 */

@Deprecated
public final class EmptyTagCompletion implements HtmlConverter {
    private Set<String> tagSet;

    public EmptyTagCompletion(Set<String> tagSet) {
        this.tagSet = new HashSet<String>();
        for (String tag : tagSet) {
            this.tagSet.add(tag.trim().toLowerCase());
        }
    }

    private void toLowcaseOfTagNameAttrs(Tag tag) {
        if (tag.isEndTag()) {
            tag.setTagName("/" + tag.getTagName().toLowerCase());
        } else {
            tag.setTagName(tag.getTagName().toLowerCase());
        }
        Vector attrs = tag.getAttributesEx();
        for (int i = 0; i < attrs.size(); i++) {
            Attribute attr = (Attribute) attrs.elementAt(i);
            if (attr != null) {
                if (attr.getName() != null) {
                    attr.setName(attr.getName().toLowerCase());
                }
            }
        }
    }

    public String convert(String html) {
        NodeList temp = new NodeList();
        try {
            Parser parser = new Parser();
            parser.setInputHTML(html);
            //            Node[] list = parser.extractAllNodesThatAre(Node.class);
            //
            //            for (int i = 0; i < list.length; i++) {
            //                Node n = list[i];
            //                if (n instanceof Tag) {
            //                    Tag tag = (Tag) n;
            //                    if (!tag.isEmptyXmlTag() && !tag.isEndTag() && tag.getEndTag() == null) {
            //                        if (tagSet.contains(tag.getTagName().toLowerCase())) {
            //                            tag.setEmptyXmlTag(true);
            //                        }
            //                    }
            //                    toLowcaseOfTagNameAttrs(tag);
            //                }
            //                if (n.getParent() == null || n.getParent() == n) {//只有一个Empty xml tag时其父为其自身
            //                    temp.add(n);
            //                }
            //            }
        } catch (ParserException pe) {
            throw new RuntimeException("Html parser error!");
        }
        return temp.toHtml();
    }
}
