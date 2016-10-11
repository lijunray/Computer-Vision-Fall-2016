package com.cv.watson;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.util.*;

/**
 * Created by Ray on 2016/10/10.
 */
public class Handler {


    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";

    /**
     * This method is to get ROC points
     * (Selector) random number -> files selected from positive directory (non-selected files are saved for testing)
     * -> zip files as positive zip file -> (Trainer) fetch classifier from server -> delete existed classifier
     * -> create new classifier with classes -> select and zip testToIndex files as testing zip file
     * -> (Tester) classify testing zip file -> scores -> (Calculator) TPR and NFR -> Map<TPR, NFR>
     *
     */
    public static Map<String, List<Double>> handle(String apiKey,
                                                   String positiveDirectoryPath,
                                                   String negativeDirectoryPath,
                                                   double offset,
                                                   int count,
                                                   String classifierName,
                                                   int testCount,
                                                   long sleepTime) throws Exception {

        List<File> positiveDirectories = Arrays.asList(new File(positiveDirectoryPath).listFiles());
        List<File> positiveFiles = new ArrayList<>();
        File negativeDirectory = new File(negativeDirectoryPath);

        List<File> positiveZipFiles = new ArrayList<>();

        for (File positiveDirectory : positiveDirectories) {
            if (!positiveDirectory.isDirectory()) {
                continue;
            }
            positiveFiles.addAll(Arrays.asList(positiveDirectory.listFiles()));
            String name = positiveDirectory.getName();
            List<File> randomPositiveFiles = Selector.select(positiveDirectory, count);
            // zip files
            File zipFile = new File(positiveDirectory.getPath() + name + ".zip");
            Selector.zip(randomPositiveFiles, "", zipFile);
            positiveZipFiles.add(zipFile);
        }

        List<File> randomNegativeFiles = Selector.select(negativeDirectory, count);
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\negative.zip");
        Selector.zip(randomNegativeFiles, "", negativeZipFile);

        List<File> negativeFiles = Arrays.asList(negativeDirectory.listFiles());

        VisualClassifier classifier = Trainer.createClassifier(Trainer.createClassifierOptions(positiveZipFiles, negativeZipFile, classifierName), apiKey);

        List<File> positiveTestFiles = Selector.selectTestFiles(positiveFiles, testCount);
        List<File> negativeTestFiles = Selector.selectTestFiles(negativeFiles, testCount);

        File positiveTestZipFile = new File(positiveDirectoryPath + "\\positive_test.zip");
        File negativeTestZipFile = new File(negativeDirectoryPath + "\\negative_test.zip");

        positiveTestZipFile.delete();
        negativeTestZipFile.delete();

        Selector.zip(positiveTestFiles, "", positiveTestZipFile);
        Selector.zip(negativeTestFiles, "", negativeTestZipFile);

        System.out.println("+++++++++++++++++Positive+++++++++++++++++");
        VisualClassification positiveClassification = Tester.classify(positiveTestZipFile, classifier, apiKey);

        System.out.println("Sleep for " + sleepTime / 1000 + "seconds...");
        Thread.sleep(sleepTime);

        System.out.println("+++++++++++++++++Negative+++++++++++++++++");
        VisualClassification negativeClassification = Tester.classify(negativeTestZipFile, classifier, apiKey);

        List<Double> positiveScores = Calculator.getScores(positiveClassification);
        List<Double> negativeScores = Calculator.getScores(negativeClassification);

        Map<String, List<Double>> map = new HashMap<>();

        map.put(POSITIVE, positiveScores);
        map.put(NEGATIVE, negativeScores);

//        List<Double> tprs = Calculator.calculateRates(positiveScores, Calculator.FLAG_TPR, offset);
//        List<Double> fnrs = Calculator.calculateRates(negativeScores, Calculator.FLAG_FNR, offset);

//        return Calculator.calculatePoints(tprs, fnrs);
        return map;
    }

    public static Map<String, List<Double>> classify(String apiKey,
                                                     String positiveDirectoryPath,
                                                     String negativeDirectoryPath,
                                                     int testCount,
                                                     long sleepTime) throws Exception {
        VisualClassifier classifier = Trainer.fetchClassifier(apiKey);
        if (classifier == null) {
            return null;
        }
        List<File> positiveFiles = new ArrayList<>();
        File positiveDirectory = new File(positiveDirectoryPath);
        for (File file : positiveDirectory.listFiles()) {
            if (file.isDirectory()) {
                positiveFiles.addAll(Arrays.asList(file.listFiles()));
            }
        }

        List<File> negativeFiles = Arrays.asList(new File(negativeDirectoryPath).listFiles());

        List<File> positiveTestFiles = Selector.selectTestFiles(positiveFiles, testCount);
        List<File> negativeTestFiles = Selector.selectTestFiles(negativeFiles, testCount);

        File positiveTestZipFile = new File(positiveDirectoryPath + "\\positive_test.zip");
        File negativeTestZipFile = new File(negativeDirectoryPath + "\\negative_test.zip");

        System.out.println(positiveTestZipFile.getPath());
        System.out.println(positiveTestFiles.size());
        System.out.println(negativeTestZipFile.getPath());
        System.out.println(negativeTestFiles.size());

        if (positiveTestZipFile.exists()) {
            positiveTestZipFile.delete();
        }
        if (negativeTestZipFile.exists()) {
            negativeTestZipFile.delete();
        }

        Selector.zip(positiveTestFiles, "", positiveTestZipFile);
        Selector.zip(negativeTestFiles, "", negativeTestZipFile);

        System.out.println("+++++++++++++++++Positive+++++++++++++++++");
        VisualClassification positiveClassification = Tester.classify(positiveTestZipFile, classifier, apiKey);
        System.out.println(positiveClassification);

        System.out.println("Sleep for " + sleepTime / 1000 + "seconds...");
        Thread.sleep(sleepTime);

        System.out.println("+++++++++++++++++Negative+++++++++++++++++");
        VisualClassification negativeClassification = Tester.classify(negativeTestZipFile, classifier, apiKey);
        System.out.println(negativeClassification);

        List<Double> positiveScores = Calculator.getScores(positiveClassification);
        List<Double> negativeScores = Calculator.getScores(negativeClassification);

        Map<String, List<Double>> map = new HashMap<>();

        map.put(POSITIVE, positiveScores);
        map.put(NEGATIVE, negativeScores);
        return map;
    }
}
