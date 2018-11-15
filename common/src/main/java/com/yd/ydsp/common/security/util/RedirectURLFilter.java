package com.yd.ydsp.common.security.util;

import com.yd.ydsp.common.security.url.CheckRedirectUrlService;
import com.yd.ydsp.common.security.url.ParseWhiteListException;

import java.io.InputStream;

public class RedirectURLFilter{

	// User safe domain for this application
	// If it's not find, use default safe domain for parse
	private static final String    DOMAIN_URI = "redirect-safe-domain.xml";
	private static CheckRedirectUrlService checkUrl = null;

	static {
		try {
			InputStream is = RedirectURLFilter.class.getClassLoader().getResourceAsStream(DOMAIN_URI);

			if( is != null){
				checkUrl = CheckRedirectUrlService.getCustomerInstance(is);
			}else{
				checkUrl = CheckRedirectUrlService.getDefaultInstance();
			}
		} catch (ParseWhiteListException e) {
			System.out.println("load white-url-list-config failed");
			throw new RuntimeException(e);
		}
	}
	public static boolean isAllowed(String url){
		try{
			return checkUrl.inWhiteList(url);
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * @param url
	 * @return if path is safe from alibaba crop, return path, else return null
	 */
	public static String parseURL(String url){
		try {
			if(!checkAt(url)){
				return null;
			}

			if(checkUrl.inWhiteList(url)){
				return url;
			}else{
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	// for old interface
	public static String paserURL(String url){
		return RedirectURLFilter.parseURL(url);
	}

	/**
	 * 判断url的域名中是否包含@字符，若有@，则返回空；
	 * @param url
	 * @return
	 */
	private static boolean checkAt(String url){
		int urllen=url.length();
		int fn=url.indexOf('/');

		if(fn == -1){ //判断url中是否包含‘/’
			return false;
		} else{
			if(fn<urllen-2 &&(url.charAt(fn)=='/') &&(url.charAt(fn+1)=='/')){ //判断是否包含'//'，并判断是否会越界

				int fr= url.indexOf('/',fn+2); //以'/'作为域名分隔符，拿到域名

				if(fr==-1){//无‘/’
					int fw = url.indexOf('?',fn+2); //以'?'作为域名分隔符，拿到域名
					if(fw >-1){
						String sub1=  url.substring(fn+2,fw); ////以'?'作为域名分隔符，拿到域名
						if(sub1.indexOf('@')>-1){
							return false;
						}else{
							return true;
						}
					}else{
						String sub1=  url.substring(fn+2,urllen); // 无?,无/，以url整体作为域名，潘达un@
						if(sub1.indexOf('@')>-1){
							return false;
						}else{
							return true;
						}
					}
				}else{//有‘/’,以'/'来进行域名分割
					String sub1=  url.substring(fn+2,fr);
					if(sub1.indexOf('@')>-1){
						return false;
					}else{
						return true;
					}
				}
			}else{
				return false;
			}


		}

	}
}
