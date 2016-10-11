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

//    private static final String PATH_CLASSIFIERS = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers";
//    private static final String VERSION = "version";
//    private static final String PARAM_NAME = "name";
//    private static final String PARAM_POSITIVE_EXAMPLES = "positive_examples";
//    private static final String PARAM_NEGATIVE_EXAMPLES = "negative_examples";


    private final String apiKey;

    public Trainer(String apiKey) {
        this.apiKey = apiKey;
    }

    /*
      *
      * @param animalDirectory the directory of images
      * @param trainZipFile zip file to be written into
      * @param testZipFile test zip file to be written into
      * @return test file array
     */
//    public Map<String, List<File>> getZips(File positiveDirectory, File negativeDirectory, File positiveZipFile,
//                               File negativeZipFile) throws Exception {
//
//
//        positiveZipFile.delete();
////        testZipFile.delete();
//        negativeZipFile.delete();
//
//        List<File> trainPositiveImages = Arrays.asList(positiveDirectory.listFiles());
//        List<File> trainNegativeImages = Arrays.asList(negativeDirectory.listFiles());
//
//        // Generate set of positive numbers
//        int positiveCount = trainPositiveImages.size();
//        int negativeCount = trainNegativeImages.size();
//        int count = positiveCount * 8 / 10;
//        Set<Integer> randomPositiveNumbers = Selector.getRandomSet(positiveCount, count);
//        Set<Integer> randomNegativeNumbers = Selector.getRandomSet(negativeCount, count);
//
//        // Generate positive training files and test files
//        List<File> trainPositiveFiles = new ArrayList<>();
//        List<File> trainNegativeFiles = new ArrayList<>();
//        List<File> testPositiveFiles = new ArrayList<>();
//        List<File> testNegativeFiles = new ArrayList<>();
//
//        for (int i = 0; i < positiveCount; i++) {
//            if (randomPositiveNumbers.contains(i)) {
//                trainPositiveFiles.add(trainPositiveImages.get(i));
//            }
//            else {
//                testPositiveFiles.add(trainPositiveImages.get(i));
//            }
//        }
//
//        //Generate negative training files
//        for (int i = 0; i < negativeCount; i++) {
//            if (randomNegativeNumbers.contains(i)) {
//                trainNegativeFiles.add(trainNegativeImages.get(i));
//            }
//            else {
//                testNegativeFiles.add(trainNegativeImages.get(i));
//            }
//        }
//
//        zip(trainPositiveFiles, "", positiveZipFile);
//        zip(trainNegativeFiles, "", negativeZipFile);
//
//        Map<String, List<File>> map = new HashMap<>();
//        map.put(Handler.POSITIVE, testPositiveFiles);
//        map.put(Handler.NEGATIVE, testNegativeFiles);
//
//        return map;
//    }

    /*
    * @param classifierName the name of classifer
    * @param positiveExample positive zip file
    * @param negativeExample negative zip file
    * @return new ClassifierOptions object
    * */
    public static ClassifierOptions createClassifierOptions(List<File> positiveExamples, File negativeExample, String name) {
        ClassifierOptions.Builder builder = new ClassifierOptions.Builder().negativeExamples(negativeExample).classifierName(name);
        for (File positiveExample : positiveExamples) {
            builder.addClass(positiveExample.getName(), positiveExample);
        }
        return builder.build();
    }

    /*
    * @param options options of classifier
    * @return VisualClassifier
    * */
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

    /*
    * @return the classifier fetched from server
    * */
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
