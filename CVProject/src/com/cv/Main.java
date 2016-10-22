package com.cv;

import com.cv.watson.Calculator;
import com.cv.watson.Drawer;
import com.cv.watson.Handler;
import com.cv.watson.Persistencer;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        File info = new File(System.getProperty("user.dir") + "\\info.properties");
        Properties properties = getProperties(info);
        String apiKey = properties.getProperty("API_KEY");
        double offset = Double.valueOf(properties.getProperty("OFFSET"));
        int count = Integer.valueOf(properties.getProperty("COUNT"));
        String classifierName = properties.getProperty("CLASSIFIER_NAME");
        int positiveTestCount = Integer.valueOf(properties.getProperty("POSITIVE_TEST_COUNT"));
        int negativeTestCount = Integer.valueOf(properties.getProperty("NEGATIVE_TEST_COUNT"));
        int plotWidth = Integer.valueOf(properties.getProperty("PLOT_WIDTH"));
        int plotHeight = Integer.valueOf(properties.getProperty("PLOT_HEIGHT"));
        String plotTitle = properties.getProperty("PLOT_TITLE");
        String plotName = properties.getProperty("PLOT_NAME");
        long sleepTime = Long.valueOf(properties.getProperty("SLEEP_TIME"));
        String format = properties.getProperty("FORMAT");

//        String[] anglesString = properties.getProperty("ANGLES").split(" ");
//        int[] angles = new int[anglesString.length];
//        for (int i = 0; i < angles.length; i++) {
//            angles[i] = Integer.valueOf(anglesString[i]);
//        }

        int[] angles = new int[10];
        for (int i = 0; i < 10; i++) {
            angles[i] = (i + 1) * 18;
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
        getScores(apiKey, sleepTime, positiveDirectoryPath, negativeDirectoryPath, count, classifierName, positiveTestCount, negativeTestCount, format, plotName, plotTitle, plotWidth, plotHeight, offset, angles);
    }

    private static void getScores(String apiKey, long sleepTime, String positiveDirectoryPath,
                                  String negativeDirectoryPath, int count, String classifierName,
                                  int positiveTestCount, int negativeTestCount, String format, String plotName,
                                  String plotTitle, int plotWidth, int plotHeight, double offset, int... angles) {
        try {
            // Train normal classifier
//            Handler.trainNormal(apiKey, positiveDirectoryPath, negativeDirectoryPath, count,
//                    classifierName, positiveTestCount, negativeTestCount);
            // Train rotated classifier
//            Handler.trainRotated(apiKey, positiveDirectoryPath, negativeDirectoryPath, classifierName, format, angles);
            // Select random images and classify
            Map<String, List<Double>> map = Handler.classifyRandomImages(apiKey, positiveDirectoryPath,
                    negativeDirectoryPath, sleepTime, positiveTestCount, negativeTestCount);
            // Classify selected images in a directory
//            Map<String, List<Double>> map = Handler.classifyRotated(apiKey, positiveDirectoryPath, negativeDirectoryPath, sleepTime);

            double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), offset);
            double[] fprs = Calculator.calculateRates(map.get(Handler.NEGATIVE), offset);

            if (map.get(Handler.POSITIVE).isEmpty() || map.get(Handler.NEGATIVE).isEmpty()) {
                System.out.printf("Not enough scores! Writing failed.");
            }
            else {
                DateFormat df = new SimpleDateFormat("dd-MM-HH-mm");
                Date date = new Date();
                String fileName = String.format("%s.json", df.format(date));
                System.out.printf("Writing to file %s...%n", fileName);
                Persistencer.write(map, fileName);
                Drawer.draw(plotName, plotTitle, plotWidth, plotHeight, fprs, tprs);
//            Handler.getCI(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime, CITimes, offset, CIRate);
            }
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
