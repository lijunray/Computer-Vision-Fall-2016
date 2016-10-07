package com.cv.watson;

import com.cv.Main;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;

/**
 * Created by Ray on 2016/10/6.
 */
public class Tester {

    private final String apiKey;

    public Tester(String apiKey) {
        this.apiKey = apiKey;
    }

    public VisualClassification classify(File file, VisualClassifier classifier) {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey(this.apiKey);

        System.out.println(file.getPath());

        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .images(file)
                .classifierIds(classifier.getId())
                .build();
        return service.classify(options).execute();
    }
}
