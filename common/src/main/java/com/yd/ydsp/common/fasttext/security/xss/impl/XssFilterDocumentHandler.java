package com.yd.ydsp.common.fasttext.security.xss.impl;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.filters.DefaultFilter;

import com.yd.ydsp.common.fasttext.codec.*;
import com.yd.ydsp.common.fasttext.security.xss.AttributeProcessHandler;
import com.yd.ydsp.common.fasttext.security.xss.Policy;
import com.yd.ydsp.common.fasttext.security.xss.TagProcessHandler;
import com.yd.ydsp.common.fasttext.security.xss.model.Action;
import com.yd.ydsp.common.fasttext.security.xss.model.Attribute;
import com.yd.ydsp.common.fasttext.security.xss.model.RestrictAttribute;
import com.yd.ydsp.common.fasttext.security.xss.model.Tag;

/**
 * 为了提高性能， 把删除标记与输出做在一起， 大约可以提高100％性能。 另外也也顺便修改了一些nekohtml不合理比较string的方法。
 * 
 * @author leon
 */
public class XssFilterDocumentHandler extends DefaultFilter {
	private static final Log logger = LogFactory.getLog(XssFilterDocumentHandler.class);
    protected int              removalStack   = 0;

    /** output buffer */
    protected StringBuilder    fwriterBuffer;
    // 这个bufferr提供给外部的handler使用，防止handler将fwriterBuffer清空. 每次传入handler前应该清空一下。调用后再append到fwriterBuffer。
    protected StringBuilder    handlerBuffer;

    /** Seen root element. */
    protected boolean          fSeenRootElement;
    /** Normalize character content. */
    protected boolean          fNormalize;
    /** if characters will be print. */
    protected boolean          fPrintChars;
    /** if in style tag parsing */
    private boolean            parserStyleTag;

    protected Map<String, Tag> tagRules;
    private CssScanner         cssScanner;
    private Policy             fPolicy;
    private Map<String, String> params;
    
    private Map<Class<?>, TagProcessHandler> tagHandlerMap = new HashMap<Class<?>, TagProcessHandler>();
    private Map<Class<?>, AttributeProcessHandler> attrHandlerMap = new HashMap<Class<?>, AttributeProcessHandler>();
    private InnerStack<TagProcessHandler> stack = new InnerStack<TagProcessHandler>();
    
