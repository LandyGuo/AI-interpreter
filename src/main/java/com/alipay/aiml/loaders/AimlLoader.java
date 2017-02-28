package com.alipay.aiml.loaders;

import com.alipay.aiml.consts.AimlConst;
import com.alipay.aiml.consts.AimlTag;
import com.alipay.aiml.entity.AimlCategory;
import com.alipay.aiml.utils.AppUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mujian on 21/06/15.
 *
 * @author mujian
 */
public class AimlLoader {

//    private static final Logger LOG = LoggerFactory.getLogger(AimlLoader.class);
    private final XmlLoader loader;

    public AimlLoader() {
        this.loader = new XmlLoader();
    }

    /**
     * ���ذ���aiml�ļ����ļ����µ�����AIML�ļ�
     *
     * @param aimlDir folder contain all aiml files
     * @return list of loaded categories
     */
    public List<AimlCategory> loadFiles(String aimlDir) {

        List<AimlCategory> categories = new ArrayList<AimlCategory>();
        File aimls = new File(aimlDir);
        File[] files = aimls.listFiles();
        if (files == null || files.length == 0) {
//            LOG.warn("Not files in folder {} ", aimls.getAbsolutePath());
            return categories;
        }
        int countNotAimlFiles = 0;
        for (File file : files) {
            if (file.getName().endsWith(AimlConst.aiml_file_suffix))
                categories.addAll(loadFile(file));
            else
                ++countNotAimlFiles;
        }
//        if (countNotAimlFiles != 0)
//            LOG.warn("Founded {} not aiml files in folder {}", countNotAimlFiles, aimlDir);
//        LOG.info("Loaded {} categories", categories.size());
        return categories;
    }

    /**
     * Loading single aiml file
     *
     * @param aimlFile aiml file
     */
    private List<AimlCategory> loadFile(File aimlFile) {
    	//��ȡaiml�ļ��ĸ��ڵ㣬ÿ��aiml�ļ���������ֻ��һ��aiml��ǩ���������
    	//aiml��ǩ(dom���ĸ��ڵ�)
        Element aimlRoot = loader.load(aimlFile);
        if (aimlRoot == null)
            return Collections.emptyList();

        if (!aimlRoot.getNodeName().equals(AimlTag.aiml)) {
//            LOG.warn(aimlFile.getName() + " is not AIML file");
            return Collections.emptyList();
        }
        String aimlVersion = aimlRoot.getAttribute("version");
//        LOG.debug("Load aiml " + aimlFile.getName() + (aimlVersion.isEmpty() ? "" : " [v." + aimlVersion + "]"));
        //��ȡaiml���ڵ㣬
//        LOG.debug("aiml children  length:"+aimlRoot.getChildNodes().getLength());
        NodeList chilren  = aimlRoot.getChildNodes();
//        for(int i=0; i<chilren.getLength();i++)
//        {
//        	LOG.debug("Node:"+i+"\t"+"content:"+chilren.item(i));
//        }
        //��ȡ����category������
        return aimlParser(aimlRoot.getChildNodes());
    }

    /**
     * ��Aiml�ļ��е�ֱ��category��topic�����categoryȫ������ȡ����;
     * category��topic,that,Ĭ��Ϊunkown,patternΪpattern�а������ַ�����template��
     * Node����ʽ�洢
     * @param nodes
     * @return
     */
    private List<AimlCategory> aimlParser(NodeList nodes) {
        List<AimlCategory> categories = new ArrayList<AimlCategory>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);

            String nodeName = node.getNodeName();
            
            if(nodeName.equals(AimlTag.text)||
            		nodeName.equals(AimlTag.comment))
            	continue;
            else if(nodeName.equals(AimlTag.topic))
            {
            	categories.addAll(parseTopic(node));
            	continue;
            }
            else if(nodeName.equals(AimlTag.category))
            {
                if (!categories.add(parseCategory(node)))
//                    LOG.debug(AppUtils.node2String(node));
                continue;
            }
            else
            {
//            	LOG.warn("Wrong structure: <aiml> tag contain " + nodeName + " tag");
            }
        }
//        LOG.debug("categories Length:[{}]",categories.size());
            return categories;
            
            
            
