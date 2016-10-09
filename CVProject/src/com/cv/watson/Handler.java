package com.cv.watson;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray on 2016/10/6.
 */
public class Handler {

    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";

    private final String apiKey;
    private final Trainer trainer;
    private final Tester tester;
    private double offset;

    public Handler(String apiKey, double offset) {
        this.apiKey = apiKey;
        this.offset = offset;
        this.trainer = new Trainer(apiKey);
        this.tester = new Tester(apiKey);
    }

    /* the Entry of whole application
    * @return a map of 3 kinds of animals' results of ROC points
    */
    public Map<String, Map<Double, Double>> handle(String negativeDirectoryPath, String ...positiveDirecotryPaths) throws Exception {
        File negativeDirectory = new File(negativeDirectoryPath);
        Map<String, Map<Double, Double>> allRocPoints = new HashMap<>();
        for (String positiveDirectoryPath : positiveDirecotryPaths) {
            File positiveDirectory = new File(positiveDirectoryPath);
            Map<Double, Double> points = createAndHandle(positiveDirectory, negativeDirectory);
            allRocPoints.put(positiveDirectory.getName(), points);
        }

        return allRocPoints;
    }

    /* create a classifier then classify
    * @param positiveDirectory directory of positive images
    * @param negativeDirectory directory of negative images
    * @return a Map containing roc points
    * */
    public Map<Double, Double> createAndHandle(File positiveDirectory, File negativeDirectory) throws Exception {
        String name = positiveDirectory.getName();

        File positiveZipFile = new File(positiveDirectory.getPath() + "\\train_positive" + name + ".zip");
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\train_negative" + name + ".zip");
//            File testZipFile = new File(animalDirectoryPath + "\\test_" + name + ".zip");


        Map<String, List<File>> testMap =
                trainer.getZips(positiveDirectory, negativeDirectory, positiveZipFile, negativeZipFile);

        List<VisualClassification> positiveClassifications = new ArrayList<>();
        List<VisualClassification> negativeClassifications = new ArrayList<>();
        // train
        VisualClassifier classifier = trainer.createClassifier(trainer.createClassifierOptions(
                name,
                positiveZipFile,
                negativeZipFile
        ));

        return getPoints(testMap, positiveClassifications, negativeClassifications, classifier);
    }

    /* fetch a classifier then classify
    * @param positiveDirectory directory of positive images
    * @param negativeDirectory directory of negative images
    * @return a Map containing roc points
    * */
    public Map<Double, Double> fetchAndHandle(File positiveDirectory, File negativeDirectory) throws Exception {
        String name = positiveDirectory.getName();

        File positiveZipFile = new File(positiveDirectory.getPath() + "\\train_positive" + name + ".zip");
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\train_negative" + name + ".zip");
//            File testZipFile = new File(animalDirectoryPath + "\\test_" + name + ".zip");


        Map<String, List<File>> testMap =
                trainer.getZips(positiveDirectory, negativeDirectory, positiveZipFile, negativeZipFile);

        List<VisualClassification> positiveClassifications = new ArrayList<>();
        List<VisualClassification> negativeClassifications = new ArrayList<>();
        // train
        VisualClassifier classifier = trainer.fetchClassifier();

        return getPoints(testMap, positiveClassifications, negativeClassifications, classifier);
    }

    /*
    * @param testMap[POSITIVE] -> list of positive testing files
    * @param testMap[NEGATIVE] -> list of negative testing files
    * @param positiveClassifications positive classifications
    * @param negativeClassifications negative classifications
    * @param classifier classifier to be classified
    * @return ROC points
    * */
    public Map<Double, Double> getPoints(Map<String, List<File>> testMap,
                                         List<VisualClassification> positiveClassifications,
                                         List<VisualClassification> negativeClassifications,
                                         VisualClassifier classifier) throws Exception {
        System.out.println("+++++++++++++++++Positive+++++++++++++++++");
        int count = 0;
        // test positive images
        for (File testFile : testMap.get(POSITIVE)) {
            positiveClassifications.add(tester.classify(testFile, classifier));
            // Rest for 5 seconds
            System.out.println("A classify finished, Waiting for 5s to start another...");
            Thread.sleep(5000);
            System.out.println(++count);
        }

        count = 0;
        System.out.println("+++++++++++++++++Negative+++++++++++++++++");
        // test negative images
        for (File testFile : testMap.get(NEGATIVE)) {
            negativeClassifications.add(tester.classify(testFile, classifier));
            System.out.println("A classify finished, Waiting for 5s to start another...");
            Thread.sleep(5000);
            System.out.println(++count);
        }

        // Get positive scores
        List<Double> positiveScores = new ArrayList<>();
        for (VisualClassification classification : positiveClassifications) {
            positiveScores.add(Calculator.getScore(classification));
        }
        // Get negative scores
        List<Double> negativeScores = new ArrayList<>();
        for (VisualClassification classification : negativeClassifications) {
            negativeScores.add(Calculator.getScore(classification));
        }

        // calculate TPRs
        List<Double> tprs = Calculator.calculateRates(positiveScores, offset, Calculator.FLAG_TPR);

        // calculate FNRs
        List<Double> fnrs = Calculator.calculateRates(positiveScores, offset, Calculator.FLAG_FNR);

        // calculate points
        Map<Double, Double> points = Calculator.calculatePoints(tprs, fnrs);

        return points;
    }

}
