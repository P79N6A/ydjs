package com.yd.ydsp.client.domian;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;


public class BaseDTO implements Serializable {

	private static final long serialVersionUID = 493188846771659168L;
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
