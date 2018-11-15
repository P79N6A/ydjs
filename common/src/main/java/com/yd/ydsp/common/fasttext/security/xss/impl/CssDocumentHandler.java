package com.yd.ydsp.common.fasttext.security.xss.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

import com.yd.ydsp.common.fasttext.security.xss.CssProcessHandler;
import com.yd.ydsp.common.fasttext.security.xss.Policy;

public class CssDocumentHandler implements DocumentHandler {
    private static final Log logger = LogFactory.getLog(CssDocumentHandler.class);

    // remove these attribute
    //private static final String  ABSOLUTE_PROP_VALUE = "(absolute)";
    //private static final String  RELATIVE_PROP_VALUE = "(relative)";
    private static final String  EXPRESSION_VALUE    = "(expression)";
    private static final String  OCTALCHAR           = "(\\\\[0-9a-zA-Z]{1,2})";
    // ////////in style///
    //private static String        ZINDEX_PROP         = "z-index";

    private StringBuilder        styleSheet          = null;
    private boolean              selectorOpen        = true;
    private boolean              inlineStyle         = false;
    private Policy               policy               = null;

    private static final Pattern FORBIDDEN_VALUE_PATTERN       = Pattern.compile(/*ABSOLUTE_PROP_VALUE + "|"
                                                             + RELATIVE_PROP_VALUE + "|"
                                                             + */EXPRESSION_VALUE + "|" + OCTALCHAR);

    public CssDocumentHandler(StringBuilder buffer, Policy policy, boolean inlineStyle) {
        this.styleSheet = buffer;
        this.inlineStyle = inlineStyle;
        this.policy = policy;
    }

    public void comment(String text) throws CSSException {}

    public void endDocument(InputSource source) throws CSSException {}

    public void endFontFace() throws CSSException {}

    public void endMedia(SACMediaList media) throws CSSException {}

    public void endPage(String name, String pseudo_page) throws CSSException {}

    public void ignorableAtRule(String atRule) throws CSSException {}

    public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI)
            throws CSSException {}

    public void namespaceDeclaration(String prefix, String uri) throws CSSException {}

    public void property(String name, LexicalUnit value, boolean important) throws CSSException {
        if (!selectorOpen && inlineStyle) {
            return;
        }

        if (name != null && !policy.cssIgnoredStyles.contains(name.trim())) {
            styleSheet.append(name);
            styleSheet.append(':');

            while (value != null) {
                styleSheet.append(' ');
                styleSheet.append(lexicalValueToString(value));
                value = value.getNextLexicalUnit();
            }
            styleSheet.append(';');
        }
        //if (!ZINDEX_PROP.equals(name)) { // TODO: z-index will ignored, why?
        //}
    }

    public void startDocument(InputSource source) throws CSSException {}

    public void startFontFace() throws CSSException {}

    public void startMedia(SACMediaList media) throws CSSException {}

    public void startPage(String name, String pseudo_page) throws CSSException {}

    public void startSelector(SelectorList selectors) throws CSSException {
        int selectorCount = 0;

        for (int i = 0; i < selectors.getLength(); i++) {
            Selector selector = selectors.item(i);

            if (selector != null) {
                String selectorName = selector.toString();
                if (selectorCount > 0) {
                    styleSheet.append(',');
                    styleSheet.append(' ');
                }
                styleSheet.append(selectorName);
                selectorCount++;
            }
        }

        if (selectorCount > 0) {
            styleSheet.append(' ');
            styleSheet.append('{');
            selectorOpen = true;
        }
    }

    public void endSelector(SelectorList selectors) throws CSSException {
        if (selectorOpen) {
            styleSheet.append('}');
            styleSheet.append('\n');
        }

        selectorOpen = false;
    }

    private CssProcessHandler cssHandler = null;

    private String lexicalValueToString(LexicalUnit lu) {
        String returnValue = null;

        if (policy.cssProcessHandler != null) {
            if (cssHandler == null) {
                try {
                    cssHandler = (CssProcessHandler) Class.forName(policy.cssProcessHandler).newInstance();
                } catch (InstantiationException e) {
                    logger.info("error:" + e.getMessage());
                } catch (IllegalAccessException e) {
                    logger.info("error:" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    logger.info("error:" + e.getMessage());
                }
            }

            if (cssHandler != null) {
                returnValue = cssHandler.printCssText(lu, policy.getCommonRegex());
            }
        }

        if (returnValue != null) {
            return returnValue;
        }

        switch (lu.getLexicalUnitType()) {
            case LexicalUnit.SAC_PERCENTAGE:
            case LexicalUnit.SAC_DIMENSION:
            case LexicalUnit.SAC_EM:
            case LexicalUnit.SAC_EX:
            case LexicalUnit.SAC_PIXEL:
            case LexicalUnit.SAC_INCH:
            case LexicalUnit.SAC_CENTIMETER:
            case LexicalUnit.SAC_MILLIMETER:
            case LexicalUnit.SAC_POINT:
            case LexicalUnit.SAC_PICA:
            case LexicalUnit.SAC_DEGREE:
            case LexicalUnit.SAC_GRADIAN:
            case LexicalUnit.SAC_RADIAN:
            case LexicalUnit.SAC_MILLISECOND:
            case LexicalUnit.SAC_SECOND:
            case LexicalUnit.SAC_HERTZ:
            case LexicalUnit.SAC_KILOHERTZ:
                // these are all measurements
                return lu.getFloatValue() + lu.getDimensionUnitText();
            case LexicalUnit.SAC_INTEGER:
                // just a number
                return String.valueOf(lu.getIntegerValue());
            case LexicalUnit.SAC_REAL:
                // just a number
                return String.valueOf(lu.getFloatValue());
            case LexicalUnit.SAC_STRING_VALUE:
            case LexicalUnit.SAC_IDENT:
                // just a string/identifier
                String value = lu.getStringValue();
                if (FORBIDDEN_VALUE_PATTERN.matcher(value).find()) {
                    value = "";
                }
                return value;
            case LexicalUnit.SAC_URI:
                return "url()";
            case LexicalUnit.SAC_RGBCOLOR:
                // this is a rgb encoded color
                int color = 0;
                LexicalUnit param = lu.getParameters();
                color |= param.getIntegerValue() << 16;
                param = param.getNextLexicalUnit(); // comma
                param = param.getNextLexicalUnit(); // G value
                color |= param.getIntegerValue() << 8;
                param = param.getNextLexicalUnit(); // comma
                param = param.getNextLexicalUnit(); // B value
                color |= param.getIntegerValue();
                color = color & 0xFFFFFF;
                String val = Integer.toHexString(color);
                if (val.length() >= 6) {
                    return "#" + val;
                }
                return "#000000".substring(0, 7 - val.length()) + val;
            case LexicalUnit.SAC_INHERIT:
                // constant
                return "inherit";
            case LexicalUnit.SAC_OPERATOR_COMMA:
                return ",";
            case LexicalUnit.SAC_ATTR:
            case LexicalUnit.SAC_COUNTER_FUNCTION:
            case LexicalUnit.SAC_COUNTERS_FUNCTION:
            case LexicalUnit.SAC_FUNCTION:
            case LexicalUnit.SAC_RECT_FUNCTION:
            case LexicalUnit.SAC_SUB_EXPRESSION:
            case LexicalUnit.SAC_UNICODERANGE:
            default:
                // these are properties that shouldn't be necessary for most run
                // of the mill HTML/CSS
                return " ";
        }
    }
}
