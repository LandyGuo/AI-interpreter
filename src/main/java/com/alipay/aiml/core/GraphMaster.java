package com.alipay.aiml.core;

import com.alipay.aiml.entity.AimlMap;
import com.alipay.aiml.entity.AimlSet;
import com.alipay.aiml.entity.AimlSubstitution;
import com.alipay.aiml.entity.AimlCategory;
import com.alipay.aiml.input.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The AIML Pattern matching algorithm and data structure.
 * Brain of bot.
 *
 * @author mujian

 */
public class GraphMaster {
    private final Map<String, AimlSet> sets;
    private final Map<String, AimlMap> maps;
    private final Map<String, AimlSubstitution> substitutions;
    private final AIMLProcessor processor;

    public GraphMaster(List<AimlCategory> categories, Map<String, AimlSet> sets, Map<String, AimlMap> maps,
                       Map<String, AimlSubstitution> substitutions) {
        this.sets = sets;
        this.maps = maps;
        this.substitutions = substitutions;
        this.processor = new AIMLProcessor(categories);
    }

    public String getStat() {
        return "Brain contain "
                + processor.getTopicCount() + " topics, "
                + processor.getCategoriesCount() + " categories, "
                + sets.size() + " sets, "
                + maps.size() + " maps, "
                + substitutions.size() + " substitutions.";
    }

    /**
     * Split an input into an array of sentences based on sentence-splitting characters.
     *
     * @param line input text
     * @return array of sentences
     */
    public String[] sentenceSplit(String line) {
        line = line.replace("，", ",")
        		 .replace("。", ".")
                .replace("？", "?")
                .replace("！", "!")
                .replaceAll("(\r\n|\n\r|\r|\n)", " ");
        String[] result = line.split("[,.!?]");
        for (int i = 0; i < result.length; i++)
            result[i] = result[i].trim();
        return result;
    }
    
//    public String respond(String pattern, String topic, String that, Map<String, String> predicates) {
//        return respond(pattern, topic, that, predicates, new ArrayList<String>());
//    }
    
    public String respond(String pattern, String topic, String that, Map<String, String> predicates,
    		List<String> entities) {
        return processor.template(pattern, topic, that, predicates, entities);
    }

    public String match(String request, String topic, String that) {
        return processor.match(request, topic, that);
    }
}
