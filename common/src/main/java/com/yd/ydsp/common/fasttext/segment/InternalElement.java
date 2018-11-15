/*
 * Copyright 1999-2007 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.yd.ydsp.common.fasttext.segment;

/**
 * @author <a href="mailto:yihan.zhaoyh@alibaba-inc.com>Zhao Yihan</a>
 */
public class InternalElement implements Comparable<InternalElement> {

    public char          sequence[];
    public TermExtraInfo termExtraInfo;

    public InternalElement(char line[]){
        this.sequence = line;
    }

    public InternalElement(char line[], TermExtraInfo node){
        this.sequence = line;
        this.termExtraInfo = node;
    }

    public int compareTo(InternalElement o) {
        char[] a = this.sequence;
        char[] b = o.sequence;
        int loop = a.length > b.length ? b.length : a.length;
        for (int i = 0; i < loop; i++) {
            int c = a[i] - b[i];
            if (c != 0) {
                return c;
            }
        }
        return a.length - b.length;
    }
}
