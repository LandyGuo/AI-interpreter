package com.alipay.aiml.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mujian on 19/10/16.
 *
 * @author mujian
 */
public class BotConfiguration implements BotInfo {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);

    private static final String PROPERTIES = "bot.properties";

    private String rootDir;
    private Properties prop;

    public BotConfiguration(String rootDir) {
        this.rootDir = rootDir;
        this.prop = loadBotInfo(getSystemConfig());
    }

    @Override
    public String getFirstname() {
        return getValue("firstname");
    }

    @Override
    public String getLastname() {
        return getValue("lastname");
    }

    @Override
    public String getLanguage() {
        return getValue("language");
    }

    @Override
    public String getEmail() {
        return getValue("email");
    }

    @Override
    public String getGender() {
        return getValue("gender");
    }

    @Override
    public String getVersion() {
        return getValue("version");
    }

    @Override
    public String getBirthplace() {
        return getValue("birthplace");
    }

    @Override
    public String getJob() {
        return getValue("job");
    }

    @Override
    public String getSpecies() {
        return getValue("species");
    }

    @Override
    public String getBirthday() {
        return getValue("birthday");
    }

    @Override
    public String getBirthdate() {
        return getValue("birthdate");
    }

    @Override
    public String getSign() {
        return getValue("sign");
    }

    @Override
    public String getReligion() {
        return getValue("religion");
    }

    @Override
    public String getBotmaster() {
        return getValue("botmaster");
    }

    //��ȡbot.properties�ļ�����Ϊ��ֵ�Դ洢
    private Properties loadBotInfo(String path) {
        LOG.debug("Load system config: " + path);
        Properties prop = new Properties();
        FileInputStream in = null;
        try{
        	in = new FileInputStream(path);
            prop.load(in);
        } catch (IOException e) {
           // e.printStackTrace();
        }
        return prop;
    }

    //��ȡ�����������ļ�:system/bot.properties�ļ�
    private String getSystemConfig() {
        return rootDir + "system" + File.separator + PROPERTIES;
    }

    private String getValue(String key) {
        return prop == null ? "" : prop.getProperty(key, "");
    }
}
