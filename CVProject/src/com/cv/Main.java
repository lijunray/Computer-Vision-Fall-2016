package com.cv;

import com.cv.watson.Calculator;
import com.cv.watson.Drawer;
import com.cv.watson.Handler;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
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
        String format = properties.getProperty("FORMAT");

        String[] anglesString = properties.getProperty("ANGLES").split(" ");
        int[] angles = new int[anglesString.length];
        for (int i = 0; i < angles.length; i++) {
            angles[i] = Integer.valueOf(anglesString[i]);
        }

        int CITimes = 50;
        double CIRate = 0.8;
        if (properties.containsKey("CI_TIMES")) {
            CITimes = Integer.valueOf(properties.getProperty("CI_TIMES"));
        }
        if (properties.containsKey("CI_Rate")) {
            CIRate = Double.valueOf(properties.getProperty("CI_RATE"));
        }

        String positiveDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive";
        String negativeDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";

        if (properties.containsKey("POSITIVE_DIRECTORY_PATH")) {
            positiveDirectoryPath = properties.getProperty("POSITIVE_DIRECTORY_PATH");
        }
        if (properties.containsKey("NEGATIVE_DIRECTORY_PATH")) {
            positiveDirectoryPath = properties.getProperty("NEGATIVE_DIRECTORY_PATH");
        }
        try {
//            Map<String, List<Double>> map = Handler.handle(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime);
////            Map<String, List<Double>> map = Handler.handle(apiKey, positiveDirectoryPath, negativeDirectoryPath, count, classifierName, testCount, sleepTime);
//            Set<String> keys = map.keySet();
//            for (String key : keys) {
//                System.out.println("---------------" + key + "----------------------");
//                for (Double score : map.get(key)) {
//                    System.out.println(score);
//                }
//            }
//            double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), offset);
//            double[] fprs = Calculator.calculateRates(map.get(Handler.NEGATIVE), offset);
//            System.out.println("scores: {" + "\"positives\": " + map.get(Handler.POSITIVE) + "," + "\"negatives\": " + map.get(Handler.NEGATIVE) + "}");
//            Drawer.draw(plotName, plotTitle, plotWidth, plotHeight, fprs, tprs);
            Handler.getCI(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime, CITimes, offset, CIRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(File info) throws IOException {
        Properties properties = new Properties();
        InputStream fis = new FileInputStream(info);
        properties.load(fis);
        System.out.println(properties);
        return properties;
    }

}
