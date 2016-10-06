package com.cv.test;

import com.cv.watson.Handler;
import com.cv.watson.Tester;
import com.cv.watson.Trainer;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Ray on 2016/10/6.
 */
public class TrainerTest {

    @Test
    public void testGetZip() throws Exception {

        String animalDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel";
        String wrongDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
        File animalDirectory = new File(animalDirectoryPath);
        String name = animalDirectory.getName();
        File positiveZipFile = new File(animalDirectoryPath + "\\train_positive_" + name + ".zip");
        File negativeZipFile = new File(wrongDirectoryPath + "\\train_negative" + ".zip");
//        File testZipFile = new File(animalDirectoryPath + "\\test_" + name + ".zip");
        Trainer.getZips(animalDirectory, new File(wrongDirectoryPath), positiveZipFile, negativeZipFile);
    }

    @Test
    public void testCreateClassifier() throws Exception {
        String animalDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel";
        String wrongDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
        File animalDirectory = new File(animalDirectoryPath);
        String name = animalDirectory.getName();
        File positiveZipFile = new File(animalDirectoryPath + "\\train_positive_" + name + ".zip");
        File negativeZipFile = new File(wrongDirectoryPath + "\\train_negative" + ".zip");
        VisualClassifier classifier = Trainer.createClassfier(Trainer.createClassifierOptions("camel", positiveZipFile, negativeZipFile));
        System.out.println(classifier.getId());
    }

    @Test
    public void testClassifyAnimal() throws Exception {
        String path = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel\\test_camel\\images (32).jpg";
        VisualRecognition service =
                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, Handler.API_KEY);

        // get all classifiers in server
        List<VisualClassifier> classifiers = service.getClassifiers().execute();
        VisualClassification classification = Tester.classify(new File(path), classifiers.get(0));

        for (double treshhold = 0; treshhold <= 1; treshhold += 0.001) {

        }
    }

    @Test
    public void testTrainAAnimal() throws Exception {
        String positiveDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel";
        String negativeDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
        Map<Double, Double> map = Handler.handleAnimal(new File(positiveDirectoryPath), new File(negativeDirectoryPath));
        System.out.println(map);
    }
}
