package com.alipay.aiml.bot;

import com.alipay.aiml.consts.AimlConst;

import java.io.File;

/**
 * Created by mujian on 19/10/16.
 *
 * @author mujian
 */
public class BotRepository {
    private static String rootDir = AimlConst.getRootPath();

    public static void setRootPath(String path) {
        rootDir = path;
    }

    public static Bot get() {
        return get(AimlConst.default_bot_name);
    }

    public static Bot get(String name) {
        return new Bot(name, getBotPath(name));
    }

    private static String getBotPath(String name) {
        return rootDir + File.separator;
    }
}
