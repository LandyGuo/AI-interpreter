package com.alipay.aiml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alipay.aiml.bot.Bot;
import com.alipay.aiml.bot.BotRepository;
import com.alipay.aiml.chat.Chat;
import com.alipay.aiml.chat.ChatState;
import com.alipay.aiml.chat.StateManager;
import com.alipay.aiml.providers.ConsoleProvider;
import com.alipay.aiml.providers.Provider;
import com.alipay.aiml.chat.AimlCharter;
import com.alipay.aiml.input.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mujian*/
public class App {
	
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
    public static void main(String[] args) {
    	startChatClient();
        
    }
    
    public static void startChatClient()
    {
    	AimlCharter.Init(System.getProperty("user.dir") + "/bots/testbot");
    	AimlCharter charter  = AimlCharter.getInstance();
        assert(charter!=null);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String textLine = null;
        System.out.println("Hello! Welcome to chat with " + charter.getBot().getName() + ".\n");
        while(true)
        {
        	//read input 
        	try {
        		System.out.print(">>");
                textLine = reader.readLine();
                String respond = charter.process(textLine);
                System.out.print(">>");
                System.out.println(respond);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void testStateManager()
    {
    	StateManager manager = new StateManager();
    	ChatState cs = manager.getState("lily");
    	cs.setTopic("你好");
    	ChatState cs2 = manager.getState("lucy");
    	cs2.setTopic("abc");
    	ChatState lily = manager.getState("lucy");
    	LOG.debug("ChatState Topic:{}",lily.topic());
    	ChatState lucy = manager.getState("lily");
    	LOG.debug("ChatState Topic:{}",lucy.topic());
    	
    }
       
}