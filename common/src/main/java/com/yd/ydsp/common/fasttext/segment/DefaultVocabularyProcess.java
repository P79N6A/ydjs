/*
 * Copyright 1999-2007 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.yd.ydsp.common.fasttext.segment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:yihan.zhaoyh@alibaba-inc.com>Zhao Yihan</a>
 */
public class DefaultVocabularyProcess implements VocabularyProcess {

    public List<InternalElement> postProcess(List<char[]> wordList) {
        List<InternalElement> elements = new ArrayList<InternalElement>(wordList.size());
        for (char[] cs : wordList) {
            elements.add(new InternalElement(cs));
        }
        return elements;
    }

}
