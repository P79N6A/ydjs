package com.yd.ydsp.common.fasttext.psoriasis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yd.ydsp.common.fasttext.segment.Darts;
import com.yd.ydsp.common.fasttext.segment.InternalElement;
import com.yd.ydsp.common.fasttext.segment.VocabularyProcess;
import com.yd.ydsp.common.fasttext.segment.WordTerm;

/**
 * 类SkipDarts.java的实现描述：<br>
 * 在darts基础上增加跳字匹配功能，例如“法-轮-功”匹配“法轮功”等，跳字次数可以设定，缺省为0，表示不跳字。
 * 算法对于状态机在不匹配状态进行了后续处理，继续按照可以跳字次数匹配后续字符，直到发现一个匹配或者跳字结束或者文本扫描完毕。 跳字次数不可小于0，建议在1~3之间，大于3没有意义，而且对性能影响较大。
 * note:内部对词表词条进行了规范化和去重处理，包括：<br>
 * 1、繁体转简体<br>
 * 2、全角转半角<br>
 * 3、大写转小写<br>
 * 4、过滤空格<br>
 * 5、去重<br>
 * 
 * @author guolin.zhuanggl 2008-8-18 上午11:19:59
 */
public class SkipDarts extends Darts {

    private static final Log logger      = LogFactory.getLog(SkipDarts.class);
    protected int            skip        = 0;
    protected int            hanziSkip   = 0;
    protected boolean        toLowerCase = true;

    //定义一个简单汉子的集合，这些汉子算入跳字
    private static final Set hanziSet = new HashSet();
    
    static {
        //一丄丅丆丨丫丶丷丿乀乁乚乛亅冖凵扌氵灬爫礻糹罒罓艹覀訁讠辶釒钅飠饣刂卩冫犭癶纟衤
        String hanzis = "\u4E00\u4E04\u4E05\u4E06\u4E28\u4E2B\u4E36\u4E37\u4E3F\u4E40\u4E41\u4E5A\u4E5B\u4E85\u5196\u51F5\u624C\u6C35\u706C\u722B\u793B\u7CF9\u7F52\u7F53\u8279\u8980\u8A01\u8BA0\u8FB6\u91D2\u9485\u98E0\u9963\u5202\u5369\u51AB\u72AD\u7676\u7E9F\u8864";
        for(char tmp : hanzis.toCharArray()) {
            hanziSet.add(tmp);
        }
    }
    /**
     * 缺省构建器，方便子类进行继承扩展，注意扩展类构建器必须调用init()。
     */
    protected SkipDarts(){
    }

    /**
     * 使用词语列表构建darts，内部对两个词表项进行规范化处理和去重处理。<br>
     * 
     * @param dic 词语列表，包含权重，是否可以跳字匹配，是否需要全字匹配等信息
     * @param skip 跳字匹配，如果为0表示不跳字，如果小于0抛出IAE
     */

    public SkipDarts(List<SkipTermExtraInfo> dic, int skip){
        if (dic == null || skip < 0) {
            logger.error("Dic can not be null or skip must be greater than 0.");
            throw new IllegalArgumentException("Dic can not be null or skip must be greater than 0.");
        }
        init(dic, skip);
    }

    /**
     * 使用使用词语列表构建darts，内部对词表项进行规范化处理和去重处理。<br>
     * 
     * @param dic 关键词词典，不可为空，为空抛出IAE异常<br>
     * @param skip 跳字匹配，如果为0表示不跳字，如果小于0抛出IAE异常<br>
     */
    protected void init(List<SkipTermExtraInfo> dic, int skip) {
        init(dic,skip,0);
    }


    /**
     * 使用使用词语列表构建darts，内部对词表项进行规范化处理和去重处理。<br>
     * 
     * @param dic 关键词词典，不可为空，为空抛出IAE异常<br>
     * @param skip 跳字匹配，如果为0表示不跳字，如果小于0抛出IAE异常<br>
     */
    protected void init(List<SkipTermExtraInfo> dic, int skip, int hanziSkip) {
        this.skip = skip;
        this.hanziSkip = hanziSkip;
        List<char[]> tokens = new ArrayList<char[]>(dic.size());
        Map<String, SkipTermExtraInfo> wordMap = new HashMap<String, SkipTermExtraInfo>();
        for (SkipTermExtraInfo word : dic) {
            //容错，空字符串可能导致darts死循环
            if(word.getWord() == null || word.getWord().trim().length() == 0){
                continue;
            }
            if (toLowerCase) {
                tokens.add(word.getWord().toLowerCase().toCharArray());
                wordMap.put(word.getWord().toLowerCase(), word);
            } else {
                tokens.add(word.getWord().toCharArray());
                wordMap.put(word.getWord(), word);
            }
        }
        build(tokens, new SkipWordProcess(wordMap));
    }
    
