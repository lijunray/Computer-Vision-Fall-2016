package com.cv.watson;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for Data Persistence
 * Created by Ray on 2016/10/21.
 */
public class Persistencer {

    public static final String SCORE_DIRECTORY_PATH = System.getProperty("user.dir") + "\\resource\\scores\\";
    public static final String POSITIVE_NORMAL = "positive_normal";
    public static final String NEGATIVE_NORMAL = "negative_normal";
    public static final String POSITIVE_ROTATED = "positive_rotated";
    public static final String NEGATIVE_ROTATED = "negative_rotated";


    /**
     * Write both positive and negative scores into a json file
     * @param map where the scores are stored in
     * @param fileName the file's name
     */
    public static void write(Map<String, List<Double>> map, String fileName) throws IOException {
        Gson gson = new Gson();
        String scores = gson.toJson(map);
        OutputStream fos = new FileOutputStream(new File(SCORE_DIRECTORY_PATH + fileName));
        fos.write(scores.getBytes());
        fos.close();
    }

    public static Map<String, List<Double>> read(String normal, String rotated) throws IOException {
        Gson gson = new Gson();
        File normalFile = new File(String.format("%s\\resource\\scores\\%s", System.getProperty("user.dir"), normal));
        File rotatedFile = new File(String.format("%s\\resource\\scores\\%s", System.getProperty("user.dir"), rotated));
        String normalString = new String(Files.readAllBytes(normalFile.toPath()));
        String rotatedString = new String(Files.readAllBytes(rotatedFile.toPath()));
        Map<String, List<Double>> normalMap = gson.fromJson(normalString, HashMap.class);
        Map<String, List<Double>> rotatedMap = gson.fromJson(rotatedString, HashMap.class);
        Map<String, List<Double>> map = new HashMap<>();

        map.put(POSITIVE_NORMAL, normalMap.get(Handler.POSITIVE));
        map.put(NEGATIVE_NORMAL, normalMap.get(Handler.NEGATIVE));
        map.put(POSITIVE_ROTATED, rotatedMap.get(Handler.POSITIVE));
        map.put(NEGATIVE_ROTATED, rotatedMap.get(Handler.NEGATIVE));
        return map;
    }
}
