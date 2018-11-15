package com.yd.ydsp.common.security.util;

import com.yd.ydsp.common.fasttext.codec.HtmlFastEntities;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * SecurityUtil 包含安全工具包中大部分的安全方法。<br />
 * 提供对富文本，JAVASCRIPT，URL等的安全判断。
 * @author 铁花  毕玄 玄霄  沈冲  KJ suddy
 *
 */
public class SecurityUtil extends StringEscapeUtils{
	//public static final char RICHTEXT_KEYCHAR = '¤';


	/**
	 * 对输入的String进行JAVASCRIPT 转义<br />
	 * 返回JAVASCRIPT 转义后的字符串，其中'被转义成\' 其他类似。<br />
	 * 用于JAVASCRIPT环境中的参数预处理<br />
	 * 如：<br />
	 * 		&lt;script&gt;var a = &quot;$SecurityUtil.jsEncode($var)&quot;;&lt;script&gt;<br />
	 * 		&lt;img src=&quot;http://www.taobao.com/logi.jpg&quot; onClick=&quot;check($SecurityUtil.jsEncode($var))&quot; /&gt;
	 *
	 * @param s -- 输入的待转义的字符串
	 * @return JAVASCRIPT转义后的字符串
	 */
	public static String jsEncode(String s) {
		if (s != null){
			return escapeJavaScript(s);
		}else{
			return null;
		}
	}

	/**
	 * 输入Object 先做toString()处理。然后进行JAVASCRIPT 转义<br />
	 * 返回JAVASCRIPT 转义后的字符串，其中'被转义成\' 其他类似。<br />
	 * 用于JAVASCRIPT环境中的参数预处理<br />
	 * 如：<br />
	 * 		&lt;script&gt;var a = &quot;$SecurityUtil.jsEncode($var)&quot;;&lt;script&gt;<br />
	 * 		&lt;img src=&quot;http://www.taobao.com/logi.jpg&quot; onClick=&quot;check($SecurityUtil.jsEncode($var))&quot; /&gt;
	 *
	 * @param s -- 输入的待转义的对象
	 * @return JAVASCRIPT转义后的字符串
	 */
	public static String jsEncode(Object s){
		if(s!=null){
			return jsEncode(s.toString());
		}else{
			return null;
		}
	}

	/**
	 * 输入的String进行富文本过滤<br />
     * 移除其中的恶意标签和脚本信息，保留安全的HTML TAG。<br />
	 *
	 * @param str -- 需要输出为富文本的字符串
	 * @return 移除输入字符串中的恶意代码后安全的富文本字符串
	 */
	public static String richtext(String str) {
		if (str != null){
			return RichTextXssFilter.filter(str);
			//return RICHTEXT_KEYCHAR + RichTextXssFilter.filter(str);
		}else{
			return null;
		}
	}

	/**
	 * 输入Object 先做toString()处理，然后进行富文本过滤<br />
     * 移除其中的恶意标签和脚本信息，保留安全的HTML TAG。<br />
	 *
	 * @param str -- 需要输出为富文本的Object
	 * @return 移除Object中的恶意代码后安全的富文本字符串
	 */
	public static String richtext(Object str) {
		if (str != null){
			return richtext(str.toString());
		}else{
			return null;
		}
	}

	/**
	 * 提供一种例外机制，允许不做任何安全处理直接输出。<br />
	 * 该方法会绕过所有的安全机制，不到万不得已请不要使用
	 *
	 * @param str -- 输入不需要任何处理 的字符串
	 * @return 原样返回输入字符串
	 */
	public static String ignoretext(String str){
		if (str != null){
			return str;
		}else{
			return null;
		}
	}
	/**
	 * 提供一种例外机制，允许不做任何安全处理直接输出。<br />
	 * 该方法会绕过所有的安全机制，不到万不得已请不要使用
	 *
	 * @param str -- 输入不需要任何处理 的对象
	 * @return 原样返回输入对象
	 */
	public static Object ignoretext(Object str){
		if (str != null){
			return str;
		}else{
			return null;
		}
	}
	/**
	 * 检测是否为ALI集团的url，用于url跳转漏洞修复
	 *
	 * @param url -- 输入的待检测URL
	 * @return	如果是ALI集团的URL，则返回输入的url。<br />
	 * 			如果非ALI集团的URL，则返回NULL。
	 */
	public static String parseURL(String url){
		return URLFilter.parseURL(url);
	}

