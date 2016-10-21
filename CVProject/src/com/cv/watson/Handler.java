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
     * (Selector) random number -> files selected from positive directory (non-selected files are saved for testing)
     * -> zip files as positive zip file -> (Trainer) fetch classifier from server -> delete existed classifier
     * -> create new classifier with classes -> select and zip testToIndex files as testing zip file
     * -> (Tester) classify testing zip file -> scores -> (Calculator) TPR and NFR -> Map<TPR, NFR>
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param count count of training images
     * @param classifierName classifier name
     * @param testCount count of testing images
     * @param sleepTime sleep time between 2 test
     * @return a map containing both positive and negative scores
     * @throws Exception
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

        return handleClassify(apiKey,
                positiveDirectoryPath,
                negativeDirectoryPath,
                sleepTime,
                classifier,
                positiveTestFiles,
                negativeTestFiles
        );
    }

    /**
     * Classify both positive and negative images in specific directories
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param testCount count of testing images
     * @param sleepTime sleep time between 2 testing
     * @return a map containing both positive and negative scores
     * @throws Exception
     */
    public static Map<String, List<Double>> handle(String apiKey,
                                                   String positiveDirectoryPath,
                                                   String negativeDirectoryPath,
                                                   int testCount,
                                                   long sleepTime) throws Exception {
        VisualClassifier classifier = Trainer.fetchClassifier(apiKey);
        if (classifier == null) {
            System.out.printf("Classifier Not Found!%n");
            return null;
        }

        System.out.printf("Classifier id: %s%n", classifier.getId());

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

        return Handler.handleClassify(
                apiKey,
                positiveDirectoryPath,
                negativeDirectoryPath,
                sleepTime,
                classifier,
                positiveTestFiles,
                negativeTestFiles
        );
    }

    /**
     * Select some positive and negative images, and rotate them by some angles
     * Delete a classifier on the server, if exist
     * Zip all training images and train a new classifier
     * Select some other positive and negative images
     * Rotate testing images by same angles as classifying test
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param count count of training images
     * @param classifierName name of created classifier
     * @param testCount count of testing images
     * @param sleepTime sleep time between 2 tests
     * @param format format for rotated images
     * @param angles angles for images to be rotated on
     * @return a map containing both positive and negative test scores
     * @throws Exception
     */
    public static Map<String, List<Double>> handle(String apiKey,
                                                   String positiveDirectoryPath,
                                                   String negativeDirectoryPath,
                                                   int count,
                                                   String classifierName,
                                                   int testCount,
                                                   long sleepTime,
                                                   String format,
                                                   int... angles) throws Exception {
        List<File> positiveDirectories = Arrays.asList(new File(positiveDirectoryPath).listFiles());
        List<File> positiveFiles = new ArrayList<>();
        File negativeDirectory = new File(negativeDirectoryPath);

        List<File> positiveZipFiles = new ArrayList<>();

        for (File positiveDirectory : positiveDirectories) {
            if (!positiveDirectory.isDirectory() || positiveDirectory.listFiles().length == 0) {
                continue;
            }
            positiveFiles.addAll(Arrays.asList(positiveDirectory.listFiles()));
            List<File> randomPositiveFiles = Rotater.rotateImages(Selector.select(positiveDirectory, count), format, angles);
            // zip files
            File zipFile = new File(positiveDirectory.getPath() + ".zip");
            Selector.zip(randomPositiveFiles, "", zipFile);
            positiveZipFiles.add(zipFile);
        }

        List<File> randomNegativeFiles = Rotater.rotateImages(Selector.select(negativeDirectory, count), format, angles);
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\negative.zip");
        Selector.zip(randomNegativeFiles, "", negativeZipFile);

        List<File> negativeFiles = Arrays.asList(negativeDirectory.listFiles());

        VisualClassifier classifier = Trainer.createClassifier(Trainer.createClassifierOptions(positiveZipFiles, negativeZipFile, classifierName), apiKey);

        List<File> positiveTestFiles = Rotater.rotateImages(Selector.selectTestFiles(positiveFiles, testCount), format, angles);
        List<File> negativeTestFiles = Rotater.rotateImages(Selector.selectTestFiles(negativeFiles, testCount), format, angles);

        return handleClassify(apiKey,
                positiveDirectoryPath,
                negativeDirectoryPath,
                sleepTime,
                classifier,
                positiveTestFiles,
                negativeTestFiles
        );
    }

    /**
     * Print Confidence Intervals in console
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory path
     * @param negativeDirectoryPath negative images directory path
     * @param testCount count of testing images
     * @param sleepTime sleep time between 2 tests
     * @param CITimes classifying times
     * @param offset offset of threshold
     * @param CIRate rate for confidence intervals
     * @throws Exception
     */
    public static void getCI(String apiKey,
                             String positiveDirectoryPath,
                             String negativeDirectoryPath,
                             int testCount,
                             long sleepTime,
                             int CITimes,
                             double offset,
                             double CIRate) throws Exception {
        List<List<Double>> tprCIs = new ArrayList<>();
        List<List<Double>> fprCIs = new ArrayList<>();
        for (int i = 0; i < 1 / offset; i++) {
            List<Double> tprCI = new ArrayList<>();
            List<Double> fprCI = new ArrayList<>();
            tprCIs.add(tprCI);
            fprCIs.add(fprCI);
        }
        for (int i = 0; i < CITimes; i++) {
            Map<String, List<Double>> map = Handler.handle(apiKey, positiveDirectoryPath, negativeDirectoryPath, testCount, sleepTime);
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
            System.out.printf("%d time finished%n", i + 1);
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

        System.out.printf("CI for TPRs: %n");
        Set<Double> tprKeys = tprCI.keySet();
        for (Double tprKey : tprKeys) {
            System.out.printf("[%f, %f], ", tprKey, tprCI.get(tprKey));
        }
        System.out.printf("%nCI for FPRs: %n");
        Set<Double> fprKeys = fprCI.keySet();
        for (Double fprKey : fprKeys) {
            System.out.printf("[%f, %f], ", fprKey, tprCI.get(fprKey));
        }
    }

    /**
     * Classify both positive and negative images in specific directories
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory path
     * @param negativeDirectoryPath negative images directory path
     * @param sleepTime sleep time between 2 tests
     * @param classifier classifier to be classified
     * @param positiveTestFiles list of positive test files
     * @param negativeTestFiles list of negative test files
     * @return
     * @throws Exception
     */
    private static Map<String, List<Double>> handleClassify(String apiKey,
                                                            String positiveDirectoryPath,
                                                            String negativeDirectoryPath,
                                                            long sleepTime,
                                                            VisualClassifier classifier,
                                                            List<File> positiveTestFiles,
                                                            List<File> negativeTestFiles) throws Exception {
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

        return map;
    }
}
