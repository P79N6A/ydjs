package com.yd.ydsp.common.security.url;

public class ParseWhiteListException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1724111774221810574L;

    public ParseWhiteListException(Exception e) {
        super(e);
    }

    public ParseWhiteListException(String string) {
        super(string);
    }
}