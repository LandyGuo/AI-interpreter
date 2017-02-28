package com.alipay.aiml;

import java.util.Arrays;
import java.util.List;

import com.alipay.aiml.bot.Bot;
import com.alipay.aiml.bot.BotRepository;
import com.alipay.aiml.chat.AimlCharter;
import com.alipay.aiml.chat.Chat;
import com.alipay.aiml.chat.ChatState;
import com.alipay.aiml.consts.AimlConst;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by mujian on 22/06/15.
 *
 * @author mujian
 */
public class BotTest extends Assert {
    private AimlCharter charter;
    private static final Logger LOG = LoggerFactory.getLogger(BotTest.class);

    @Before
    public void setUp() throws Exception {
    	AimlCharter.Init(System.getProperty("user.dir") + "/bots/testbot");
    	charter  = AimlCharter.getInstance();
        assertTrue(charter!=null);
    }

    /* test normal response*/
    @Test
    public void testNormalRespond() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "你今年多大了啊?";
        String correctRequest = "我今年16岁啦";
        String respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    /*test star*/
    @Test
    public void testStar() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "马云是谁啊";
        String correctRequest = "我不知道马云是谁额";
        String respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    /*test srai*/
    @Test
    public void testSrai() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "你几岁了啊?";
        String correctRequest = "我今年16岁啦";
        String respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        
    }
    
    /*test random*/
    @Test
    public void testRandom() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "你喜欢什么运动啊?";
        String respond = charter.process(request).trim();
        String[] answers = "跑步;游泳".split(";");
        boolean result = false;
        for (String answer : answers) {
            if (respond.equals(answer))
                result = true;
        }
        assertTrue("Request = " + request + ", Respond = " + respond, result);
    }
    
    /*test think ,set ,get*/
    @Test
    public void testGetSet() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "我叫金三顺";
        String respond = charter.process(request).trim();
        String correctRequest = "你好金三顺";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    /*test get set topic*/
    @Test
    public void testGetSetTopic() throws Exception {
        charter.setChatState(new ChatState("Human"));
        String request = "你喜欢什么";
        String respond = charter.process(request).trim();
        String correctRequest = "你当前说话的主题是:爱好";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    /*test topic*/
    @Test
    public void testTopic() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "你喜欢吃什么";
        charter.getChatState().setTopic("爱好");
        String respond = charter.process(request).trim();
        String correctRequest = "番茄炒蛋";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    /*test Multi-User manager*/
    @Test
    public void testMultiUser() throws Exception {
    	String correctRequest2;
    	//User 1
    	charter.setUser("Human1");
        String request = "我家在北京";
        String correctRequest = "哦哦知道啦";
        String respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        request = "刚说我家在哪里啊?";
        correctRequest = "你刚告诉我你家在"+charter.getChatState().getPredicates().get("home");	
        respond = charter.process(request).trim();
        correctRequest2 = respond;
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        //User 2
        charter.setUser("Human2");
        correctRequest = "你刚告诉我你家在";	
        respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        //change to User 1 again
        charter.setUser("Human1");
        correctRequest = "你刚告诉我你家在"+charter.getChatState().getPredicates().get("home");
        respond = charter.process(request).trim();
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest2));
    }
    
    
    @Test
    public void testMultiTemplates() throws Exception {
    	charter.setChatState(new ChatState("Human"));
    	//test template type "food"
		String request = "香肠怎么样啊";
    	String[] arr = new String[] {
    			"food",};
    	List<String> entities = Arrays.asList(arr);
    	String correctRespond = "香肠吃起来不错";
    	String respond = charter.process(request,entities).trim();
    	assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRespond));
    	//test template type "location"
    	request = "北京怎么样啊";
    	entities = Arrays.asList(new String[] {
    			"location",});
    	correctRespond = "北京风景很好";
    	respond = charter.process(request,entities).trim();
    	assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRespond));
    	//test template type "politician"
    	request = "习大大怎么样啊";
    	entities = Arrays.asList(new String[] {
    			"politician",});
    	correctRespond = "习大大为人民服务棒棒哒";
    	respond = charter.process(request,entities).trim();
    	assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRespond));
    	//test template type "default"
    	request = "这东西怎么样啊";
    	correctRespond = "我也不太清楚额";
    	respond = charter.process(request).trim();
    	assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRespond));
    }
    
    @Test
    public void testThat() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "天气怎么样";
        String respond = charter.process(request).trim();
        String correctRequest = "你问的是哪儿的天气呢";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        request = "北京";
        respond = charter.process(request).trim();
        correctRequest = "你现在是在北京吗?我帮你查查北京的天气哈";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
    
    @Test
    public void testCondition() throws Exception {
    	charter.setChatState(new ChatState("Human"));
        String request = "我到北京了";
        String respond = charter.process(request).trim();
        String correctRequest = "嗯我知道啦";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
        request = "我到杭州了";
        respond = charter.process(request).trim();
        correctRequest = "骗人！你明明在北京";
        assertTrue("Request = " + request + ", Respond = " + respond, respond.equals(correctRequest));
    }
}
