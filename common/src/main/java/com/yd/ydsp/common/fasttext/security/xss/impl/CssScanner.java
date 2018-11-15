package com.yd.ydsp.common.fasttext.security.xss.impl;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.batik.css.parser.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;

import com.yd.ydsp.common.fasttext.security.xss.Policy;
import com.yd.ydsp.common.fasttext.security.xss.ScanException;

public class CssScanner {

    private static final Log logger = LogFactory.getLog(CssScanner.class);

    private String removeSpecialStyles(String inputCss) {
        Matcher m = Pattern.compile("\\r|\\n|;", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(inputCss);
        StringBuilder sb = new StringBuilder(inputCss.length());
        int lastStart = 0;

        while (m.find()) {
            String currentStyle = inputCss.substring(lastStart, m.start()).trim();

            if (currentStyle.length() > 0 && !currentStyle.startsWith("-")) {
                sb.append(currentStyle).append(";");
            }

            lastStart = m.end();
        }

        String lastStyle = inputCss.substring(lastStart).trim();

        if (lastStyle.length() > 0 && !lastStyle.startsWith("-")) {
            sb.append(lastStyle).append(";");
        }

        return sb.toString();
    }

    public String scanStyleSheet(String taintedCss, Policy policy, int sizeLimit, boolean inline)
            throws ScanException {
        if (taintedCss == null) {
            throw new ScanException("taintedCss is null");
        }

        if (inline) {
            taintedCss = removeSpecialStyles(taintedCss);
        }

        Parser parser = new Parser();
        StringBuilder buffer = new StringBuilder(taintedCss.length() << 1);
        DocumentHandler handler = new CssDocumentHandler(buffer, policy, inline);

        parser.setDocumentHandler(handler);
        try {
            if (inline) {
                parser.parseStyleDeclaration(new InputSource(new StringReader(taintedCss)));
            } else {
                parser.parseStyleSheet(new InputSource(new StringReader(taintedCss)));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return buffer.toString();
    }
}
