package com.cv;

import com.cv.watson.Calculator;
import com.cv.watson.Drawer;
import com.cv.watson.Handler;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        File info = new File(System.getProperty("user.dir") + "\\info.properties");
        Properties properties = getProperties(info);
        String apiKey = properties.getProperty("API_KEY");
        double offset = Double.valueOf(properties.getProperty("OFFSET"));
        int count = Integer.valueOf(properties.getProperty("COUNT"));
        String classifierName = properties.getProperty("CLASSIFIER_NAME");
        int testCount = Integer.valueOf(properties.getProperty("TEST_COUNT"));
        int plotWidth = Integer.valueOf(properties.getProperty("PLOT_WIDTH"));
        int plotHeight = Integer.valueOf(properties.getProperty("PLOT_HEIGHT"));
        String plotTitle = properties.getProperty("PLOT_TITLE");
        String plotName = properties.getProperty("PLOT_NAME");
        long sleepTime = Long.valueOf(properties.getProperty("SLEEP_TIME"));

        String positiveDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive";
        String negativeDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";

        if (properties.containsKey("POSITIVE_DIRECTORY_PATH")) {
            positiveDirectoryPath = properties.getProperty("POSITIVE_DIRECTORY_PATH");
        }
        if (properties.containsKey("NEGATIVE_DIRECTORY_PATH")) {
            positiveDirectoryPath = properties.getProperty("NEGATIVE_DIRECTORY_PATH");
        }

        Map<String, List<Double>> map = Handler.handle(apiKey, positiveDirectoryPath, negativeDirectoryPath, offset, count, classifierName, testCount, sleepTime);
        System.out.println(map);
        Set<String> keys = map.keySet();
        System.out.println("scores:");
        for (String key : keys) {
            System.out.println("---------------" + key + "----------------------");
            for (Double score : map.get(key)) {
                System.out.println(score);
            }
        }
        double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), Calculator.FLAG_TPR, offset);
        double[] fnrs = Calculator.calculateRates(map.get(Handler.NEGATIVE), Calculator.FLAG_FNR, offset);

        Drawer.draw(plotName, plotTitle, plotWidth, plotHeight, tprs, fnrs);
    }

    public static Properties getProperties(File info) throws IOException {
        Properties properties = new Properties();
        InputStream fis = new FileInputStream(info);
        properties.load(fis);
        System.out.println(properties);
        return properties;
    }

}
