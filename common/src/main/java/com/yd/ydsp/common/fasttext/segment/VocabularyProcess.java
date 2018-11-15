package com.yd.ydsp.common.fasttext.segment;

import java.util.List;

public interface VocabularyProcess {

    public List<InternalElement> postProcess(List<char[]> lines);

}
