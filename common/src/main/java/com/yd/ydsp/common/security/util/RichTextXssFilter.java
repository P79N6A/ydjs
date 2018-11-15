package com.yd.ydsp.common.security.util;

import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.fasttext.security.xss.Policy;
import com.yd.ydsp.common.fasttext.security.xss.PolicyException;
import com.yd.ydsp.common.fasttext.security.xss.XssXppScanner;

import java.io.InputStream;

public class RichTextXssFilter {
	
	private static final String POLICY_FILE_NAME = "taobao-xss.xml";

	private static XssXppScanner scanner = null;
	static {
		Policy p;
		try {
			InputStream inputStream = RichTextXssFilter.class.getClassLoader().getResourceAsStream(POLICY_FILE_NAME);
			p = Policy.getCustomerPolicyInstance(inputStream);
			scanner = new XssXppScanner(p);
		} catch (PolicyException e) {
			throw new RuntimeException(e);
		}
	}

	public static String filter(String html) {

		if (StringUtil.isBlank(html)) {
			return html;
		}
		try {
			return scanner.scan(html);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}

	}
	

}
