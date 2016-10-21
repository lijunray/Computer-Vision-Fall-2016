package com.cv.watson;

import com.google.gson.Gson;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Responsible for Data Persistence
 * Created by Ray on 2016/10/21.
 */
public class Persistencer {

    public static final String SCORE_DIRECTORY_PATH = System.getProperty("user.dir") + "\\resource\\scores\\";

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
}
