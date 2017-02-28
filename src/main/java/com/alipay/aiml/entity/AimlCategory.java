package com.alipay.aiml.entity;

import java.util.Map;
import java.util.TreeMap;

import com.alipay.aiml.consts.AimlTag;

import org.w3c.dom.Node;

/**
 * Created by mujian on 21/06/15.
 *
 * @author mujian
 */
public class AimlCategory implements AimlElement {
    private String topic = "";
    private String pattern = "";
    private Map<String,Node> templates = new TreeMap<String,Node>();
    private String that = "";
    public static final String DEFAULT_TEMPLATE_TYPE ="default";  
    public static final String DEFAULT_TYPE_FIELD ="type";
    
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Node getTemplate(String type) {
    	if(!templates.containsKey(type))
    		return null;
    	return templates.get(type);
    }
    
    public Node getTemplate() {
    	return templates.get(AimlCategory.DEFAULT_TEMPLATE_TYPE);
    }
    

    public void addTemplate(Node template, String type) {
        templates.put(type, template);
    }
    
    public void addTemplate(Node template) {
        templates.put(AimlCategory.DEFAULT_TEMPLATE_TYPE, template);
    }
    
    public boolean hasTemplateType(String type)
    {
    	return templates.containsKey(type);
    }

    public String getThat() {
        return that;
    }

    public void setThat(String that) {
        this.that = that;
    }

    @Override
    public String getType() {
        return AimlTag.category;
    }
}