	/**
	 * 判断域名是否在跳转白名单redirect-safe-domain.xml，专用于url跳转漏洞修复
	 *
	 * @param url -- 输入的待检测URL
	 * @return	如果是ALI集团的URL，则返回输入的url。<br />
	 * 			如果非ALI集团的URL，则返回NULL。
	 */
	public static String parseRedirectUrl(String url){
		return RedirectURLFilter.parseURL(url);
	}

	/**
	 * 不破坏JSON结构的escape函数，只对json结构中name和vaule做escapeHtml处理
	 *
	 * @param jsontext -- 输入的待转义的String jsontext
	 * @return	经过转义的JSON 字符串
	 */
	public static String escapeJson(String jsontext){
		if (jsontext != null)
		{
			return JsonTextXssFilter.jsonTextFilter(jsontext);
		}
		else{
			return null;
		}
	}


	/**
	 * 先对Object做toString()处理，然后进行escapeJson处理
	 *
	 * @param jsonobj -- 输入的待转义的Object jsonobj
	 * @return 经过转义的json 字符串
	 */
	public static String escapeJson(Object jsonobj){
		if (jsonobj != null){
			return escapeJson(jsonobj.toString());
		}
		else{
			return null;
		}

	}

	/**
	 * 不破坏JSON结构的escape函数，只对json结构中name和vaule做escapeHtml处理,对转义后的汉字，再转成unicode编码
	 *
	 * @param jsontext -- 输入的待转义的String jsontext
	 * @return	经过转义的JSON 字符串
	 */
	public static String escapeJsonForUnicode(String jsontext){
		StringBuilder rs = new StringBuilder();
		if (jsontext != null)
		{

			String inc_unic = escapeJson(jsontext);
			if(inc_unic != null){
			 	char[] chars = inc_unic.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					byte[] bytes = ("" + chars[i]).getBytes();

					if (bytes.length == 2) {
						int[] ints = new int[2];
						ints[0] = bytes[0] & 0xff;
						ints[1] = bytes[1] & 0xff;

						if (ints[0] >= 0x81 && ints[0] <= 0xFE &&
								ints[1] >= 0x40 && ints[1] <= 0xFE) {
							rs.append("\\u").append(Integer.toHexString(chars[i]));
						}
					}else{

						rs.append(chars[i]);
					}

				}

			}

			return rs.toString();
		}
		else{
			return null;
		}
	}


	/**
	 * 不破坏JSON结构的escape函数，只对json结构中name和vaule做escapeHtml处理<br />
	 *
	 * 返回结果进过jsEncode处理<br />
	 *
	 * @param jsontext -- 输入的待转义的jsontext
	 * @return	经过转义和jsEncode的json 字符串
	 */
	public static String escapeJsonForJsVar(String jsontext){
		if (jsontext != null)
		{
			return jsEncode(escapeJson(jsontext));
		}
		else{
			return null;
		}
	}


	/**
	 * 先对Object做toString()处理，然后进行escapeJsonForJs处理
	 *
	 * @param jsonobj -- 输入的待转义的Object jsonobj
	 * @return 经过转义和jsEncode的json 字符串
	 */
	public static String escapeJsonForJsVar(Object jsonobj){
		if (jsonobj != null){
			return escapeJsonForJsVar(jsonobj.toString());
		}
		else{
			return null;
		}

	}

	/**
	 * 对 ' " & < >进行html转义成html实体
	 * @param str 待转义变量
	 * @return  转义后变量
	 */
	public static String escapeHtml(String str){
		return HtmlFastEntities.HTML40.escape(str);
	}
	
}
