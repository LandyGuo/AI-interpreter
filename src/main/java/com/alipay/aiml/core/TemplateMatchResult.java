package com.alipay.aiml.core;

/**
 * 
 * @author mujian
 *
 * @param <T1>
 * @param <T2>
 * 
 * 输入与AIML template的匹配结果;
 * T1类型指示是否匹配;如果匹配，T2存储匹配的结果，否则T2为空
 */
public class TemplateMatchResult<T1, T2>  {
	public T1 isMatch;
	public T2 matchCnt;
	public TemplateMatchResult(T1 a, T2 b)
	{
		this.isMatch = a;
		this.matchCnt = b;
	}
	
}
