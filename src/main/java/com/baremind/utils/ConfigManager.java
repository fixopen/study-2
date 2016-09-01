package com.baremind.utils;

import com.baremind.algorithm.Config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fixopen on 4/6/15.
 */
public class ConfigManager {
    public static Config parseConfig(String configFilePath) {
        Config result = new Config();

        try {
            Properties props = new Properties();
            InputStream in = new FileInputStream(configFilePath);
            props.load(in);
            in.close();
            result.BOOKS = props.getProperty("BOOKS");
            result.COVERS = props.getProperty("COVERS");
            result.ZIP_FILES = props.getProperty("ZIP_FILES");
            result.ZIP_TEMPORARY = props.getProperty("ZIP_TEMPORARY");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void saveConfig(Config config, String configFilePath) {
    }
}
