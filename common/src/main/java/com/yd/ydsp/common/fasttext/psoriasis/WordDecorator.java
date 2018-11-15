package com.yd.ydsp.common.fasttext.psoriasis;

import com.yd.ydsp.common.fasttext.segment.WordTerm;


public interface WordDecorator {
    /**
     * 是否对这个词进行特殊高亮。
     * @param word 原始文本
     * @param wordTerm 分词
     * @return 是否采用这个词高亮器对词进行特殊高亮，如果为false，decorateWord方法将不会被调用。
     */
    public boolean match(String word,WordTerm wordTerm);
    /**
     * 对词语进行特殊高亮
     * @param word 原始文本
     * @param wordTerm 分词
     * @return 高亮后的文本,可以增加自定义tag
     */
    public String decorateWord(String word,WordTerm wordTerm);
}
