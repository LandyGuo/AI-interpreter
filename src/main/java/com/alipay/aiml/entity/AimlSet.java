package com.alipay.aiml.entity;

import com.alipay.aiml.consts.AimlTag;
import com.alipay.aiml.core.Named;
import com.alipay.aiml.core.Countable;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements AIML Sets
 *
 * @author mujian
 */
public class AimlSet extends HashSet<String> implements Named, AimlElement, Countable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5575558726803040996L;
	private final String name;

    public AimlSet(String name, Set<String> data) {
        super(data);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return AimlTag.set;
    }

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return this.size();
	}
}
