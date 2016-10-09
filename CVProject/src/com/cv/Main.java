package com.cv;

import com.cv.watson.Handler;

import java.util.Map;

public class Main {

    public static final String API_KEY = "";
    public static final String OFFSET = 0.001;

    public static void main(String[] args) throws Exception {
        String apiKey = API_KEY;
        Handler handler = new Handler(apiKey, OFFSET);
        String projectPath = System.getProperty("user.dir");
        String[] positiveDirectoryPath = {
                projectPath + "Your Positive Directory Paths"
        };
        String negativeDirectoryPath = projectPath + "Your Negative Directory Path";
        Map<String, Map<Double, Double>> map = handler.handle(negativeDirectoryPath, positiveDirectoryPath);
    }
}
