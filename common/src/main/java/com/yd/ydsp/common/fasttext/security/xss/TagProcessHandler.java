package com.yd.ydsp.common.fasttext.security.xss;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;



public interface TagProcessHandler {
    public void startElement(StringBuilder writerBuffer,QName element, XMLAttributes attributes, Augmentations augs);

    public void endElement(StringBuilder writerBuffer,QName element, Augmentations augs);

    public void characters(StringBuilder writerBuffer,XMLString text, Augmentations augs);
    
    public void emptyElement(StringBuilder writerBuffer,QName element, XMLAttributes attributes, Augmentations augs);

}
