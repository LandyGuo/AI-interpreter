package com.alipay.aiml.entity;

import com.alipay.aiml.consts.AimlTag;
import com.alipay.aiml.utils.AppUtils;

import java.util.List;

/**
 * Created by mujian on 21/10/16.
 *
 * @author mujian
 */
public class AimlRandom implements AimlElement {
    private final List<String> options;

    public AimlRandom(List<String> options) {
        this.options = options;
    }

    @Override
    public String getType() {
        return AimlTag.random;
    }

    public String getValue() {
        return AppUtils.getRandom(options);
    }
}
