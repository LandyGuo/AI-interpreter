package com.alipay.aiml.bot;

import com.alipay.aiml.chat.ChatState;
import com.alipay.aiml.consts.AimlConst;
import com.alipay.aiml.core.GraphMaster;
import com.alipay.aiml.core.Named;
import com.alipay.aiml.core.Countable;
import com.alipay.aiml.entity.AimlMap;
import com.alipay.aiml.entity.AimlSet;
import com.alipay.aiml.entity.AimlSubstitution;
import com.alipay.aiml.entity.AimlCategory;
import com.alipay.aiml.input.Entity;
import com.alipay.aiml.loaders.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class representing the AIML bot
 *
 * @author mujian
 */
public class Bot implements Named {

    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);

    private GraphMaster brain;
    private BotInfo botInfo;
    private String rootDir;
    private String name;

    public Bot(String name, String rootDir) {
        this.name = name;
        this.rootDir = rootDir;
        this.botInfo = new BotConfiguration(rootDir);
        //loadAiml():加载所有aiml文件的category,其中category中的template以node形式存储
        brain = new GraphMaster(loadAiml(), loadSets(), loadMaps(), loadSubstitutions());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrainStats() {
        return brain.getStat();
    }

    public String multisentenceRespond(String request, ChatState state) {
    	return multisentenceRespond(request,state,new ArrayList<String>());
    }
    
    public String multisentenceRespond(String request, ChatState state, List<String> entities) {
        String[] sentences = brain.sentenceSplit(request);
        String response = "";
        for (String sentence : sentences)
            response += " " + respond(sentence, state, entities);
        return response.trim();
    }

//    public String respond(String request, ChatState state) {
//    	return respond(request,state,new ArrayList<String>() );
//    }
//    
    public String respond(String request, ChatState state, List<String> entities) {
    	//先查找匹配上的pattern
        String pattern = brain.match(request, state.topic(), state.that());
        //根据匹配上的pattern,确定返回结果
        if(pattern.isEmpty())
        	return "";
        LOG.debug("Find Match Pattern:{}",pattern);
        return brain.respond(pattern, state.topic(), state.that(), state.getPredicates(), entities);
    }

    public boolean wakeUp() {
        return validate(getRootDir()) && validate(getAimlFolder());
    }

    private List<AimlCategory> loadAiml() {
        AimlLoader loader = new AimlLoader();
        return loader.loadFiles(getAimlFolder());
    }

    private Map<String, AimlSet> loadSets() {

        File sets = new File(getSetsFolder());
        if (!sets.exists()) {
            LOG.warn("Sets not found!");
            return Collections.emptyMap();
        }
        File[] files = sets.listFiles();
        if (files == null || files.length == 0)
            return Collections.emptyMap();

        FileLoader<AimlSet> loader = new SetLoader();

        final Map<String, AimlSet> data = loader.loadAll(files);
        //int count = data.keySet().stream().mapToInt(s -> data.get(s).size()).sum();
        int count = countValuesOfMap(data);
        LOG.info("Loaded {} set records from {} files.", count, files.length);
        return data;
    }
    
    private int countValuesOfMap(final Map<String, ? extends Countable> data)
    {
    	Iterator<String>it = data.keySet().iterator();
    	int count = 0;
    	while(it.hasNext())
    	{
    		String k = it.next();
    		count += data.get(k).getSize();
    	}
    	return count;
    }

    private Map<String, AimlMap> loadMaps() {

        File maps = new File(getMapsFolder());
        if (!maps.exists()) {
            LOG.warn("Maps not found!");
            return Collections.emptyMap();
        }
        File[] files = maps.listFiles();
        if (files == null || files.length == 0) return Collections.emptyMap();


        FileLoader<AimlMap> loader = new MapLoader<AimlMap>();

        final Map<String, AimlMap> data = loader.loadAll(files);
        //int count = data.keySet().stream().mapToInt(s -> data.get(s).size()).sum();
        int count = countValuesOfMap(data);
        LOG.info("Loaded " + count + " map records from " + files.length + " files.");
        return data;
    }

    private Map<String, AimlSubstitution> loadSubstitutions() {

        File maps = new File(getSubstitutionsFolder());
        if (!maps.exists()) {
            LOG.warn("Maps not found!");
            return Collections.emptyMap();
        }
        File[] files = maps.listFiles();
        if (files == null || files.length == 0)
            return Collections.emptyMap();


        FileLoader<AimlSubstitution> loader = new SubstitutionLoader();

        final Map<String, AimlSubstitution> data = loader.loadAll(files);
        //int count = data.keySet().stream().mapToInt(s -> data.get(s).size()).sum();
        int count = countValuesOfMap(data);
        LOG.info("Loaded " + count + " substitutions from " + files.length + " files.");
        return data;
    }

    private boolean validate(String folder) {
        if (folder == null || folder.isEmpty())
            return false;
//        Path botsFolder = Paths.get(folder);
        File file=new File(folder); 
        if (!file.exists()) {
            LOG.warn("Bot folder " + folder + " not found!");
            return false;
        }
        return true;
    }

    private String getRootDir() {
        return rootDir;
    }

    private String getAimlFolder() {
        return getRootDir() + "aiml";
    }

    private String getSubstitutionsFolder() {
        return getRootDir() + "substitutions";
    }

    private String getSetsFolder() {
        return getRootDir() + "sets";
    }

    private String getMapsFolder() {
        return getRootDir() + "maps";
    }

}
