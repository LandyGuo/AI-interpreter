package com.alipay.aiml.core;

import com.alipay.aiml.App;
import com.alipay.aiml.consts.AimlConst;
import com.alipay.aiml.consts.AimlTag;
import com.alipay.aiml.consts.WildCard;
import com.alipay.aiml.consts.TemplateType;
import com.alipay.aiml.entity.AimlCategory;
import com.alipay.aiml.input.Entity;
import com.alipay.aiml.utils.AppUtils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The core AIML parser and interpreter.
 * Implements the AIML 2.0 specification as described in
 * AIML 2.0 Working Draft document
 * https://docs.google.com/document/d/1wNT25hJRyupcG51aO89UcQEiG-HkXRXusukADpFnDs4/pub
 * https://playground.pandorabots.com/en/tutorial/
 * http://blog.pandorabots.com/aiaas-aiml-2-0-support/
 * http://www.alicebot.org/documentation/aiml101.html
 * http://itc.ua/articles/kvest_tyuringa_7667/
 * http://www.eblong.com/zarf/markov/chan.c
 *
 * @author mujian
 */
public class AIMLProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AIMLProcessor.class);
	
    private final List<AimlCategory> categories;
    private final Map<String, Map<String, AimlCategory>> topics;
    private Map<String, String> predicates;
    private List<String> regxMatchContent;//匹配成功时，存储正则匹配的内容
    private int regxCnt = -1;//匹配成功时， 模板中通配符个数
    private String curInput,curTopic,curThat;//当前正在匹配的topic和that
    private String sraiLastMatchedPattern = null;//记录srai上次匹配上的pattern,避免死循环
    
    public AIMLProcessor(List<AimlCategory> categories) {
        this.predicates = new HashMap<String, String>();
        this.topics = convert(categories);
        this.categories = categories;
    }

    private Map<String, Map<String, AimlCategory>> convert(List<AimlCategory> categories) {
        Map<String, Map<String, AimlCategory>> topics = new HashMap<String, Map<String, AimlCategory>>();
        for(int i=0; i<categories.size(); i++)
        {
        	AimlCategory category = categories.get(i);
        	Map<String, AimlCategory> topicCategories;
            if (topics.containsKey(category.getTopic())) {
                topicCategories = topics.get(category.getTopic());
            } else {
                topicCategories = new HashMap<String, AimlCategory>();
                topics.put(category.getTopic(), topicCategories);
            }
            topicCategories.put(category.getPattern(), category);
        }
        return topics;
    }

    public String match(final String input, String topic, String that) {
    	String request = input.toUpperCase();
        //存储当前匹配的topic和that
    	this.curTopic = topic;
    	this.curThat = that;
    	this.curInput = request;
    	LOG.debug("Topic:{} that:{} input:{}",topic, that, request);
        Set<String> patterns = patterns(topic);
        for (String pattern : patterns) {
        	TemplateMatchResult<Boolean,List<String>> mr = isMatching(request, pattern);
            if (mr.isMatch.booleanValue())
            {
            	//找到匹配的模式，尝试进行that 匹配
            	AimlCategory cate = category(topic, pattern);
            	LOG.debug("Pattern匹配成功,进行That匹配: curThat"
            			+ "{} vs template that:{}",that,cate.getThat());
            	//Category 的that 不为 default,则必须要求that也匹配
            	if (cate.getThat().equals(AimlConst.default_that)||
            			cate.getThat().equals(that))
            	{
	            	//匹配成功,设置当前的匹配内容
	            	this.regxMatchContent = mr.matchCnt;
	            	this.regxCnt =getRegularCnt(pattern); 
	            	return pattern;
            	}
            }     
        }
        //把topic包含的patterns全部取出来
        patterns = patterns(AimlConst.default_topic);
        for (String pattern : patterns) {
        	TemplateMatchResult<Boolean,List<String>> mr = isMatching(request, pattern);
            if (mr.isMatch.booleanValue())
            {//找到匹配的模式，尝试进行that 匹配
            	
            	AimlCategory cate = category(AimlConst.default_topic, pattern);
            	//Category 的that 不为 default,则必须要求that也匹配
            	if (cate.getThat().equals(AimlConst.default_that)||
            			cate.getThat().equals(that))
            	{
	            	LOG.debug("Pattern匹配成功,That匹配成功: curThat"
	            			+ "{} vs template that:{}",that,cate.getThat() );
	            	
	            	//找到匹配的模式，设置当前的匹配内容
	            	this.regxMatchContent = mr.matchCnt;
	            	//获取匹配pattern中统配符号的个数
	            	this.regxCnt =getRegularCnt(pattern); 
	            	return pattern;
            	}
            }  
        }
        //没有找到匹配的模式,清空当前的匹配内容
        this.regxMatchContent = new ArrayList<String>();
        this.regxCnt = -1;
        return "";
    }

    public String template(String pattern, String topic, String that, Map<String, String> predicates,List<String> entities) {
    	onResponse();
    	this.predicates = predicates;
        AimlCategory category = category(topic, pattern);
        if (category == null)
            category = category(AimlConst.default_topic, WildCard.OneMore.get());
        if (category == null)
        	return AimlConst.default_bot_response;
        LOG.debug(String.format("find matched Pattern:%s",
        		category.getPattern()));
        //如果找到对应的category，根据实体识别的结果选择不同类型的模板进行返回
        Node template = category.getTemplate();//default的template
        TemplateType type = TemplateType.DEFAULT;//进行实体识别
        for(int i=0; i<entities.size();i++)
        {
        	try{
            	type = TemplateType.valueOf(entities.get(i).toUpperCase(Locale.ENGLISH));
                if(category.hasTemplateType(type.getName()))//根据实体识别的结果切换模板
                {
                	LOG.debug(String.format("实体识别切换模板类型：%s",type));
                	template = category.getTemplate(type.getName());
                	return getTemplateValue(template);
                }
        	}
        	catch(IllegalArgumentException e)//AIML未定义的模板类型
        	{
        		e.printStackTrace();
        		continue;
        	}

        }
        template = category.getTemplate(TemplateType.DEFAULT.getName());
        return getTemplateValue(template);
    }
    
    public void onResponse()
    {
    	//清空srai历史匹配结果
    	this.sraiLastMatchedPattern = null;
    }
    
    /*这里用于实现实体识别，对regxMatchContent中的内容进行实体识别，根据实体识别
     * 结果返回相应的类型*/
    public String recognize()
    {
    	String[] arr = new String[] {"default","food",
        		"location","politician"};
    	List<String> types = Arrays.asList(arr);
    	return AppUtils.getRandom(types);
    }

    public int getTopicCount() {
        return topics.size();
    }

    public int getCategoriesCount() {
        return categories.size();
    }

    //在这里获取匹配的内容
    private TemplateMatchResult<Boolean,List<String>> isMatching(String input, String pattern) {
    	LOG.debug("-------------1-------------------");
        input = input.trim();
        String regex_pattern = pattern.trim();
        //计算pattern里面有多少匹配内容(*和+)
        int cnt = getRegularCnt(pattern);
        //存储匹配内容
        List<String> matchCont = new ArrayList<String>();
        //+ -> ^
        regex_pattern = regex_pattern.replace(WildCard.ZeroMore.get(), "^");
        //* -> .+
        regex_pattern = regex_pattern.replace(WildCard.OneMore.get(), "(.+)");
        //^ -> .*
        regex_pattern = regex_pattern.replace("^", "(.*)");
        
//        regex_pattern = regex_pattern.replace(WildCard.ZeroMore.get(), "(.*)");
        regex_pattern = regex_pattern.replace(" ", "\\s*");//保留对英文的支持
        
        TemplateMatchResult<Boolean,List<String>> ret;
        
        LOG.debug("regex_pattern:"+regex_pattern);
        LOG.debug("input:"+input);
        
        if(!Pattern.matches(regex_pattern, input))
        	return new TemplateMatchResult<Boolean, List<String>>(Boolean.valueOf(false),matchCont);

        Pattern p = Pattern.compile(regex_pattern);
        Matcher  m = p.matcher(input);
        //获取matcher中的匹配内容
        while(m.find())
        {//匹配后，开始获取匹配内容
        	for(int i=1;i<=cnt;i++)
        	{
            	LOG.debug("pattern Match content:"+m.group(i));
            	matchCont.add(m.group(i));	
        	}
        }
        LOG.debug("-------------2-------------------");
        return new TemplateMatchResult<Boolean, List<String>>(Boolean.valueOf(true),matchCont);
    }
    
    //计算pattern里面有多少匹配内容(*和+)
    private int getRegularCnt(String pattern)
    {
    	int res = 0;
    	for(int i=0;i<pattern.length();i++)
    	{
    		String cur = pattern.substring(i,i+1);
    		if(cur.equals(WildCard.OneMore.get())||
    				cur.equals(WildCard.ZeroMore.get()))
    			res++;
    	}
    	return res;
    }

    private String getTemplateValue(Node node) {
        String result = getNodeValue(node);
        return result.isEmpty() ? AimlConst.default_bot_response : result;
    }
    
    private String getNodeValue(Node node)
    {
    	 String result = "";
         NodeList childNodes = node.getChildNodes();
         for (int i = 0; i < childNodes.getLength(); ++i)
             result += recurseParse(childNodes.item(i));
         return result;
    }

    //这个方法里面需要传入正则匹配的结果进来，以便进行star标签的替换
    //需要处理的工作:1.获取每个通配符正则匹配的结果 2.需要进行star标签的解析，因为其中涉及index参数的提取
    //其他需要做的工作:给每个category增加一个type标签，形成topic pattern type的三级topic索引
    //对通配符的匹配结果进行实体识别，并根据实体识别的类别确定当前的type
    private String recurseParse(Node node) {
        String nodeName = node.getNodeName();
        if(nodeName.equals(AimlTag.text))
        	return textParse(node);
        else if(nodeName.equals(AimlTag.template))
        	return getTemplateValue(node);
        else if(nodeName.equals(AimlTag.random))
        	return randomParse(node);
        else if(nodeName.equals(AimlTag.system))
        	return AppUtils.exeCmd(AppUtils.node2String(node));
        else if(nodeName.equals(AimlTag.srai))
        	return sraiParse(node);
        else if(nodeName.equals(AimlTag.set)){
        	setParse(node);
            return "";//set 标签永远不返回
        }
        else if(nodeName.equals(AimlTag.get)){
        	return getParse(node);
        }
        else if(nodeName.equals(AimlTag.think)){
            getTemplateValue(node);
            return "";//think 永远返回为空
        }
        else if(nodeName.equals(AimlTag.topic)){
            //Topic 为 category上级元素，出现在category内部时直接忽略
            return "";
        }
        else if(nodeName.equals(AimlTag.condition)){
        	//对condition标签的孩子进行处理
            return conditionParse(node);
        }
        else if(nodeName.equals(AimlTag.li)){
        	//只有当li标签的predicates中name属性对应的值和value值相等时，此
        	//li标签才返回值：<li name="city" value="北京"></li>
        	NamedNodeMap attrs = node.getAttributes();
        	LOG.debug("<li> TAG attrs length:{}",attrs.getLength());
        	if(attrs.getLength()==0){//没有name和value属性，表明是默认返回的li
        		//default <li></li>for condition
        		return getTemplateValue(node);
        	}
        	else if( attrs.getLength()==2){
                 Node name = attrs.getNamedItem("name");
                 Node value = attrs.getNamedItem("value");
                 if(name!=null&&value!=null){
                	 String attr = predicates.get(name.getNodeValue());
                	 LOG.debug("Tag <li> Attrs: name:{} predicate value:{}  value:{}",name.getNodeValue(),
                			 attr, value.getNodeValue() );
                	 if((attr==null && value.getNodeValue().isEmpty())||
                	    (attr!=null&&attr.equals(value.getNodeValue()))){
                		 return getTemplateValue(node);
                	 }
                 }
            }
            return "";//其它情况放弃解析
        }
        else if(nodeName.equals(AimlTag.star)){
        	//修改这里，让template里面的star标签可以被替换成匹配的内容
        	//pattern里面不包含正则匹配的内容，直接忽略star标签
        	if(this.regxCnt<=0) 
        		return "";
        	//获取star node 的 index属性
        	int index = 1;//index默认为1
        	NamedNodeMap attrs = node.getAttributes();
        	if(attrs.getLength()>0 && attrs!=null)
            {
                 Node attr = attrs.getNamedItem("index");
                 index = Integer.parseInt(attr.getNodeValue());
            }
        	//检查index是否在合理范围内,返回index的值
        	if(index<=this.regxCnt && index>=1)
        		return this.regxMatchContent.get(index-1);
        	return "";
        }
        return "";
        
        /*
        switch (nodeName) {
            case AimlTag.text:
                return textParse(node);
            case AimlTag.template:
                return getTemplateValue(node);
            case AimlTag.random:
                return randomParse(node);
            case AimlTag.system:
            	return AppUtils.exeCmd(AppUtils.node2String(node));
            case AimlTag.srai:
                return sraiParse(node);
            case AimlTag.set:
                setParse(node);
                return "";
            case AimlTag.think:
                getTemplateValue(node);
                return "";
            case AimlTag.star:
            	//修改这里，让template里面的star标签可以被替换成匹配的内容
            	//pattern里面不包含正则匹配的内容，直接忽略star标签
            	if(this.regxCnt<=0) 
            		return "";
            	//获取star node 的 index属性
            	int index = 1;//index默认为1
	        	NamedNodeMap attrs = node.getAttributes();
	        	if(attrs.getLength()>0 && attrs!=null)
	            {
	                 Node attr = attrs.getNamedItem("index");
	                 index = Integer.parseInt(attr.getNodeValue());
	            }
	        	//检查index是否在合理范围内,返回index的值
	        	if(index<=this.regxCnt && index>=1)
	        		return this.regxMatchContent.get(index-1);
	        	return "";
        }
        return "";
        */
    }

    private String textParse(Node node) {
        return node.getNodeValue().replaceAll("(\r\n|\n\r|\r|\n)", "").replaceAll("  ", " ");
    }

    private void setParse(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes.getLength() > 0) {
            Node node1 = attributes.getNamedItem("name");
            String key = node1.getNodeValue();//属性
            String value = getTemplateValue(node);//值
            LOG.debug("setParse save predicate:{}->{}",key,value);
            predicates.put(key, value);
        }
    }
    
    private String getParse(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes.getLength() > 0) {
            Node node1 = attributes.getNamedItem("name");
            String key = node1.getNodeValue();
            String val = predicates.get(key);
            LOG.debug("getParse predicate key:{} get value:{}",key,val);
            return val!=null?val:"";
        }
        return "";
    }

    private String sraiParse(Node node) {
    	//获取srai节点内部的内容，支持srai里面有正则匹配符号
    	String val = this.getNodeValue(node);
    	//update current topic if topic changes before srai
    	String updatedTopic = predicates.get("topic");
    	this.curTopic = updatedTopic != null?updatedTopic:curTopic;
    	LOG.debug(String.format("重定向到pattern:%s topic：%s that:%s",
    			val, this.curTopic, this.curThat));
    	
    	//将srai里面的内容重新进行匹配，获取对应的pattern
    	String pattern = this.match(val,this.curTopic, this.curThat);
        if(pattern.equals(this.sraiLastMatchedPattern))
        	return "";
        this.sraiLastMatchedPattern = pattern;
    	//根据topic和pattern,获取对应的category
    	AimlCategory category = category(this.curTopic, pattern);
    	
//    	AimlCategory category = category(AimlConst.default_topic, AppUtils.node2String(node));
        return category != null ? getTemplateValue(category.getTemplate()) : AimlConst.error_bot_response;
    }

    private String randomParse(Node node) {
        List<String> values = new ArrayList<String>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            if (childNodes.item(i).getNodeName().equals(AimlTag.li))
            {
//            	values.add(AppUtils.node2String(childNodes.item(i)));
            	values.add(getNodeValue(childNodes.item(i)));
            }   
        }
        return AppUtils.getRandom(values);
    }
    

    private String conditionParse(Node node) {
    	String defaultvalue = "";
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            if (childNodes.item(i).getNodeName().equals(AimlTag.li))
            {
            	Node liNode = childNodes.item(i);
            	//判断liNode是否为condition缺省节点：<li></li>
            	NamedNodeMap attrs = liNode.getAttributes();
            	String nodeVal = recurseParse(liNode);
            	if(attrs.getLength()==0)
            		defaultvalue = nodeVal;
            	else if(!nodeVal.isEmpty())
            		return nodeVal;
            }   
        }
        return defaultvalue;
    }

    private Set<String> patterns(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic).keySet();
        } else {
            topics.put(topic, new HashMap<String, AimlCategory>());
            return Collections.emptySet();
        }
    }

    private AimlCategory category(String topic, String pattern) {
        return topics.get(topic).get(pattern);
    }
}