package com.yd.ydsp.common.security.url;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CheckRedirectUrlService {

	private static final String DEFAULT = "";
	private static final String EXCLUDE_FILE = "resources/redirect-exclude-domains.xml";
	private static final String EXCLUDE_DOMAINS = "excludedomains";
	private static final String ELEMENT_TAG = "domain";
	private static final String IS_STRICT = "strict";
	private CheckSafeUrl superInst;
	private static CheckRedirectUrlService inst = null;

	private static Map<String, CheckSafeUrl.TreeNode> getExcludemap(InputStream is,
			CheckSafeUrl superInst) throws ParseWhiteListException {
		Map<String, CheckSafeUrl.TreeNode> excludemap = new HashMap<String, CheckSafeUrl.TreeNode>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document dom = null;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(is);
		} catch (Exception e) {
			throw new ParseWhiteListException(e);
		}

		Element root = dom.getDocumentElement();
		NodeList excludedomainsNodes = root
				.getElementsByTagName(EXCLUDE_DOMAINS);

		for (int k = 0; k < excludedomainsNodes.getLength(); k++) {
			Element excludedomainsNode = (Element) excludedomainsNodes.item(k);
			String id = excludedomainsNode.getAttribute("name");

			CheckSafeUrl.TreeNode node = excludemap.get(id);
			if (node == null) {
				node = new CheckSafeUrl.TreeNode();
				excludemap.put(id, node);
			}

			NodeList domainNodes = excludedomainsNode
					.getElementsByTagName(ELEMENT_TAG);
			for (int i = 0; i < domainNodes.getLength(); i++) {
				Element element = (Element) domainNodes.item(i);
				String value = element.getTextContent();
				String strict = element.getAttribute(IS_STRICT);

				superInst.insert(value.toCharArray(), node);
				if ("true".equalsIgnoreCase(strict)) {
					continue;
				}
				superInst.insert(("*." + value).toCharArray(), node);
			}
		}
		return excludemap;
	}

	public static CheckRedirectUrlService getDefaultInstance()
			throws ParseWhiteListException {
		if (inst == null) {
			synchronized (CheckUrlService.class) {
				if (inst == null) {
					try {
						InputStream in = CheckRedirectUrlService.class
								.getResourceAsStream(EXCLUDE_FILE);
						CheckSafeUrl superInst = CheckSafeUrl
								.getDefaultInstance();
						superInst.setExcludes(getExcludemap(in, superInst));
						inst = new CheckRedirectUrlService(superInst);
					} catch (Exception e) {
						throw new ParseWhiteListException(e);
					}
				}
			}
		}
		return inst;
	}

	public static CheckRedirectUrlService getCustomerInstance(InputStream is)
			throws ParseWhiteListException {
		try {
			return new CheckRedirectUrlService(
					CheckSafeUrl.getCustomerInstance(is));
		} catch (Exception e) {
			throw new ParseWhiteListException(e);
		}
	}

	private CheckRedirectUrlService(CheckSafeUrl superInst)
			throws ParseWhiteListException {
		this.superInst = superInst;
	}

	/**
	 * �ж�URL�Ƿ��ڰ������У���������URL�Ѿ���host�ˣ����齫<code>isHost</code>��Ϊtrue�������Ἣ����߼������
	 * 
	 * @param url
	 *            ������url
	 * @param isHost
	 *            �Ƿ��Ѿ���Host
	 * @param id
	 *            �������б�����
	 * @return
	 */

	public boolean inWhiteList(String url, boolean isHost, String id) {
		return superInst.inWhiteListDropExclude(url, isHost, id);
	}

	/**
	 * �ж�URL�Ƿ��ڰ������У�URL�������<code>URL</code>�淶 <a
	 * href="hfap://www.ietf.org/rfc/rfc2396.txt""><i>RFC&nbsp;2396: Uniform
	 * Resource Identifiers (URI): Generic Syntax</i></a> Э��ͷ�Ǳ���ģ����磺
	 * <ol>
	 * <li>hfap://www.taobao.com</li>
	 * <li>hfaps://test.taobao.com:8080</li>
	 * <li>hfaps://test.taobao.com:8080/test/query?para=test</li>
	 * </ol>
	 * ��ֻ���ж�Host�Ƿ��ڰ������У���ֱ�ӵ���{@link #inWhiteList(String, boolean)}������:
	 * <ol>
	 * <li>www.taobao.com</li>
	 * <li>test.taobao.com</li>
	 * <li>taobao.com</li>
	 * </ol>
	 * 
	 * @param url
	 * @return
	 */
	public boolean inWhiteList(String url) {
		return inWhiteList(url, false, DEFAULT);
	}

	/**
	 * �ж�URL�Ƿ��ڰ�������
	 * 
	 * @param url
	 *            ������url
	 * @param id
	 *            �������б�����
	 * @return
	 */
	public boolean inWhiteList(String url, String id) {
		return inWhiteList(url, false, id);
	}

	/**
	 * �ж�URL�Ƿ��ڰ������У���������URL�Ѿ���host�ˣ����齫<code>isHost</code>��Ϊtrue�������Ἣ����߼������
	 * 
	 * @param url
	 *            ������url
	 * @param isHost
	 *            �Ƿ��Ѿ���Host
	 * @return
	 */
	public boolean inWhiteList(String url, boolean isHost) {
		return inWhiteList(url, isHost, DEFAULT);
	}

}
