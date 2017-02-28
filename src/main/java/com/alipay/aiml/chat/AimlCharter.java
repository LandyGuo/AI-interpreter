package com.alipay.aiml.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.alipay.aiml.App;
import com.alipay.aiml.bot.Bot;
import com.alipay.aiml.bot.BotRepository;
import com.alipay.aiml.chat.ChatState;
import com.alipay.aiml.consts.AimlConst;
import com.alipay.aiml.input.Entity;

public class AimlCharter {
	
//	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
    private final static String DEFAULT_NICKNAME = "ChatRobot";
    private final Bot bot;
    private String nickname = DEFAULT_NICKNAME;
    private ChatState state;
    private StateManager manager;
    private static volatile AimlCharter instance; 

    private AimlCharter(Bot agent) {
        bot = agent;
        manager = new StateManager();
        state = manager.getState(nickname);
    }
    
    public Bot getBot()
    {
    	return bot;
    }
    
    public void setUser(String userName)
    {
    	state = manager.getState(userName);
    }
    
    public ChatState getChatState()
    {
    	return  state;
    }
    
    public void setChatState(ChatState cs)
    {
    	state = cs;
    }
    
    public static void Init(String robotFolder)
    {
    	AimlConst.setRootPath(robotFolder);
    }
        
    public static AimlCharter getInstance()
    {
    	if(instance==null)
    	{
    		 synchronized (AimlCharter.class) {
                 //δ��ʼ�������ʼinstance����
                 if (instance == null) {
                	 Bot agent = BotRepository.get();
                     if (!agent.wakeUp())
                     {
                    	 System.out.println("agent files not exist");
                         return null; 
                     }
                     instance = new AimlCharter(agent);   
                 }   
             }   
    	}
		return instance;
    }

    public String process(String request) {
    	//对输入进行预处理
    	String query = request.replaceAll("呀|啊|呢", "");
//    	LOG.debug("query:{}",query);
    	return process(query, new ArrayList<String>());
    }
    
    public String process(String request, List<String> entities) {
        String response = bot.multisentenceRespond(request, state, entities);
        if(!response.isEmpty())
        	this.state.newState(request, response);
        for (Entry<String, String> m : state.getPredicates().entrySet()) { 
        	   
//            LOG.debug("state predicates:key {} value:{} " ,m.getKey(), m.getValue()); 
           } 
        return response.trim();
}
}
