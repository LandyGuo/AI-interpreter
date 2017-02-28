package com.alipay.aiml.loaders;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Load root element from xml file
 * Created by mujian on 25/10/16.
 *
 * @author mujian
 */
public class XmlLoader implements FileLoader<Element> {
//    private static final Logger LOG = LoggerFactory.getLogger(XmlLoader.class);

    @Override
    public Element load(File file) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException e){
            e.printStackTrace();
        }
        catch (SAXException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        if (doc == null) 
        {
//        	LOG.debug("返回为空");
        	return null;
        }

        Element rootElement = doc.getDocumentElement();
        rootElement.normalize();//合并相邻的文本节点为一个文本节点
//        LOG.debug("root Element:"+rootElement);
        return rootElement;
    }

    @Override
    public Map<String, Element> loadAll(File... files) {
        Map<String, Element> data = new HashMap<String, Element>();
        for (File file : files)
            data.put(file.getName(), load(file));
//        LOG.info("Loaded {} files", data.size());
        return data;
    }
}
