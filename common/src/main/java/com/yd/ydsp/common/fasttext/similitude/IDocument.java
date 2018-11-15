package com.yd.ydsp.common.fasttext.similitude;

public interface IDocument {

    public Object getWordTerms();

    public int getDocumentLength();

    public SimilitudeText getSimilitudeText();

}
