package com.alipay.aiml.entity;

import com.alipay.aiml.consts.AimlTag;
import com.alipay.aiml.core.Named;
import com.alipay.aiml.core.Countable;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements AIML Map
 * A map is a function from one string set to another.
 * Elements of the domain are called keys and elements of the range are called values.
 *
 * @author mujian
 */
public class AimlMap extends HashMap<String, String> implements Named, AimlElement, Countable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4719148645979547061L;
	protected final String name;

    public AimlMap(String name, Map<String, String> data) {
        super(data);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return AimlTag.map;
    }

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return this.size();
	}
}
