package com.yd.ydsp.common.fasttext.security.xss;

import com.yd.ydsp.common.fasttext.security.xss.impl.CssScanner;
import com.yd.ydsp.common.fasttext.security.xss.impl.FragmentXppParser;
import com.yd.ydsp.common.fasttext.security.xss.impl.XssFilterDocumentHandler;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class XssXppScanner {
    private Policy                policy               = null;
    private CssScanner            cssScanner           = new CssScanner();

    protected static final String NAMES_ELEMS_PROPERTY = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS_PROPERTY = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS_PROPERTY     = "http://cyberneko.org/html/properties/filters";

    public XssXppScanner(Policy policy) {
        this.policy = policy;
    }

    public String scan(String html) {
        return scan(html, null);
    }

    public String scan(String html, Map<String, String> params) {
        if (html == null) {
            throw new ScanException("input html can not be null");
        }
        if (html.length() > policy.maxInputSize) {
            throw new ScanException("input html reach max size: " + policy.maxInputSize);
        }

        if (this.policy.usePreXMLValid) {
            html = stripNonValidXMLCharacters(html);
        }

        FragmentXppParser parser = new FragmentXppParser();
        parser.setProperty(NAMES_ELEMS_PROPERTY, this.policy.elemsLower ? "lower" : "upper");
        parser.setProperty(NAMES_ATTRS_PROPERTY, this.policy.attrsLower ? "lower" : "upper");

        StringBuilder buffer = new StringBuilder(html.length() << 1);

        // 输出工具
        XssFilterDocumentHandler filter = new XssFilterDocumentHandler(buffer, policy, cssScanner, params);

        if (this.policy.usePurifier) {
            Purifier p = new Purifier();
            XMLDocumentFilter[] filters = { p, filter };
            parser.setProperty(FILTERS_PROPERTY, filters);
        } else {
            XMLDocumentFilter[] filters = { filter };
            parser.setProperty(FILTERS_PROPERTY, filters);
        }

        InputSource input = new InputSource(new StringReader(html));
        try {
            parser.parse(input);
        } catch (SAXException e) {
            throw new ScanException(e);
        } catch (IOException e) {
            throw new ScanException(e);
        }
        return buffer.toString();
    }

    /**
     * This method ensures that the output has only valid XML unicode characters as
     * specified by the XML 1.0 standard. For reference, please see <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty String if the input is
     * null or empty.
     * 
     * 定义XML允许出现的合法字符::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     */
    public String stripNonValidXMLCharacters(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(in.length() << 1);
        for (int i = 0; i < in.length(); ++i) {
            char ch = in.charAt(i);
            if (isAllow(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    private boolean isAllow(char c) {
        return (0x20 <= c && c <= 0xD7FF) || (0xE000 <= c && c <= 0xFFFD) || (0x10000 <= c && c <= 0x10FFFF) || c == 0x9 || c == 0xA || c == 0xD;
    }
}
