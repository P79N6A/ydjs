package com.yd.ydsp.common.fasttext.extract;

import java.util.List;
/**
 * 用户自定义code table 加载器，用于需要定制某些特殊字符过滤和转换的情况下。
 * @author guolin.zhuanggl
 *
 */
public interface CharTableLoader {
	/**
	 * 加载需要过滤的符号表，每行字符有","隔开，形如   "UFE66,﹦,﹦"   ,对于symbol来说，后面两个字符是一样的。
	 * 注意，对于",","\r","\n"特殊处理了，只要unicode码即可。
	 * @return 需要过滤的符号列表，格式如上
	 */
	List<String> loadSymbolTable();
	/**
	 * 加载需要转换的同形字列表，形如"U3280,(一),1"   ,unicode代码为需要转换的字符编码，后面的字符为转换后的字符。
	 * @return 需要转换的字符列表。
	 */
	List<String> loadSynonmyTable();
}
