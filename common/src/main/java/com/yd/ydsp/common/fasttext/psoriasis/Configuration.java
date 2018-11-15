package com.yd.ydsp.common.fasttext.psoriasis;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yd.ydsp.common.fasttext.decorator.DecoratorConstants;
import com.yd.ydsp.common.fasttext.segment.WordTerm;

public class Configuration {

    private static final Log   logger                      = LogFactory.getLog(Configuration.class);
    // customeize behavior start
    public static final String DECOR_QUICKMATCH_ENABLE_KEY = "decorator.quickmatch.enable";
    public static final String DARTS_SKIP_VALUE_KEY        = "decorator.darts.skip.value";
    public static final String DARTS_HANZI_SKIP_VALUE_KEY  = "decorator.darts.hanziskip.value";
    public static final String DARTS_ENABLE_TOLOWERCASE    = "decorator.darts.deform.enable";
    public static final String DARTS_ENABLE_PINYIN_KEY     = "decorator.darts.pinyin.enable";
    public static final String DARTS_ENALBLE_FORK_KEY      = "decorator.darts.fork.enable";
    public static final String DARTS_ENABLE_HOMOPHONE_KEY  = "decorator.darts.homophone.enable";
    public static final String DARTS_ENABLE_DEFORM_KEY     = "decorator.darts.deform.enable";

    // customeize behavior end
    private MappedExtractText  extractor;
    private WordDecorator      wordDecorator;
    private boolean            quickMatch;
    private int                skip;
    private int                hanziSkip;
    private boolean            pinyin;
    private boolean            fork;
    private boolean            homophone;
    private boolean            deform;
    private boolean            toLowcase;

    public Configuration() {

    }

    public Configuration(Properties props) {
        this(null, null, props);
    }

    public Configuration(WordDecorator wordDecorator, Properties props) {
        this(null, wordDecorator, props);
    }

    public Configuration(MappedExtractText extractor, Properties props) {
        this(extractor, null, props);
    }

    public Configuration(MappedExtractText extractor, WordDecorator wordDecorator, Properties props) {
        if (extractor == null) {
            extractor = new CompositeHTMLExtractText();
        }
        if (wordDecorator == null) {
            wordDecorator = new DefaultWordDecorator();
        }
        this.extractor = extractor;
        this.wordDecorator = wordDecorator;
        init(props);
    }

    private void init(Properties props) {
        quickMatch = Boolean.parseBoolean(props.getProperty(DECOR_QUICKMATCH_ENABLE_KEY, "false"));
        String skipStr = props.getProperty(DARTS_SKIP_VALUE_KEY, "3");
        String hanziSkipStr = props.getProperty(DARTS_HANZI_SKIP_VALUE_KEY, "0");
        try {
            skip = Integer.parseInt(skipStr);
            hanziSkip = Integer.parseInt(hanziSkipStr);
        } catch (NumberFormatException e) {
            logger.error("Invalid skip or hanziSkip value. " + e.getMessage());
            throw new IllegalArgumentException(e);
        }

        pinyin = Boolean.parseBoolean(props.getProperty(DARTS_ENABLE_PINYIN_KEY, "true"));
        fork = Boolean.parseBoolean(props.getProperty(DARTS_ENALBLE_FORK_KEY, "true"));
        homophone = Boolean.parseBoolean(props.getProperty(DARTS_ENABLE_HOMOPHONE_KEY, "true"));
        deform = Boolean.parseBoolean(props.getProperty(DARTS_ENABLE_DEFORM_KEY, "true"));
        toLowcase = Boolean.parseBoolean(props.getProperty(DARTS_ENABLE_TOLOWERCASE, "true"));
    }

    public MappedExtractText getMappedExtracter() {
        return extractor;
    }

    public void setMappedExtracter(MappedExtractText extractor) {
        this.extractor = extractor;
    }

    public WordDecorator getWordDecorator() {
        return wordDecorator;
    }

    public void setWordDecorator(WordDecorator wordDecorator) {
        if (wordDecorator != null) {
            this.wordDecorator = wordDecorator;
        }
    }

    public boolean isQuickMatch() {
        return quickMatch;
    }

    public void setQuickMatch(boolean quickMatch) {
        this.quickMatch = quickMatch;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        if (skip > 0) {
            this.skip = skip;
        }
    }

    public int getHanziSkip() {
        return hanziSkip;
    }

    public void setHanziSkip(int hanziSkip) {
        if (hanziSkip > 0) {
            this.hanziSkip = hanziSkip;
        }
    }

    public boolean isToLowcase() {
        return toLowcase;
    }

    public void setToLowcase(boolean toLowcase) {
        this.toLowcase = toLowcase;
    }

    public boolean enablePinyinTransform() {
        return pinyin;
    }

    public void setPinyinTransform(boolean pinyin) {
        this.pinyin = pinyin;
    }

    public boolean enableForkTransform() {
        return fork;
    }

    public void setForkTransform(boolean fork) {
        this.fork = fork;
    }

    public boolean enableHomophoneTransform() {
        return homophone;
    }

    public void setHomophoneTransform(boolean homophone) {
        this.homophone = homophone;
    }

    public boolean enableDeformTransform() {
        return deform;
    }

    public void setDeformTransform(boolean deform) {
        this.deform = deform;
    }

    private static class DefaultWordDecorator implements WordDecorator {

        public DefaultWordDecorator() {
        }

        public String decorateWord(String content, WordTerm wordTerm) {
            return DecoratorConstants.DEFAULT_HL_START_TAG + content
                    + DecoratorConstants.DEFAULT_HL_END_TAG;
        }

        public boolean match(String content, WordTerm wordTerm) {
            return false;
        }
    }
}
