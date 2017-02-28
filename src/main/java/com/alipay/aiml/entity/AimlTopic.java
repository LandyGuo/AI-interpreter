package com.alipay.aiml.entity;

import com.alipay.aiml.consts.AimlTag;

import java.util.List;

/**
 * Created by mujian on 21/10/16.
 *
 * @author mujian
 */
public class AimlTopic implements AimlElement {
    private List<AimlCategory> categories;
    private String name;

    @Override
    public String getType() {
        return AimlTag.topic;
    }

    public List<AimlCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<AimlCategory> categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
