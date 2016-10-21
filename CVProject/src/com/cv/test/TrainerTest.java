package com.cv.test;

import com.cv.Main;

import com.cv.watson.Drawer;
import com.cv.watson.Rotater;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

    @Test
    public void testDrawing() {
        double[] x = new double[1000];
        double[] y = new double[1000];
        int count = 0;
        for (double i = 0; i <= 1; i += 0.01) {
            x[count] = i;
            y[count] = i * 2;
            count++;
        }
        Drawer.draw("plotName", "plotTitle", 600, 600, x, y);
    }

    @Test
    public void testRotateAnImage() throws IOException {
        List<File> rotatedImages = Rotater.rotateAnImage(new File("C:\\Users\\Ray\\Documents\\GitHub\\Computer-Vision-Fall-2016\\CVProject\\resource\\pictures\\positive\\camel.jpg"), "PNG", 10, 20, 30, 40, 50, 60);
    }

    @Test
    public void testRotateImages() throws IOException {
        File dir = new File("C:\\Users\\Ray\\Documents\\GitHub\\Computer-Vision-Fall-2016\\CVProject\\resource\\pictures\\positive\\test");
        List<File> files = Arrays.asList(dir.listFiles());
        Rotater.rotateImages(files, "png", 10, 20, 30, 40);
    }
}
