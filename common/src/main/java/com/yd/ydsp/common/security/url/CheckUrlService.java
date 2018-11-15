package com.yd.ydsp.common.security.url;

import java.io.InputStream;

public class CheckUrlService {

	private static final String DEFAULT = "";
	private CheckSafeUrl superInst;
	private static CheckUrlService inst = null;

	public static CheckUrlService getDefaultInstance()
			throws ParseWhiteListException {
		if (inst == null) {
			synchronized (CheckUrlService.class) {
				if (inst == null) {
					try {
						inst = new CheckUrlService(
								CheckSafeUrl.getDefaultInstance());
					} catch (ParseWhiteListException e) {
						throw new ParseWhiteListException(e);
					}
				}
			}
		}
		return inst;
	}

	public static CheckUrlService getCustomerInstance(InputStream is)
			throws ParseWhiteListException {
		try {

			return new CheckUrlService(CheckSafeUrl.getCustomerInstance(is));
		} catch (Exception e) {
			throw new ParseWhiteListException(e);
		}
	}

	private CheckUrlService(CheckSafeUrl superInst)
			throws ParseWhiteListException {
		this.superInst = superInst;
	}

	/**
	 * 判断URL是否在白名单中，若待检测的URL已经是host了，则建议将<code>isHost</code>设为true，这样会极大提高检测性能
	 *
	 * @param url
	 *            待检测的url
	 * @param isHost
	 *            是否已经是Host
	 * @param id
	 *            白名单列表类型
	 * @return
	 */

	public boolean inWhiteList(String url, boolean isHost, String id) {
		return superInst.inWhiteListDropExclude(url, isHost, id);
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

}