    public XssFilterDocumentHandler(StringBuilder writerBuffer, Policy policy, CssScanner cssScanner, Map<String, String> params) {
        this.fwriterBuffer = writerBuffer;
        this.handlerBuffer = new StringBuilder();
        this.fPolicy = policy;
        this.tagRules = policy.getTagRules();
        this.params = params;
        this.cssScanner = cssScanner;
    }

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) {
        fSeenRootElement = false;
        fNormalize = true;
        fPrintChars = true;
        parserStyleTag = false;
        super.startDocument(locator, encoding, nscontext, augs);
    }

    public void startDocument(XMLLocator locator, String encoding, Augmentations augs)
            throws XNIException {
        startDocument(locator, encoding, null, augs);
    }

    public void startPrefixMapping(String prefix, String uri, Augmentations augs)
            throws XNIException {
        if (removalStack == 0) {
            super.startPrefixMapping(prefix, uri, augs);
        }
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
            throws XNIException {
		Tag tag = tagRules.get(element.rawname);
		if (tag != null && tag.getAction() == Action.HANDLER) {
			TagProcessHandler handler = this.getStartTagProcessHandler(tag.getActionClass());
			if (handler == null) {
				throw new XNIException("handler null:");
			}
			handler.startElement(fwriterBuffer, element, attributes, augs);
			return;
		}
        if (handleOpenTag(element, attributes) && removalStack == 0) {
            fSeenRootElement = true;
            fNormalize = !HTMLElements.getElement(element.rawname).isSpecial();
            printStartElement(element, attributes);
            super.startElement(element, attributes, augs);
        }

    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
		Tag tag = tagRules.get(element.rawname);
		if (tag != null && tag.getAction() == Action.HANDLER) {
			TagProcessHandler handler = this.getEndTagProcessHandler();
			if (handler == null) {
				throw new XNIException("handler null:");
			}
			handler.endElement(fwriterBuffer, element, augs);
			return;
		}
    	if (elementAccepted(element) && removalStack == 0) {
            fNormalize = true;
            printEndElement(element);
            super.endElement(element, augs);
        }
    }

    protected boolean handleOpenTag(QName element, XMLAttributes attributes) {
        Tag tag = tagRules.get(element.rawname);
        if (tag != null) {
            if (tag.getAction() == Action.ACCEPT || tag.getAction() == Action.CSSHANDLER) {
                return true;
            } else if (tag.getAction() == Action.REMOVE) {
                removalStack++;
            }
        }
        return false;
    }

    protected boolean handleEmptyTag(QName element, XMLAttributes attributes) {
        Tag tag = tagRules.get(element.rawname);
        if (tag != null) {
            if (tag.getAction() == Action.ACCEPT || tag.getAction() == Action.CSSHANDLER) {
                return true;
            }
        }
        return false;
    }

    private boolean elementAccepted(QName element) {
        Tag tag = tagRules.get(element.rawname);
        boolean accept = false;

        if (tag != null) {
            Action action = tag.getAction();
            if (action == Action.ACCEPT || action == Action.CSSHANDLER) {
                accept = true;
            } else if (action == Action.REMOVE) {
                removalStack--;
            }
        }
        return accept;
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
            throws XNIException {
		Tag tag = tagRules.get(element.rawname);
		if (tag != null && tag.getAction() == Action.HANDLER) {
			TagProcessHandler handler = this.getStartTagProcessHandler(tag.getActionClass());
			if (handler == null) {
				throw new XNIException("handler null:");
			}
			handler.emptyElement(fwriterBuffer, element, attributes, augs);
			this.getEndTagProcessHandler();
			return;
		}
        if (removalStack == 0 && handleEmptyTag(element, attributes)) {
            fSeenRootElement = true;
            printEmptyElement(element, attributes);
            super.emptyElement(element, attributes, augs);
        }
    }

    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (removalStack == 0 && !fPolicy.removeComment) {
            if (fSeenRootElement) {
                fwriterBuffer.append("\r\n");
            }
            fwriterBuffer.append("<!--");
            printCharacters(text, false);
            fwriterBuffer.append("-->");
            if (!fSeenRootElement) {
                fwriterBuffer.append("\r\n");
            }
        }
    }

    public void processingInstruction(String target, XMLString data, Augmentations augs)
            throws XNIException {
        if (removalStack == 0) {
            super.processingInstruction(target, data, augs);
        }
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
		TagProcessHandler handler;
		if ((handler = getProcessHandler()) != null) {
			handler.characters(fwriterBuffer, text, augs);
			return;
		}
    	if (removalStack == 0) {
            if (fPrintChars) {
                if (parserStyleTag && cssScanner != null) {
                    String css = new String(text.ch, text.offset, text.length);
                    fwriterBuffer.append(cssScanner.scanStyleSheet(css, fPolicy, fPolicy.maxCssInputSize,
                            false));
                } else {
                    printCharacters(text, fNormalize);
                }
            }
            super.characters(text, augs);
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.ignorableWhitespace(text, augs);
        }
    }

    /**
     * Start general entity. </br> <li>如果是非法的html字符， 那么直接删除， 这个是为了安全起见, <li>
     * 避免特殊的字符集攻击<li>另外windows下一些特殊字符也可以被引起攻击。 这个地方需要好好再研究下。
     */
    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding,
                                   Augmentations augs) throws XNIException {
        fPrintChars = false;
        if (removalStack == 0) {
            if (name.startsWith("#")) {
                try {
                    boolean hex = name.startsWith("#x");
                    int offset = hex ? 2 : 1;
                    int base = hex ? 16 : 10;
                    int value = Integer.parseInt(name.substring(offset), base);
                    char[] entity = HtmlFastEntities.HTML40.getEntity((char) value);
                    if (entity != null) {
                        name = new String(entity);
                    }
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
        fwriterBuffer.append(name);
        super.startGeneralEntity(name, id, encoding, augs);
    }

    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.textDecl(version, encoding, augs);
        }
    }

    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        fPrintChars = true;
        if (removalStack == 0) {
            super.endGeneralEntity(name, augs);
        }
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.startCDATA(augs);
        }
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.endCDATA(augs);
        }
    }

    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
        if (removalStack == 0) {
            super.endPrefixMapping(prefix, augs);
        }
    }

    protected void printCharacters(XMLString text, boolean normalize) {
        if (!normalize) {
            fwriterBuffer.append(text.ch, text.offset, text.length);
            return;
        }
        for (int i = 0; i < text.length; i++) {
            char c = text.ch[text.offset + i];

            if (c == '\r' && i != text.length - 1 && text.ch[text.offset + i + 1] == '\n') {
                fwriterBuffer.append("\r\n");
                i++;
            } else if (c == ' ' || c == '\t') {
                fwriterBuffer.append(c);
            } else if (c == '\n') {
                fwriterBuffer.append("\r\n");
            } else {
                char[] entity = HtmlFastEntities.HTML40.getEntity(c);
                if (entity != null) {
                    fwriterBuffer.append(entity);
                } else {
                    fwriterBuffer.append(c);
                }
            }
        }
    }

    protected void printStartElement(QName element, XMLAttributes attributes) {
        Tag tag = tagRules.get(element.rawname);
        if(tag == null) return;
        
        fwriterBuffer.append('<');
        fwriterBuffer.append(element.rawname);
        int attrCount = attributes != null ? attributes.getLength() : 0;
        
        if (tag.getAction() == Action.CSSHANDLER && element.rawname.equalsIgnoreCase("style")) {
            parserStyleTag = true;
        }

        List<String> allAttr = new ArrayList<String>(tag.getAllowedAttributes().keySet());
        for (int i = 0; i < attrCount; i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);
            if(value == null || value.trim().length() == 0 || !allAttr.contains(name)){
            	continue;
            }
            allAttr.remove(name);
            
        	if(tag.getName().equalsIgnoreCase("embed") && name.equalsIgnoreCase("src")){
        		int index = value.indexOf("?");
        		if(index != -1){
        			value = value.substring(0,index);
        		}
        	}

        	// purifier inline style
            Attribute attr = tag.getAllowedAttributes().get(name);
            if (fPolicy.enableStyleScan) {
                if (attr.restrictAttribute == RestrictAttribute.STYLE && cssScanner != null) {
                    value = cssScanner.scanStyleSheet(value, fPolicy, fPolicy.maxCssInputSize, true);
                }
            }
            
            // if has regular expression and matched, print raw one. else use defined handler 
            boolean accept = true;
            List<Pattern> expr = attr.allowedRegExp;
            if (expr != null) {
                accept = false;
                for (Pattern pattern : expr) {
                    if (pattern.matcher(value).matches()) {
                        accept = true;
                        break;
                    }
                }
            } 
            
            if (accept) {
                fwriterBuffer.append(' ');
                fwriterBuffer.append(name);
                fwriterBuffer.append("=\"");
                printAttributeValue(value);
                fwriterBuffer.append('"');
            } else if(attr.backHandler != null){
                AttributeProcessHandler handler = this.getAttributeProcessHandler(attr.backHandler);
                if(handler != null){
                    this.handlerBuffer.setLength(0);
                    handler.printAttribute(this.handlerBuffer, name, value, params);
                    this.fwriterBuffer.append(" ").append(this.handlerBuffer).append(" ");
                }
            }
        }
        
        for(String defaultName : allAttr){
        	String defaultValue = tag.getAllowedAttributes().get(defaultName).defaultValue; 
        	if(defaultValue != null && !defaultValue.equals("")){
                fwriterBuffer.append(' ');
                fwriterBuffer.append(defaultName);
                fwriterBuffer.append("=\"");
                printAttributeValue(defaultValue);
                fwriterBuffer.append('"');
        	}
        }
        fwriterBuffer.append('>');
    }

    protected void printEmptyElement(QName element, XMLAttributes attributes) {
    	this.printStartElement(element, attributes);
        this.fwriterBuffer.insert(this.fwriterBuffer.length() - 1, '/');
    }

    // convert " to &quot;
    private void printAttributeValue(String text) {
        int length = text.length();
        for (int j = 0; j < length; j++) {
            char c = text.charAt(j);
            if (c == '"') {
                fwriterBuffer.append("&quot;");
            } else {
                fwriterBuffer.append(c);
            }
        }
    }

    private void printEndElement(QName element) {
        Tag tag = tagRules.get(element.rawname);
        if (tag != null && tag.getAction() == Action.CSSHANDLER && element.rawname.equalsIgnoreCase("style")) {
            parserStyleTag = false;
        }
        fwriterBuffer.append("</");
        fwriterBuffer.append(element.rawname);
        fwriterBuffer.append('>');
    }
    
	private TagProcessHandler getStartTagProcessHandler(
			Class<? extends TagProcessHandler> clazz) {
		if(clazz == null) return null;
		TagProcessHandler handler = this.tagHandlerMap.get(clazz);
		if (handler == null) {
			try {
				handler = clazz.newInstance();
			} catch (InstantiationException e) {
				logger.info("error:" + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.info("error:" + e.getMessage());
			}
			this.tagHandlerMap.put(clazz, handler);
		}
		stack.push(handler);
		return handler;
	}

	private TagProcessHandler getEndTagProcessHandler() {
		try {
			return stack.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	}
	
	private TagProcessHandler getProcessHandler() {
		try {
			return stack.peek();
		} catch (EmptyStackException e) {
			return null;
		}
	}
	
	private AttributeProcessHandler getAttributeProcessHandler(
			Class<? extends AttributeProcessHandler> clazz) {
		if(clazz == null) return null;
		AttributeProcessHandler handler = this.attrHandlerMap.get(clazz);
		if (handler == null) {
			try {
				handler = clazz.newInstance();
			} catch (InstantiationException e) {
				logger.info("error:" + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.info("error:" + e.getMessage());
			}
			this.attrHandlerMap.put(clazz, handler);
		}
		return handler;
	}
	private static class InnerStack<E> {
		private int size;
		private static final int INITIALCAPACITY = 32;
		private E[] os;

		@SuppressWarnings("unchecked")
		public InnerStack() {
			os = (E[]) new Object[INITIALCAPACITY];
		}

		public void push(E o) {
			checkCapacity();
			this.os[size++] = o;
		}

		public E pop() {
			if (size <= 0)
				throw new EmptyStackException();
			E o = (E) this.os[--size];
			this.os[size] = null;
			return o;
		}

		public E peek() {
			if (size <= 0)
				throw new EmptyStackException();
			E o = (E) this.os[this.size - 1];
			return o;
		}

		@SuppressWarnings("unchecked")
		private void checkCapacity() {
			if (size == os.length) {
				E[] os1 = (E[]) new Object[size * 2 + 1];
				System.arraycopy(os, 0, os1, 0, os.length);
				os = os1;
			}
		}
	}
}
