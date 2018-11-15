package com.yd.ydsp.common.security.url;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CheckSafeUrl {

	private static final Log logger = LogFactory.getLog(CheckSafeUrl.class);

	public static final String DEFAULT_DOMAIN_URI = "resources/default-safe-domain.xml";
	public static final String DAILY_DEFAULT_DOMAIN_URI = "resources/daily-default-safe-domain.xml";
	public static final String USER_DOMAIN_URI = "safe-domain.xml";
	private static final String CONFIG_FLAG_URI = "safeurl.conf";

	// 配置文件父节点名称
	private final String SAFE_DOMAINS = "safedomains";
	private final String BLOCK_DOMAINS = "blockdomains"; // B2B
	private final String EXCLUDE_DOMAINS = "excludedomains"; // TAOBAO
	// 配置文件元素名称
	private final String ELEMENT_TAG = "domain";
	// 配置域名是否可以加通配符.
	private final String IS_STRICT = "strict";
	// safedomans的默认name, 即不写name属性时的值
	private final String DEFAULT = "";

	private Map<String, TreeNode> safemap;
	private Map<String, TreeNode> blockmap;
	private Map<String, TreeNode> excludemap;
	private static CheckSafeUrl inst = null;

	public static boolean productionMode = true;

	static {
		// add a config to control if use safe-domain list for daily or for
		// production env.
		// If the productionMode in safeurl.conf is false. It use for
		// daily-safe-domain list for while list.
		// else, it use for safe-domain list for while list.
		// by suddy
		try {
			InputStream is = CheckSafeUrl.class.getClassLoader()
					.getResourceAsStream(CONFIG_FLAG_URI);

			Properties config = new Properties();

			if (is != null) {
				config.load(is);
				is.close();
				if ((config.getProperty("productionMode") != null)
						&& ("false".equalsIgnoreCase(config.getProperty(
								"productionMode").trim()))) {
					productionMode = false;
					logger.warn("Use safe-domain config for Daily environment. Do not use this config for Product environment!");
				}
			}
		} catch (IOException e) {
			logger.info("Didn't config safe-domain for Daily environment. Use safe-domain config for Product environment.");
		}
	}

	/**
	 * 构建CheckUrlService
	 * 
	 * @throws ParseWhiteListException
	 *             解析白名单列表配置文件时出错
	 */
	private CheckSafeUrl(InputStream is) throws ParseWhiteListException {
		if (is == null) {
			logger.error("gived InputStream is null");
			throw new ParseWhiteListException("gived InputStream is null");
		}
		safemap = new HashMap<String, TreeNode>();
		blockmap = new HashMap<String, TreeNode>();
		excludemap = new HashMap<String, TreeNode>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document dom = null;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(is);
		} catch (ParserConfigurationException e) {
			logger.error("parse white list error", e);
			throw new ParseWhiteListException(e);
		} catch (SAXException e) {
			logger.error("parse white list error", e);
			throw new ParseWhiteListException(e);
		} catch (IOException e) {
			logger.error("read whitelist file error", e);
			throw new ParseWhiteListException(e);
		}

		Element root = dom.getDocumentElement();
		NodeList safedomainsNodes = root.getElementsByTagName(SAFE_DOMAINS);
		NodeList blockdomainsNodes = root.getElementsByTagName(BLOCK_DOMAINS);
		NodeList excludedomainsNodes = root
				.getElementsByTagName(EXCLUDE_DOMAINS);
		// safedomains
		
		for (int k = 0; k < safedomainsNodes.getLength(); k++) {
			Element safedomainsNode = (Element) safedomainsNodes.item(k);
			String id = safedomainsNode.getAttribute("name");

			TreeNode node = safemap.get(id);
			if (node == null) {
				node = new TreeNode();
				safemap.put(id, node);
			}

			NodeList domainNodes = safedomainsNode
					.getElementsByTagName(ELEMENT_TAG);
			for (int i = 0; i < domainNodes.getLength(); i++) {
				Element element = (Element) domainNodes.item(i);
				String value = element.getTextContent();
				String strict = element.getAttribute(IS_STRICT);

				insert(value.toCharArray(), node);
				if ("true".equalsIgnoreCase(strict)) {
					continue;
				}
				insert(("*." + value).toCharArray(), node);
			}
		}
		for (int k = 0; k < blockdomainsNodes.getLength(); k++) {
			Element blockdomainsNode = (Element) blockdomainsNodes.item(k);
			String id = blockdomainsNode.getAttribute("name");

			TreeNode node = blockmap.get(id);
			if (node == null) {
				node = new TreeNode();
				blockmap.put(id, node);
			}

			NodeList blockNodes = blockdomainsNode
					.getElementsByTagName(ELEMENT_TAG);
			for (int i = 0; i < blockNodes.getLength(); i++) {
				Element element = (Element) blockNodes.item(i);
				String value = element.getTextContent();
				String strict = element.getAttribute(IS_STRICT);
				insert(value.toCharArray(), node);
				if ("true".equalsIgnoreCase(strict)) {
					continue;
				}
				insert(("*." + value).toCharArray(), node);
			}
		}

		for (int k = 0; k < excludedomainsNodes.getLength(); k++) {
			Element excludedomainsNode = (Element) excludedomainsNodes.item(k);
			String id = excludedomainsNode.getAttribute("name");

			TreeNode node = excludemap.get(id);
			if (node == null) {
				node = new TreeNode();
				excludemap.put(id, node);
			}

			NodeList excludeNodes = excludedomainsNode
					.getElementsByTagName(ELEMENT_TAG);
			for (int i = 0; i < excludeNodes.getLength(); i++) {
				Element element = (Element) excludeNodes.item(i);
				String value = element.getTextContent();
				String strict = element.getAttribute(IS_STRICT);
				insert(value.toCharArray(), node);
				if ("true".equalsIgnoreCase(strict)) {
					continue;
				}
				insert(("*." + value).toCharArray(), node);
			}
		}

	}

	/**
	 * 使用默认的白名单构建CheckUrlService
	 * 
	 * @throws ParseWhiteListException
	 *             解析白名单列表配置文件时出错
	 */
	public static CheckSafeUrl getDefaultInstance() throws ParseWhiteListException{
		if (inst == null) {
			synchronized (CheckSafeUrl.class) {
				if (inst == null) {

					InputStream is = null;
					try {
						try {
							is = CheckSafeUrl.class.getClassLoader().getResourceAsStream(USER_DOMAIN_URI);
						} catch (Exception e) {
							; // try load user domain list
						}
						if (is == null) {
							if (productionMode) {
								is = CheckSafeUrl.class
										.getResourceAsStream(DEFAULT_DOMAIN_URI);
							} else {
								is = CheckSafeUrl.class
										.getResourceAsStream(DAILY_DEFAULT_DOMAIN_URI);
							}
						}
						inst = new CheckSafeUrl(is);
					} catch (Exception e) {
						throw new  ParseWhiteListException(
								"can not read safe domain list");
					} finally {
						try {
							if (is != null) {
								is.close();
							}
						} catch (IOException e) {/* nothing */
							;
						}
					}
				}
			}
		}
		return inst;
	}

	/**
	 * 使用自定义的白名单构建CheckUrlService
	 * 
	 * @throws ParseWhiteListException
	 *             解析白名单列表配置文件时出错
	 */
	public static CheckSafeUrl getCustomerInstance(InputStream is)
			throws ParseWhiteListException {
		return new CheckSafeUrl(is);
	}

	/**
	 * 判断URL是否在白名单中，若待检测的URL已经是host了，则建议将<code>isHost</code>设为true，这样会极大提高检测性能
	 * 
	 * @param url
	 *            待检测的url
	 * @param isHost
	 *            是否已经是Host
	 * @param id
	 *            白名单列表类型, 对应url配置配置文件中safedomans的name属性.
	 * @return
	 */

	private static String[] blockStart = { "/\\", "/\r", "/\n" };

	public boolean inWhiteList(String url, boolean isHost, String id) {
		if (url == null) {
			return false;
		}
//		try {
//			url = URLDecoder.decode(url, "UTF-8");
//		} catch (Exception e) {
//			;// nothing}
//
//		}
		// 支持webx 中的内部转跳. 以/开头，但不是"//"说明省略了内部域名，直接返回true
		if (url.startsWith("/") && !url.startsWith("//")) {
			{
				url = url.toUpperCase();
				for (String bs : blockStart) {
					if (url.startsWith(bs)) {
						return false;
					}
				}
			}

			return true;
		}

		TreeNode node = safemap.get(id);
		if (node == null) {
			return false;
		}
		String hosts = null;
		if (isHost) {
			hosts = url.toLowerCase();
		} else {
			try {
				hosts = getHost(url).toLowerCase();
			} catch (URISyntaxException e) {
				return false;
			}
		}

		return search(hosts, node) && checkHosts(hosts);
	}

	public boolean inWhiteListDropBlock(String url, boolean isHost, String id) {
		if (url == null) {
			return false;
		}
//		try {
//			url = URLDecoder.decode(url, "UTF-8");
//		} catch (Exception e) {
//			;// nothing}
//
//		}

		TreeNode node = blockmap.get(id);
		if (node != null) { // block可能为null
			String hosts = null;
			if (isHost) {
				hosts = url.toLowerCase();
			} else {
				try {
					hosts = getHost(url).toLowerCase();
				} catch (URISyntaxException e) {
					return false;// 非法host，肯定不是白名单，应该返回false
				}
			}
			if (hosts != null) {
				if (search(hosts, node) && checkHosts(hosts)) {
					return false; // 只有发现在block中才返回false;
				}
			}
			// XXX 从理论上说，如果一个url或域名中提取域名为null,则域名不合法，视为黑名单
			else {
				return false;
			}
		}
		// 支持webx 中的内部转跳. 以/开头，说明省略了内部域名，直接返回true
		if (url.startsWith("/") && !url.startsWith("//")) {
			{
				url = url.toUpperCase();
				for (String bs : blockStart) {
					if (url.startsWith(bs)) {
						return false;
					}
				}
			}

			return true;
		}
		node = safemap.get(id);
		if (node == null) {
			return false;
		}
		String hosts = null;
		if (isHost) {
			hosts = url.toLowerCase();
		} else {
			try {
				hosts = getHost(url).toLowerCase();
			} catch (URISyntaxException e) {
				return false;
			}
		}

		return search(hosts, node) && checkHosts(hosts);
	}

	public boolean inWhiteListDropExclude(String url, boolean isHost, String id) {
		if (url == null) {
			return false;
		}
//		try {
//			url = URLDecoder.decode(url, "UTF-8");
//		} catch (Exception e) {
//			;// nothing}
//
//		}

		TreeNode node = excludemap.get(id);
		if (node != null) { // block可能为null
			String hosts = null;
			if (isHost) {
				hosts = url.toLowerCase();
			} else {
				try {
					hosts = getHost(url).toLowerCase();
				} catch (URISyntaxException e) {
					return false;// 非法host，肯定不是白名单，应该返回false
				}
			}
			if (hosts != null) {
				if (search(hosts, node) && checkHosts(hosts)) {
					return false; // 只有发现在exclud中才返回false;
				}
			}
			// XXX 从理论上说，如果一个url或域名中提取域名为null,则域名不合法，视为黑名单
			else {
				return false;
			}
		}
		// 支持webx 中的内部转跳. 以/开头，说明省略了内部域名，直接返回true
		if (url.startsWith("/") && !url.startsWith("//")) {
			{
				url = url.toUpperCase();
				for (String bs : blockStart) {
					if (url.startsWith(bs)) {
						return false;
					}
				}
			}

			return true;
		}
		node = safemap.get(id);
		if (node == null) {
			return false;
		}
		String hosts = null;
		if (isHost) {
			hosts = url.toLowerCase();
		} else {
			try {
				hosts = getHost(url).toLowerCase();
			} catch (URISyntaxException e) {
				return false;
			}
		}

		return search(hosts, node) && checkHosts(hosts);
	}

	/**
	 * 白名单统一，不能修改，但每个业务要排除的名单可以设置
	 * @param blockmap
	 */
	public void setBlocks(Map<String, TreeNode> blockmap){
		if(blockmap != null){
			this.blockmap = blockmap;
		}
	}
	
	/**
	 * 白名单统一，不能修改，但每个业务要排除的名单可以设置
	 */
	public void setExcludes(Map<String, TreeNode> excludemap){
		if(excludemap != null){
			this.excludemap = excludemap;
		}
	}
	
	private boolean checkHosts(String host) {
		char[] chs = host.toCharArray();

		for (int i = 0; i < chs.length; i++) {
			if (!((chs[i] >= 'a' && chs[i] <= 'z')
					|| (chs[i] >= 'A' && chs[i] <= 'Z')
					|| (chs[i] >= '0' && chs[i] <= '9') || chs[i] == '-'
					|| chs[i] == '.' || chs[i] == '_')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断URL是否在白名单中，URL必须符合<code>URL</code>规范 <a
	 * href="hfap://www.ietf.org/rfc/rfc2396.txt""><i>RFC&nbsp;2396: Uniform
	 * Resource Identifiers (URI): Generic Syntax</i></a> 协议头是必须的，比如：
	 * <ol>
	 * <li>hfap://www.taobao.com</li>
	 * <li>hfaps://test.taobao.com:8080</li>
	 * <li>hfaps://test.taobao.com:8080/test/query?para=test</li>
	 * </ol>
	 * 若只是判断Host是否在白名单中，请直接调用{@link #inWhiteList(String, boolean)}，比如:
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

	public boolean inWhiteListDropBlock(String url) {
		return inWhiteListDropBlock(url, false, DEFAULT);
	}

	/**
	 * 判断URL是否在白名单中
	 * 
	 * @param url
	 *            待检测的url
	 * @param id
	 *            白名单列表类型
	 * @return
	 */
	public boolean inWhiteList(String url, String id) {
		return inWhiteList(url, false, id);
	}

	public boolean inWhiteListDropBlock(String url, String id) {
		return inWhiteListDropBlock(url, false, id);
	}
	
	public boolean inWhiteListDropExclude(String url, String id) {
		return inWhiteListDropExclude(url, false, id);
	}

	/**
	 * 判断URL是否在白名单中，若待检测的URL已经是host了，则建议将<code>isHost</code>设为true，这样会极大提高检测性能
	 * 
	 * @param url
	 *            待检测的url
	 * @param isHost
	 *            是否已经是Host
	 * @return
	 */
	public boolean inWhiteList(String url, boolean isHost) {
		return inWhiteList(url, isHost, DEFAULT);
	}

	public boolean inWhiteListDropBlock(String url, boolean isHost) {
		return inWhiteListDropBlock(url, isHost, DEFAULT);
	}
	
	public boolean inWhiteListDropExclude(String url, boolean isHost) {
		return inWhiteListDropExclude(url, isHost, DEFAULT);
	}

	private static String getHost(String url) throws URISyntaxException {
		url = SafeURLHost.getHostFormURL(url);
		if (url == null)
			return "";
		String host = new URI(url).getHost();
		if (host == null)
			host = "";
		return host;
	}

	public boolean insert(char[] host, TreeNode node) {
		int index = host.length - 1;
		int last;
		int pos;
		TreeNode fa = node;
		TreeNode son = null;

		for (; index >= 0; index--) {
			last = (int) host[index];
			if (last > 96 && last < 123) {
				// a-z:97-122 index:0-25
				pos = last - 97;
			} else if (last > 47 && last < 58) {
				// 0-9:48-57 index:26-35
				pos = last - 22;
			} else if (last == 46) {
				// .:46 index:36
				pos = last - 10;
			} else if (last == 45) {
				// -:45 index:37
				pos = last - 8;
			} else if (last == 95) {
				// _:95 index:38
				pos = last - 57;
			} else if (last == 42) {
				fa.anyCharacter = true;
				return true;
			} else {
				return false;
			}

			son = fa.next[pos];
			if (son == null) {
				son = new TreeNode();
				fa.next[pos] = son;
			}
			fa = son;
		}
		fa.terminal = true;
		return true;

	}

	public boolean search(String url, TreeNode node) {
		char[] host = url.toCharArray();

		int index = host.length - 1;
		if (index < 0) {
			return false;
		}
		int last;
		int pos;

		for (; index >= 0; index--) {
			last = (int) host[index];
			if (last > 96 && last < 123) {
				// a-z:97-122 index:0-25
				pos = last - 97;
			} else if (last > 47 && last < 58) {
				// 0-9:48-57 index:26-35
				pos = last - 22;
			} else if (last == 46) {
				// .:46 index:36
				pos = last - 10;
			} else if (last == 45) {
				// -:45 index:37
				pos = last - 8;
			} else if (last == 95) {
				// _:95 index:38
				pos = last - 57;
			} else {
				return false;
			}

			node = node.next[pos];

			if (node == null) {
				return false;
			}
			if (node.anyCharacter && index > 0) {
				return true;
			}
		}
		if (node.terminal) {
			return true;
		} else {
			return false;
		}
	}

	public static class TreeNode {

		private static final int capacity = 39;

		public TreeNode[] next;
		public boolean anyCharacter;
		public boolean terminal;

		public TreeNode() {
			next = new TreeNode[capacity];
		}
	}
}
