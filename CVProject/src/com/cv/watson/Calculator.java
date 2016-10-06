package com.cv.watson;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/10/6.
 */
public class Calculator {

    public static final String FLAG_TPR = "tpr";
    public static final String FLAG_FPR = "fpr";

    /*
    * @param classification classifying result from server
    * @return score of this image
    * */
    public static double getScore(VisualClassification classification) {
        return classification.getImages().get(0).getClassifiers().get(0).getClasses().get(0).getScore();
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
        else if (flag == FLAG_FPR) {
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

}
