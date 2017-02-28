package com.alipay.aiml.chat;

import org.apache.commons.collections.map.ListOrderedMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.alipay.aiml.App;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Created by mujian on 6/18/15.
 *
 * @author mujian
 */

public class StateManager {
	
//	private static final Logger LOG = LoggerFactory.getLogger(StateManager.class);
	private ListOrderedMap statePool;
	private int concurrentSize;
	private static final int DefaultSize = 1000;//default maximum states
	
	public StateManager()
	{
		this(DefaultSize);
	}
	
	public StateManager(int maintainSize)
	{
		statePool = new ListOrderedMap();
		this.concurrentSize = maintainSize;
	}
	
	public ChatState getState(String key)
	{
		if(!statePool.containsKey(key))
		{
			//insert key if not exists
			statePool.put(key, new ChatState(key));
//			LOG.debug("put key:{} cursize:{} maxsize:{}",key,statePool.size(),concurrentSize);
			//check size
			if(statePool.size()>this.concurrentSize)
			{
//				LOG.debug("size full, remove earliest ChatState:{}",statePool.firstKey());
				statePool.remove(statePool.firstKey());
			}
			
		}
		return (ChatState)statePool.get(key);
	}
}
