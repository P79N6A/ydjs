package com.yd.ydsp.common.security.util;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Descript: A security lib for call in other
 * 
 * @author suddy
 *
 */
public class SecurityUtilLib extends StringEscapeUtils {
	
	
	/**
	 * ����Object ����toString()����Ȼ�����JAVASCRIPT ת��<br />
	 * ����JAVASCRIPT ת�����ַ���������'��ת���\' �������ơ�<br />
	 * ����JAVASCRIPT�����еĲ���Ԥ����<br />
	 * �磺<br />
	 * 		&lt;script&gt;var a = &quot;$SecurityUtil.jsEncode($var)&quot;;&lt;script&gt;<br />
	 * 		&lt;img src=&quot;http://www.taobao.com/logi.jpg&quot; onClick=&quot;check($SecurityUtil.jsEncode($var))&quot; /&gt;
	 * 
	 * @param s -- ����Ĵ�ת��Ķ���
	 * @return 
	 */
	public static String jsEncode(Object s){
		if(s!=null){
			return SecurityUtilLib.escapeJavaScript(s.toString());
		}else{
			return null;
		}
	}

	/**
	 * @param str--��Ҫ���Ϊ���ı���Object
	 * @return �Ƴ�Object�еĶ�������ȫ�ĸ��ı��ַ���
	 */
	public static String richtext(Object str) {
		if (str != null){
			return RichTextXssFilter.filter(str.toString());
		}else{
			return null;
		}
	}
	
	/**
	 * �ṩһ��������ƣ��������κΰ�ȫ����ֱ�������<br />
	 * �÷������ƹ����еİ�ȫ���ƣ������򲻵����벻Ҫʹ��
	 * 
	 * @param str--���벻��Ҫ�κδ��� ���ַ���
	 * @return ԭ�����������ַ���
	 */
	public static String ignoretext(String str){
		if (str != null){
			return str;
		}else{
			return null;
		}
	}
	/**
	 * �ṩһ��������ƣ��������κΰ�ȫ����ֱ�������<br />
	 * �÷������ƹ����еİ�ȫ���ƣ������򲻵����벻Ҫʹ��
	 * 
	 * @param str -- ���벻��Ҫ�κδ��� �Ķ���
	 * @return ԭ�������������
	 */
	public static Object ignoretext(Object str){
		if (str != null){
			return str;
		}else{
			return null;
		}
	}
	
	/**
	 * ����Ƿ�ΪALI���ŵ�url������url��ת©���޸�
	 * 
	 * @param url--����Ĵ����URL
	 * @return	�����ALI���ŵ�URL���򷵻������url��<br />
	 * 			�����ALI���ŵ�URL���򷵻�NULL��
	 */
	public static String parseURL(String url){
		return URLFilter.parseURL(url);
	}
}
