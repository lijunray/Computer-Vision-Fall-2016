package com.cv.test;

import com.cv.Main;

import com.cv.watson.Calculator;
import com.cv.watson.Drawer;
import com.cv.watson.Handler;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
//        Trainer.getZips(animalDirectory, new File(wrongDirectoryPath), positiveZipFile, negativeZipFile);
    }

    @Test
    public void testCreateClassifier() throws Exception {
        String animalDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel";
        String wrongDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
        File animalDirectory = new File(animalDirectoryPath);
        String name = animalDirectory.getName();
        File positiveZipFile = new File(animalDirectoryPath + "\\train_positive_" + name + ".zip");
        File negativeZipFile = new File(wrongDirectoryPath + "\\train_negative" + ".zip");
//        VisualClassifier classifier = Trainer.createClassifier(Trainer.createClassifierOptions("camel", positiveZipFile, negativeZipFile));
//        System.out.println(classifier.getId());
    }

    @Test
    public void testClassifyAnimal() throws Exception {
        String path = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel\\camel.zip";
//        VisualRecognition service =
//                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, Main.API_KEY);

        // get all classifiers in server
//        List<VisualClassifier> classifiers = service.getClassifiers().execute();
//        VisualClassification classification = new Tester(Main.API_KEY).classify(new File(path), classifiers.get(0), apiKey);
//        System.out.println(classification);
    }

//    @Test
//    public void testTrainAAnimal() throws Exception {
//        String positiveDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive\\camel";
//        String negativeDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
//        System.out.println(map);
//      }

    @Test
    public void testClassifier() throws Exception {
        String positiveDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\positive";
        String negativeDirectoryPath = System.getProperty("user.dir") + "\\resource\\pictures\\negative";
//        Map<String, List<Double>> map = Handler.classify(Main.API_KEY, positiveDirectoryPath, negativeDirectoryPath, Main.TEST_COUNT, Main.SLEEP_TIME);
//        System.out.println(map);
//        Set<String> keys = map.keySet();
//        System.out.println("scores:");
//        for (String key : keys) {
//            System.out.println("---------------" + key + "----------------------");
//            for (Double score : map.get(key)) {
//                System.out.println(score);
//            }
//        }
//        double[] tprs = Calculator.calculateRates(map.get(Handler.POSITIVE), Calculator.FLAG_TPR, Main.OFFSET);
//        double[] fnrs = Calculator.calculateRates(map.get(Handler.NEGATIVE), Calculator.FLAG_FNR, Main.OFFSET);
//
//        Drawer.draw(Main.PLOT_NAME, Main.PLOT_TITLE, Main.PLOT_WIDTH, Main.PLOT_HEIGHT, tprs, fnrs);

    }

    @Test
    public void test1() {
        String path = System.getProperty("user.dir") + "\\resource\\pictures\\negative\\test.zip";
        File testFile = new File(path);
        System.out.printf(String.valueOf(testFile.getName().endsWith(".zip")));
    }

    @Test
    public void testProperties() throws IOException {
        File info = new File(System.getProperty("user.dir") + "\\info.properties");
        Properties properties = Main.getProperties(info);
        System.out.println(properties.getProperty("COUNT").getClass());
    }
}
