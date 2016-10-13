package com.cv;

import com.cv.watson.Calculator;
import com.cv.watson.Drawer;
import com.cv.watson.Handler;

import java.io.*;
import java.util.*;

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

        Map<String, List<Double>> map = Handler.classify(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime);
//        Map<String, List<Double>> map = Handler.handle(apiKey, positiveDirectoryPath, negativeDirectoryPath, count, classifierName, testCount, sleepTime);
        Set<String> keys = map.keySet();
        for (String key : keys) {
            System.out.println("---------------" + key + "----------------------");
            for (Double score : map.get(key)) {
                System.out.println(score);
            }
        }
        double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), Calculator.FLAG_TPR, offset);
        double[] fnrs = Calculator.calculateRates(map.get(Handler.NEGATIVE), Calculator.FLAG_FNR, offset);
//        List<Double> positiveScores = Arrays.asList(0.0602488, 0.129214, 0.127466, 0.109041, 0.0472907, 0.0585011, 0.0440812, 0.0430361, 0.0517429, 0.0581118);
//        List<Double> negativeScores = Arrays.asList(0.946686, 0.877077, 0.90731, 0.962736, 0.060238, 0.0562582, 0.0614881, 0.0484286, 0.0514426, 0.130368);
//        double[] tprs = Calculator.calculateRates(positiveScores, Calculator.FLAG_TPR, offset);
//        double[] fnrs = Calculator.calculateRates(negativeScores, Calculator.FLAG_FNR, offset);
        for (int i = 0; i < 1000; i++) {
            System.out.println(tprs[i] + "," + fnrs[i]);
        }
        System.out.println("{" + "\"positives\": " + map.get(Handler.POSITIVE) + "," + "\"negatives\": " + map.get(Handler.NEGATIVE) + "}");
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
