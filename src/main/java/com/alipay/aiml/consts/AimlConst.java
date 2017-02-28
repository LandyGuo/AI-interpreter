package com.alipay.aiml.consts;

import java.io.File;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.alipay.aiml.App;

/**
 * Created by mujian on 6/17/15.
 *
 * @author mujian
 */
public class AimlConst {
	
//	private static final Logger LOG = LoggerFactory.getLogger(AimlConst.class);
	

    private static String root_path = System.getProperty("user.dir") + File.separator + "bots";
    public static final String default_bot_name = "Walle";
    public static final String aiml_file_suffix = ".aiml";
    public static final String error_bot_response = "Something is wrong with my brain.";
    public static final String default_bot_response = "";
    public static final String default_topic = "unknown";
    public static final String default_that = "unknown";
    public static final String null_input = "#NORESP";
    public static final String test_boot_name = "testbot";
    public static boolean debug = false;

    public static String getRootPath() {
//    	LOG.debug("load aiml file info:");
//    	LOG.debug("root dir:"+System.getProperty("user.dir"));
        return root_path;
    }

    public static void setRootPath(String newRootPath) {
        root_path = newRootPath;
    }
}