    /**
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br> 使用缺省设置skip值。
     * 
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @return 一次匹配的结果(可能匹配到多个相互包含的词，如'abcd'和'abcde')
     */

    public List<WordTerm> prefixSearch(char[] key, int pos, int len) {
        return prefixSearch(key, pos, len, this.skip);
    }

    /**
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br> 使用缺省设置skip值。
     * 
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @return 一次匹配的结果(可能匹配到多个相互包含的词，如'abcd'和'abcde')
     */
    public WordTerm prefixSearchMax(char[] key, int pos, int len) {
        return prefixSearchMax(key, pos, len, this.skip);
    }

    /**
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br>
     * 
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @param skip 允许跳过的字符数(每两个字符间)
     * @return 一次匹配的结果(可能匹配到多个相互包含的词，如'abcd'和'abcde')
     */

    public List<WordTerm> prefixSearch(char[] key, int pos, int len, int skip) {
        return prefixSearch(key,pos,len,skip,this.hanziSkip);
    }

    /**
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br>
     * 如指定skip为2,hanziSkip为1，那么'a汉b字c',可以匹配'abc','a汉字b汉字c'不能匹配'abc','ahzbhzc'可以匹配'abc'
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @param skip 允许跳过的字符数(每两个字符间)
     * @param hanziSkip 允许汉子的跳过的字符数(每两个字符间)，此参数在skip>0的情况下才有效,为0表示不考虑跳字时汉子的特殊处理,目前只能设定为1
     * @return 一次匹配的结果(可能匹配到多个相互包含的词，如'abcd'和'abcde')
     */
    public List<WordTerm> prefixSearch(char[] key, int pos, int len, int skip, int hanziSkip) {
        if (skip < 0 || hanziSkip < 0) {
            throw new IllegalArgumentException("Skip or hanziSkip can not less than 0!");
        }
        if (skip == 0) {
            return super.prefixSearch(key, pos, len);
        }
        int p, n, i, b = baseArray[0];
        List<WordTerm> result = new ArrayList<WordTerm>();
        int remain = skip;
        int hanziRemain = hanziSkip;
        for (i = pos; i < pos + len; ++i) {
            p = b; // + 0;
            n = baseArray[p];
            if (b == checkArray[p] && n < 0) {// 找到一个词
                WordTerm w = new WordTerm();
                w.position = -n - 1;
                w.begin = pos;
                w.length = i - pos;
                w.termExtraInfo = extraNodeArray[p];
                if (result.size() > 0) {// 如果已经跳字匹配但是没有成功，此时会增加一个重复错误匹配进去，这里处理
                    WordTerm w1 = result.get(result.size() - 1);// 取得上一个匹配结果
                    if (w1.position != w.position) {// 不是同一个词重复匹配
                        result.add(w);
                    }
                } else {
                    result.add(w);
                }
            }
            p = b + (key[i]) + 1;
            if (b == checkArray[p]) {// 还能往下接
                b = baseArray[p];
                remain = skip;// 还原还能跳过的字符数
                hanziRemain = hanziSkip;
            } else if (hanziSkip == 0 || hanziSkip == skip){//hanziSkip == skip此条件不写,下面的代码也正确,写上是为了优化,不走下面的多逻辑判断
                if (remain == 0 || b == baseArray[0]) {// 不能再跳过字符了或者一开始就没有匹配到
                    return result;
                } else {
                    remain--;
                }
            } else {
                if (remain == 0 || b == baseArray[0]) {// 不能再跳过字符了或者一开始就没有匹配到
                    return result;
                } else {
                    if(key[i]> '\u4e00' && key[i] < '\u9fa5' && !hanziSet.contains(key[i])) {
                        hanziRemain--;
                    }
                    remain--;
                    
                    if(hanziRemain < 0 || (hanziRemain==0 && (skip-hanziSkip != remain )) ) {
                        return result;
                    }                    
                }
            }
        }
        p = b;
        n = baseArray[p];
        if (b == checkArray[p] && n < 0) {
            WordTerm w = new WordTerm();
            w.position = -n - 1;
            w.begin = pos;
            w.length = i - pos;
            w.termExtraInfo = extraNodeArray[p];
            if (result.size() > 0) {// 如果已经跳字匹配但是没有成功，此时会增加一个重复错误匹配进去，这里处理
                WordTerm w1 = result.get(result.size() - 1);// 取得上一个匹配结果
                if (w1.position != w.position) {// 不是同一个词重复匹配
                    result.add(w);
                }
            } else {
                result.add(w);
            }
        }
        return result;
    }

