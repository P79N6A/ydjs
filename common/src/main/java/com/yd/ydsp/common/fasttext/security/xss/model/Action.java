package com.yd.ydsp.common.fasttext.security.xss.model;

/**
 * Html 标记处理办法
 * 
 * <pre>
 * 1. REMOVE 删除标记和内容
 * 2. ACCEPT 保留标记和内容
 * 其余的情况是删除标记，但保留内容
 * </pre>
 * 
 * @author leon
 */
public enum Action {
    REMOVE, ACCEPT, CSSHANDLER,HANDLER
}

