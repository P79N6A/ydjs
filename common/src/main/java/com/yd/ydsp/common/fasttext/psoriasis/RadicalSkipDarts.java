package com.yd.ydsp.common.fasttext.psoriasis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 在SkipDarts基础上增加部首匹配处理，如”氵去“对应”法“，”氵去轮功“对应”法轮功等，同样支持编辑距离概念，<br>
 * 如“氵去aa轮功”也可以匹配"法轮功"，但是“氵aa去”不能匹配“法”因为字符拆分后在插入其它字符没有意义。
 * 
 * @author guolin.zhuanggl 2008-8-11 上午11:48:03
 */
public class RadicalSkipDarts extends SkipDarts {

    private static final Log   logger                  = LogFactory.getLog(RadicalSkipDarts.class);

    public static final String DEFAULT_RADICA_DIC_NAME = "DefaultRadicalDic_u8.txt";
    private Configuration      config;

    public RadicalSkipDarts(List<SkipTermExtraInfo> dic, Configuration config) {
        this.config = config;
        this.skip = this.config.getSkip();
        this.hanziSkip = this.config.getHanziSkip();
        init(dic, skip, hanziSkip);
    }

    @Deprecated
    public RadicalSkipDarts(List<SkipTermExtraInfo> dic, int skip) {
        this(dic, null, skip);
    }

    @Deprecated
    public RadicalSkipDarts(List<SkipTermExtraInfo> dic, List<SkipTermExtraInfo> radicalDic,
                            int skip) {
        if (dic == null || skip < 0) {
            logger.error("Dictinary can not be null or skip must be greater than 0.");
            throw new IllegalArgumentException(
                    "Dictinary can not be null or skip must be greater than 0.");
        }
        if (radicalDic != null) {
            List<String> rwl = PsoriasisUtil.readList(DEFAULT_RADICA_DIC_NAME, "utf8", this
                    .getClass());
            WordTransformer transform = new WordTransformer(null, rwl, null, null);
            List<SkipTermExtraInfo> radList = new ArrayList<SkipTermExtraInfo>();
            for (SkipTermExtraInfo info : dic) {
                radList.addAll(transform.transformForkWords(info.getWord()));
            }
            dic.addAll(radList);
        }
        init(dic, skip);
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize Darts from dic: " + dic + "Radicals Dic: " + radicalDic
                    + " skip: " + skip);
        }
    }

}
