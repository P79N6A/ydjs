package com.yd.ydsp.common.fasttext.security.xss.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.yd.ydsp.common.fasttext.security.xss.TagProcessHandler;

/**
 * 标记： 名字， 动作， 属性
 * 
 * @author leon
 */
public class Tag {

    private String                             name;
    private String                             attrRef;
    private Map<String, Attribute>             allowedAttributes = new HashMap<String, Attribute>();
    private Action                             action;
    private Class<? extends TagProcessHandler> actionClass;

    public Tag(String name) {
        this.name = name;
    }

    public String getAttrRef() {
        return attrRef;
    }

    public void setAttrRef(String attrRef) {
        this.attrRef = attrRef;
    }

    public Map<String, Attribute> getAllowedAttributes() {
        return allowedAttributes;
    }

    public void setAllowedAttributes(Map<String, Attribute> allowedAttributes) {
        if (allowedAttributes != null) {
            this.allowedAttributes = allowedAttributes;
        }
    }

    public Attribute getAttributeByName(String name) {

        return (Attribute) allowedAttributes.get(name);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String[] getAttributes() {
        String atts[] = null;
        if (allowedAttributes != null && allowedAttributes.size() > 0) {
            atts = new String[allowedAttributes.size()];
            Set<String> key = allowedAttributes.keySet();
            int i = 0;
            for (String string : key) {
                atts[i++] = string;
            }
        }
        return atts;
    }

    public Class<? extends TagProcessHandler> getActionClass() {
        return this.actionClass;
    }

    public void setActionClass(Class<? extends TagProcessHandler> actionClass) {
        this.actionClass = actionClass;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "={");
        if (allowedAttributes != null && allowedAttributes.size() > 0) {
            Set<String> key = allowedAttributes.keySet();
            if (key.size() == 0) {
                return "";
            }
            for (String string : key) {
                sb.append(string);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

}
