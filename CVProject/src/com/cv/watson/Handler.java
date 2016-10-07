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

    public Handler(String apiKey) {
        this.apiKey = apiKey;
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
            Map<Double, Double> points = handleAnimal(positiveDirectory, negativeDirectory);
            allRocPoints.put(positiveDirectory.getName(), points);
        }

        return allRocPoints;
    }

    /* handle a kind of animal
    * @param positiveDirectory directory of positive images
    * @param negativeDirectory directory of negative images
    * @return a Map containing roc points
    * */
    public Map<Double, Double> handleAnimal(File positiveDirectory, File negativeDirectory) throws Exception {
        String name = positiveDirectory.getName();

        File positiveZipFile = new File(positiveDirectory.getPath() + "\\train_positive" + name + ".zip");
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\train_negative" + name + ".zip");
//            File testZipFile = new File(animalDirectoryPath + "\\test_" + name + ".zip");


        Map<String, List<File>> testMap =
                trainer.getZips(positiveDirectory, negativeDirectory, positiveZipFile, negativeZipFile);

        List<VisualClassification> positiveClassifications = new ArrayList<>();
        List<VisualClassification> negativeClassifications = new ArrayList<>();
        // train
        VisualClassifier classifier = trainer.createClassfier(trainer.createClassifierOptions(
                name,
                positiveZipFile,
                negativeZipFile
        ));

        System.out.println("+++++++++++++++++Positive+++++++++++++++++");
        int count = 0;
        // test positive images
        for (File testFile : testMap.get(POSITIVE)) {
            positiveClassifications.add(tester.classify(testFile, classifier));
            System.out.println(++count);
        }

        count = 0;
        System.out.println("+++++++++++++++++Negative+++++++++++++++++");
        // test negative images
        for (File testFile : testMap.get(NEGATIVE)) {
            negativeClassifications.add(tester.classify(testFile, classifier));
            System.out.println(++count);
        }

        // positive
        List<Double> positiveScores = new ArrayList<>();
        for (VisualClassification classification : positiveClassifications) {
            positiveScores.add(Calculator.getScore(classification));
        }
        // calculate TPRs
        List<Double> tprs = new ArrayList<>();
        for (int threshold = 0; threshold <= 1; threshold += 0.001) {
            double tpr = Calculator.calculate(positiveScores, threshold, Calculator.FLAG_TPR);
            tprs.add(tpr);
        }

        // negative
        List<Double> negativeScores = new ArrayList<>();
        for (VisualClassification classification : negativeClassifications) {
            negativeScores.add(Calculator.getScore(classification));
        }
        // calculate FPRs
        List<Double> fprs = new ArrayList<>();
        for (int threshold = 0; threshold <= 1; threshold += 0.001) {
            double fpr = Calculator.calculate(positiveScores, threshold, Calculator.FLAG_FPR);
            fprs.add(fpr);
        }

        Map<Double, Double> points = new HashMap<>();
        for (int i = 0; i < tprs.size(); i++) {
            points.put(tprs.get(i), fprs.get(i));
        }

        return points;
    }
}
