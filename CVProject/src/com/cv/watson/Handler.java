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
            File zipFile = new File(positiveDirectory.getPath() + ".zip");
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
        System.out.println("Positive Classification: " + positiveClassification);

        System.out.println("Sleep for " + sleepTime / 1000 + "seconds...");
        Thread.sleep(sleepTime);

        System.out.println("+++++++++++++++++Negative+++++++++++++++++");
        VisualClassification negativeClassification = Tester.classify(negativeTestZipFile, classifier, apiKey);
        System.out.println("Negative Classification: " + negativeClassification);

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

        System.out.println("Classifier id: " + classifier.getId());

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

        System.out.println("Positive zip file path: " + positiveTestZipFile.getPath());
        System.out.println("Positive images count: " + positiveTestFiles.size());
        System.out.println("Negative zip file path: " + negativeTestZipFile.getPath());
        System.out.println("Negative images count: " + negativeTestFiles.size());

        if (positiveTestZipFile.exists()) {
            positiveTestZipFile.delete();
        }
        if (negativeTestZipFile.exists()) {
            negativeTestZipFile.delete();
        }

        Selector.zip(positiveTestFiles, "", positiveTestZipFile);
        Selector.zip(negativeTestFiles, "", negativeTestZipFile);
        Map<String, List<Double>> map = new HashMap<>();

        try {
            System.out.println("+++++++++++++++++Positive+++++++++++++++++");
            VisualClassification positiveClassification = Tester.classify(positiveTestZipFile, classifier, apiKey);
            System.out.println("Positive Classification: " + positiveClassification);

            System.out.println("Sleep for " + sleepTime / 1000 + "seconds...");
            Thread.sleep(sleepTime);

            System.out.println("+++++++++++++++++Negative+++++++++++++++++");
            VisualClassification negativeClassification = Tester.classify(negativeTestZipFile, classifier, apiKey);
            System.out.println("Negative Classification: " + negativeClassification);

            List<Double> positiveScores = Calculator.getScores(positiveClassification);
            List<Double> negativeScores = Calculator.getScores(negativeClassification);


            map.put(POSITIVE, positiveScores);
            map.put(NEGATIVE, negativeScores);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return map;
        }
    }

    public static void getCI(String apiKey,
                             String positiveDirectoryPath,
                             String negativeDirectoryPath,
                             int testCount,
                             long sleepTime,
                             int CITimes,
                             double offset,
                             double CIRate) throws Exception {
        List<List<Double>> tprCIs = new ArrayList<>(20);
        List<List<Double>> fprCIs = new ArrayList<>(20);
        for (int i = 0; i < CITimes; i++) {
            Map<String, List<Double>> map = Handler.classify(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime);
            Set<String> keys = map.keySet();
            for (String key : keys) {
                System.out.println("---------------" + key + "----------------------");
                for (Double score : map.get(key)) {
                    System.out.println(score);
                }
            }
            double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), offset);
            double[] fprs = Calculator.calculateRates(map.get(Handler.NEGATIVE), offset);
            System.out.println("scores: {" + "\"positives\": " + map.get(Handler.POSITIVE) + "," + "\"negatives\": " + map.get(Handler.NEGATIVE) + "}");

            for (int j = 0; j < 1 / offset; j++) {
                tprCIs.get(j).add(tprs[j]);
            }
            for (int j = 0; j < 1 / offset; j++) {
                fprCIs.get(j).add(fprs[j]);
            }
            Thread.sleep(sleepTime);
        }
        int count = (int) (CITimes * CIRate);
        int start = (CITimes - count) >> 1;
        int end = CITimes - start;
        Map<Double, Double> tprCI = new HashMap<>();
        Map<Double, Double> fprCI = new HashMap<>();
        for (List<Double> t : tprCIs) {
            t.sort((a, b) -> (int) (a - b));
            tprCI.put(t.get(start), t.get(end));
        }
        for (List<Double> f : fprCIs) {
            f.sort((a, b) -> (int) (a - b));
            fprCI.put(f.get(start), f.get(end));
        }

        System.out.printf("CI for TPRs: /n");
        Set<Double> tprKeys = tprCI.keySet();
        for (Double tprKey : tprKeys) {
            System.out.printf("[/d, /d], ", tprKey, tprCI.get(tprKey));
        }
        System.out.printf("/nCI for FPRs: /n");
        Set<Double> fprKeys = fprCI.keySet();
        for (Double fprKey : fprKeys) {
            System.out.printf("[/d, /d], ", fprKey, tprCI.get(fprKey));
        }
    }
}
