package com.cv.watson;

import com.cv.Main;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/10/6.
 */
public class Tester {

    public static VisualClassification classify(File file, VisualClassifier classifier, String apiKey) {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey(apiKey);

        System.out.println(file.getPath());

        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .images(file)
                .classifierIds(classifier.getId())
                .threshold(0)
                .build();
        return service.classify(options).execute();
    }

    public List<File> getTestFiles(List<File> files, int count) {
        List<File> testFiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            testFiles.add(files.get(i));
        }
        return testFiles;
    }
}
