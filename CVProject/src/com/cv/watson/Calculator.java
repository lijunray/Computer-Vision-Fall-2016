package com.cv.watson;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.util.*;

/**
 * Created by Ray on 2016/10/6.
 */
public class Calculator {

    public static final String FLAG_TPR = "tpr";
    public static final String FLAG_FNR = "fnr";

    /*
    * @param classification classifying result from server
    * @return score of this image
    * */
    public static double getScore(VisualClassification classification) {
        double score = classification.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getScore();
        System.out.println(score);
        return score;
    }

    /*
    * @param scores scores for a test
    * @param threshold
    * @param flag flag to calculate TPR or FPR
    * @return result of a ROC point under this threshold
    * */
    public static double calculate(List<Double> scores, double threshold, String flag) throws Exception{

        int count = 0;
        if (flag == FLAG_TPR) {
            for (Double score : scores) {
                if (score > threshold) {
                    count++;
                }
            }
        }
        else if (flag == FLAG_FNR) {
            for (Double score : scores) {
                if (score < threshold) {
                    count++;
                }
            }
        }
        else {
            throw new Exception("Undefined flag!");
        }
        return count / scores.size();
    }

    public static List<Double> calculateRates(List<Double> scores, double offset, String flag) throws Exception {
        List<Double> rates = new ArrayList<>();
        double maxScore = 0;
        for (Double score : scores) {
            maxScore = score > maxScore ? score : maxScore;
        }
        double minScore = 0;
        for (Double score : scores) {
            minScore = score < minScore ? score : minScore;
        }
        for (double threshold = minScore; threshold <= maxScore; threshold += offset) {
            double r = Calculator.calculate(scores, threshold, flag);
            rates.add(r);
        }
        return rates;
    }

    public static Map<Double, Double> calculatePoints(List<Double> tprs, List<Double> fnrs) {
        Map<Double, Double> points = new HashMap<>();
        for (int i = 0; i < tprs.size(); i++) {
            points.put(tprs.get(i), fnrs.get(i));
        }
        return points;
    }

}