    /**
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br>
     * 
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @param skip 允许跳过的字符数(每两个字符间)
     * @return 一次匹配的结果(可能匹配到多个相互包含的词，如'abcd'和'abcde')
     */
    public WordTerm prefixSearchMax(char[] key, int pos, int len, int skip) {
        return prefixSearchMax(key,pos,len,skip,this.hanziSkip);
    }
    
    /**
     * 
     * 扩展Darts，添加允许指定数量跳过字符的匹配方法</br> <br>
     * 扩展的前向匹配查找， 要以在匹配时跳过指定个数的字符。</br> <br>
     * 如指定跳过一个字符，则'a b c', 'abdc', 'a我bc'都可以匹配'abc'</br>
     * 如指定skip为2,hanziSkip为1，那么'a汉b字c',可以匹配'abc','a汉字b汉字c'不能匹配'abc','ahzbhzc'可以匹配'abc'
     * @param key 需要被匹配的字串
     * @param pos 从字串中哪个位置开始匹配
     * @param len 匹配多少个字符
     * @param skip 允许跳过的字符数(每两个字符间)
     * @param hanziSkip 允许汉子的跳过的字符数(每两个字符间)，此参数在skip>0的情况下才有效,为0表示不考虑跳字时汉子的特殊处理,目前只能设定为1
     * @return WordTerm
     */
    public WordTerm prefixSearchMax(char[] key, int pos, int len, int skip, int hanziSkip) {
        if (skip < 0) {
            throw new IllegalArgumentException("Skip can not less than 0!");
        }
        if (skip == 0) {
            return super.prefixSearchMax(key, pos, len);
        }
        int p, n, i, b = baseArray[0];
        WordTerm w = null;
        int remain = skip;
        int hanziRemain = hanziSkip;
        boolean isSkipMatched = false;
        for (i = pos; i < pos + len; ++i) {
            p = b; // + 0;
            n = baseArray[p];
            if (b == checkArray[p] && n < 0) {
                if (w == null || !isSkipMatched || w.position != -n - 1) {// 如果是跳字匹配且没有发现新词，保留原词
                    if (w == null) {
                        w = new WordTerm();
                    }
                    w.position = -n - 1;
                    w.begin = pos;
                    w.length = i - pos;
                    if (hasExtraData) {
                        w.termExtraInfo = extraNodeArray[p];
                    }
                }
            }
            p = b + (key[i]) + 1;
            if (b == checkArray[p]) {// 还能往下接
                b = baseArray[p];
                remain = skip;// 还原还能跳过的字符数
                hanziRemain = hanziSkip;
            } else if (hanziSkip == 0 || hanziSkip == skip){//hanziSkip == skip此条件不写,下面的代码也正确,写上是为了优化,不走下面的多逻辑判断
                if (remain == 0 || b == baseArray[0]) {// 不能再跳过字符了或者一开始就没有匹配到
                    return w;
                } else {
                    isSkipMatched = true;// 开始跳字匹配
                    remain--;
                }
            }  else {
                if (remain == 0 || b == baseArray[0]) {// 不能再跳过字符了或者一开始就没有匹配到
                    return w;
                } else {
                    isSkipMatched = true;// 开始跳字匹配
                    if(key[i]> '\u4e00' && key[i] < '\u9fa5' && !hanziSet.contains(key[i])) {
                        hanziRemain--;
                    }
                    remain--;
                    
                    if(hanziRemain < 0 || (hanziRemain==0 && (skip-hanziSkip != remain )) ) {
                        return w;
                    }                    
                }
            }
        }
        p = b;
        n = baseArray[p];
        if (b == checkArray[p] && n < 0) {
            if (w == null || !isSkipMatched || w.position != -n - 1) {// 如果是跳字匹配且没有发现新词，保留原词
                if (w == null) {
                    w = new WordTerm();
                }
                w.position = -n - 1;
                w.begin = pos;
                w.length = i - pos;
                if (hasExtraData) {
                    w.termExtraInfo = extraNodeArray[p];
                }
            }
        }
        return w;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        if (skip < 0) {
            logger.error("skip must be greater than 0.");
            throw new IllegalArgumentException("Skip must be greater than 0.");
        }
        this.skip = skip;
    }

    static class SkipWordProcess implements VocabularyProcess {

        private Map<String, SkipTermExtraInfo> wordMap;

        public SkipWordProcess(Map<String, SkipTermExtraInfo> wordMap){
            this.wordMap = wordMap;
        }

        public List<InternalElement> postProcess(List<char[]> lines) {
            List<InternalElement> elements = new ArrayList<InternalElement>(lines.size());
            for (char[] cs : lines) {
                SkipTermExtraInfo word = wordMap.get(new String(cs));
                if (word != null) {
                    InternalElement element = new InternalElement(cs, word);
                    elements.add(element);
                }
            }
            return elements;
        }
    }
}
