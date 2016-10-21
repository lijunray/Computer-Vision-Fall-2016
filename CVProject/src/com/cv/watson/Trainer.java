package com.cv.watson;


import com.cv.Main;
import com.ibm.watson.developer_cloud.util.Validator;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Ray on 2016/10/3.
 */
public class Trainer {

    /**
     * Creating classifier options
     * @param positiveExamples positive images
     * @param negativeExample negative images
     * @param name classifier name
     * @return new ClassifierOptions object
     */
    public static ClassifierOptions createClassifierOptions(List<File> positiveExamples, File negativeExample, String name) {
        ClassifierOptions.Builder builder = new ClassifierOptions.Builder().negativeExamples(negativeExample).classifierName(name);
        for (File positiveExample : positiveExamples) {
            builder.addClass(positiveExample.getName(), positiveExample);
        }
        return builder.build();
    }

    /**
     * Create a classifier
     * @param options classifier options
     * @param apiKey api key
     * @return a new classifier
     */
    public static VisualClassifier createClassifier(ClassifierOptions options, String apiKey) {
        Validator.notNull(options, "options cannot be null");
        VisualClassifier classifier = fetchClassifier(apiKey);
        VisualRecognition service =
                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, apiKey);
        // delete classifiers if one exists in server
        if (classifier != null) {
            System.out.println("Deleting classifier...");
            service.deleteClassifier(classifier.getId()).execute();
            System.out.println("Deleted classifier");
        }

        System.out.println("Creating classifier...");
        VisualClassifier newClassifier = service.createClassifier(options).execute();
        System.out.println("Created classifier:" + newClassifier.getName() + " with id of " + newClassifier.getId());
        return newClassifier;
    }

    /**
     * Fetch a classifier from the server
     * @param apiKey api key
     * @return the fetched classifier
     */
    public static VisualClassifier fetchClassifier(String apiKey) {
        VisualRecognition service =
                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, apiKey);

        // get all classifiers in server
        System.out.println("Fetching classifiers...");
        List<VisualClassifier> classifiers = service.getClassifiers().execute();
        System.out.println("Got classifiers");
        if (!classifiers.isEmpty()) {
            return classifiers.get(0);
        }
        return null;
    }
}