//            switch (nodeName) {
//                case AimlTag.text:
//                case AimlTag.comment:
//                    break;
//                case AimlTag.topic:
//                	//��topic�е�����category���������
//                    categories.addAll(parseTopic(node));
//                    break;
//                case AimlTag.category:
//                    if (!categories.add(parseCategory(node)))
//                        LOG.debug(AppUtils.node2String(node));
//                    break;
//                default:
//                    LOG.warn("Wrong structure: <aiml> tag contain " + nodeName + " tag");
//            }
//        }
//        return categories;
    }

    //��topic�����categoryȫ����ȡ����
    private List<AimlCategory> parseTopic(Node node) {
        List<AimlCategory> categories = new ArrayList<AimlCategory>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            String childNodeName = childNodes.item(i).getNodeName();
            
            if(childNodeName.equals(AimlTag.text)||
            		childNodeName.equals(AimlTag.comment))
            	continue;
            else if(childNodeName.equals(AimlTag.category))
            {
            	categories.add(parseCategory(childNodes.item(i), getAttribute(node, AimlTag.name)));
            	continue;
            }
            else{
//            	LOG.warn("Wrong structure: <topic> tag contain " + childNodeName + " tag");
            }
            
            
//            switch (childNodeName) {
//                case AimlTag.text:
//                case AimlTag.comment:
//                    break;
//                case AimlTag.category:
//                    categories.add(parseCategory(childNodes.item(i), getAttribute(node, AimlTag.name)));
//                    break;
//                default:
//                    LOG.warn("Wrong structure: <topic> tag contain " + childNodeName + " tag");
//            }
        }
        return categories;
    }

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    private AimlCategory parseCategory(Node node) {
        return parseCategory(node, AimlConst.default_topic);
    }

    //��ȡһ��category���������
    private AimlCategory parseCategory(Node node, String topic) {
        AimlCategory category = new AimlCategory();
        category.setTopic(topic);
        category.setThat(AimlConst.default_that);
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            String childNodeName = childNodes.item(i).getNodeName();
            
            if(childNodeName.equals(AimlTag.text)||
            		childNodeName.equals(AimlTag.comment))
            	continue;
            else if(childNodeName.equals(AimlTag.pattern))
            {
            	category.setPattern(AppUtils.node2String(childNodes.item(i)));
            	continue;
            }
            else if(childNodeName.equals(AimlTag.template))
            {
              	//解析template节点的type
            	String type = "default";
            	try{
            		type = getAttribute(childNodes.item(i),AimlCategory.DEFAULT_TYPE_FIELD);
            	}
            	catch(NullPointerException e)
            	{
            		type = null;
            	}
            	
//            	LOG.debug(String.format("template type: %s",type));
            	if(type != null)
            		category.addTemplate(childNodes.item(i),type);
            	else
            		category.addTemplate(childNodes.item(i));
            	continue;
            }
            else if(childNodeName.equals(AimlTag.topic))
            {
            	category.setTopic(AppUtils.node2String(childNodes.item(i)));
            	continue;
            }
            else if(childNodeName.equals(AimlTag.that))
            {
                category.setThat(AppUtils.node2String(childNodes.item(i)));
                continue;
            }
            else{
//            	LOG.warn("Wrong structure: <category> tag contain " + childNodeName + " tag");
            }
            
            
//            switch (childNodeName) {
//                case AimlTag.text:
//                case AimlTag.comment:
//                    break;
//                case AimlTag.pattern:
//                	//ȥ��pattern��ǩ����ȡ����������ı���topic��that�Ĵ�������ͬ
//                    category.setPattern(AppUtils.node2String(childNodes.item(i)));
//                    break;
//                case AimlTag.template:
//                	//解析template节点的type
//                	String type = "default";
//                	try{
//                		type = getAttribute(childNodes.item(i),AimlCategory.DEFAULT_TYPE_FIELD);
//                	}
//                	catch(NullPointerException e)
//                	{
//                		type = null;
//                	}
//                	
//                	LOG.debug(String.format("template type: %s",type));
//                	if(type != null)
//                		category.addTemplate(childNodes.item(i),type);
//                	else
//                		category.addTemplate(childNodes.item(i));
//                    break;
//                case AimlTag.topic:
//                    category.setTopic(AppUtils.node2String(childNodes.item(i)));
//                    break;
//                case AimlTag.that:
//                    category.setThat(AppUtils.node2String(childNodes.item(i)));
//                    break;
//                default:
//                    LOG.warn("Wrong structure: <category> tag contain " + childNodeName + " tag");
//            }
        }
        return category;
    }
}
