package com.alipay.aiml.loaders;

import com.alipay.aiml.entity.AimlSubstitution;

import java.io.File;
import java.util.Map;

/**
 * Created by mujian on 19/10/16.
 *
 * @author mujian
 */
public class SubstitutionLoader extends MapLoader<AimlSubstitution> {
    @Override
    public AimlSubstitution load(File file) {
        return super.load(file);
    }

    @Override
    public Map<String, AimlSubstitution> loadAll(File... files) {
        return super.loadAll(files);
    }

    @Override
    protected Map<String, String> loadFile(File file) {
        return super.loadFile(file);
    }

    @Override
    protected void parseRow(final Map<String, String> data, final String row) {
        String[] splitStr = row.toUpperCase().trim().split(",");
        String first = splitStr[0];
        String second = splitStr[1];
        if (first.length() >= 2 && second.length() >= 2) data.put(removeBraces(first), removeBraces(second));
    }

    private String removeBraces(String value) {
        return value.substring(1, value.length() - 2).trim();
    }
}
