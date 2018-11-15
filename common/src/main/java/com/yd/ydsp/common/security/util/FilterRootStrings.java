package com.yd.ydsp.common.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 描述：过滤不处理的RootString
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class FilterRootStrings {

	private static final String DEFAULT_FILTERFILE="xssVmFilter.properties";

	private static final String DEFAULT_TEMPLATEFILTERFILE="xssFileFilter.properties";

	private static List filters=new ArrayList();

	private static List filterFiles=new ArrayList();

	static{
		readFile(DEFAULT_FILTERFILE,filters);
		readFile(DEFAULT_TEMPLATEFILTERFILE,filterFiles);
	}

	/**
	 * 是否需要过滤对此RootString的处理
	 */
	public static boolean isFilter(String rootString){
		return filters.contains(rootString.trim().toLowerCase());
	}

	/**
	 * 判断文件是否需要过滤
	 */
	public static boolean isFilterFile(String file){
		return filterFiles.contains(file.trim().toLowerCase());
	}

	private static void readFile(String fileName,List filterValues){
		InputStream is=FilterRootStrings.class.getClassLoader().getResourceAsStream(fileName);
		if(is!=null){
			Properties props=new Properties();
			try {
				props.load(is);
			}
			catch (IOException e) {
				// IGNORE
			}
			finally{
				try {
					if(is!=null)
						is.close();
				}
				catch (IOException e) {
					// IGNORE
				}
			}
			for (Iterator iterator = props.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String filterRootStrings=(String) props.get(key);
				String[] stringsArray=filterRootStrings.split(",");
				for (int i = 0; i < stringsArray.length; i++) {
					filterValues.add(stringsArray[i].toLowerCase());
				}
			}
		}
		else{
			if(!fileName.equals(DEFAULT_FILTERFILE)){
				return;
			}
			filters.add("control");
			filters.add("field");
			filters.add("screen_placeholder");
			filters.add("cmstool");
			filters.add("cmscontrol");
			filters.add("categories");
			filters.add("esitool");
			filters.add("cats");
			filters.add("resultcode");
			filters.add("stringescapeutil");
			filters.add("tbstringutil");
		}
	}

}
