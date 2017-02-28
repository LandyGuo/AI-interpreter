package com.alipay.aiml.entity;

import java.util.Map;


/**
 * Implements AIML Map
 *
 * A map is a function from one string set to another.
 * Elements of the domain are called keys and elements of the range are called values.
 *
 * @author mujian
 */
public class AimlSubstitution extends AimlMap {

    public AimlSubstitution(String name, Map<String, String> data) {
        super(name, data);
    }
}
