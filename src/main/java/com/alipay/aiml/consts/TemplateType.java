package com.alipay.aiml.consts;

/**
 * Support templates type for Aiml
 * Created by mujian on 02/25/17.
 *
 * @author mujian
 */
public enum TemplateType {
	DEFAULT("default"),
	FOOD("food"),
	COMPANY("company"),
	LOCATION("location"),
	ENTERTAINER("entertainer"),
	ENTREPRENEUR("entrepreneur"),
	POLITICIAN("politician")
	;
	
	private final String name;
	
	TemplateType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	

}
