package com.cv.watson;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ray on 2016/10/10.
 */
public class Handler {


    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";
    private static final int ZIP_LIMIT = 20;

    /**
     * Delete classifier on the server if exists one,
     * randomly select some images, copy them into a directory
     * then create a new classifier based on those random images,
     * and select some testing images copied into specific directories.
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param count count of training images
     * @param classifierName classifier name
     * @return a map containing both positive and negative scores
     * @throws Exception
     */
    public static void trainNormal(String apiKey, String positiveDirectoryPath, String negativeDirectoryPath,
                                   int count, String classifierName) throws Exception {
        List<File> positiveDirectories = Arrays.asList(new File(positiveDirectoryPath).listFiles());
        List<File> positiveFiles = new ArrayList<>();
        File negativeDirectory = new File(negativeDirectoryPath);

        List<File> positiveZipFiles = new ArrayList<>();

        for (File positiveDirectory : positiveDirectories) {
            if (!positiveDirectory.isDirectory()) {
                continue;
            }
            // remove directories and add them to positive files list
            positiveFiles.addAll(Arrays.asList(positiveDirectory.listFiles()).stream()
                            .filter(file -> Selector.isImage(file.getName())).collect(Collectors.toList()));
            List<File> randomPositiveFiles = copyToDirectory(Selector.select(Arrays.asList(positiveDirectory.listFiles())
                    .stream().filter(file -> Selector.isImage(file.getName())).collect(Collectors.toList()), count));
            // zip files
            File zipFile = new File(positiveDirectory.getPath() + ".zip");
            Selector.zip(randomPositiveFiles, 0, randomPositiveFiles.size() - 1, "", zipFile);
            positiveZipFiles.add(zipFile);
        }

        List<File> negativeFiles = Arrays.asList(negativeDirectory.listFiles());

        List<File> randomNegativeFiles = copyToDirectory(Selector.select(negativeFiles, count));
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\negative.zip");
        Selector.zip(randomNegativeFiles, 0, randomNegativeFiles.size() - 1, "", negativeZipFile);

        VisualClassifier classifier = Trainer.createClassifier(Trainer.createClassifierOptions(positiveZipFiles, negativeZipFile, classifierName), apiKey);

        // Randomly select files in directory but don't copy them
//        List<File> positiveTestFiles = Selector.selectTestFiles(positiveFiles, positiveTestCount);
//        List<File> negativeTestFiles = Selector.selectTestFiles(negativeFiles, negativeTestCount);



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
     * @param classifierName name of created classifier
     * @param format format for rotated images
     * @param angles angles for images to be rotated on
     * @return a map containing both positive and negative test scores
     * @throws Exception
     */
    public static void trainRotated(String apiKey, String positiveDirectoryPath, String negativeDirectoryPath,
                                   String classifierName, String format, int... angles) throws IOException {
        List<File> positiveDirectories = Arrays.asList(new File(positiveDirectoryPath).listFiles());
        File negativeDirectory = new File(negativeDirectoryPath);

        List<File> positiveZipFiles = new ArrayList<>();

        for (File positiveDirectory : positiveDirectories) {
            if (!positiveDirectory.isDirectory() || positiveDirectory.listFiles().length == 0
                    || positiveDirectory.getName().equals("positive_test")) {
                continue;
            }
            File randomPositiveDirectory = new File(String.format("%s\\%s_train", positiveDirectory.getPath(),
                    positiveDirectory.getName()));
            // remove directories and add them to positive files list
            File rotatedDirectory = new File(String.format("%s\\%s_rotated", positiveDirectory.getPath(),
                    positiveDirectory.getName()));
            List<File> positiveFiles = Arrays.asList(randomPositiveDirectory.listFiles());
            deleteDirectory(rotatedDirectory);
            List<File> rotatedPositiveFiles = Rotater.rotateImages(positiveFiles, format, rotatedDirectory, angles);
            // zip files
            File zipFile = new File(positiveDirectory.getPath() + ".zip");
            Selector.zip(rotatedPositiveFiles, 0, rotatedPositiveFiles.size() - 1, "", zipFile);
            positiveZipFiles.add(zipFile);
        }

        List<File> negativeFiles = Arrays.asList(new File(negativeDirectoryPath + "\\negative_train").listFiles());
        File rotatedNegativeDirectory = new File(negativeDirectory.getPath() + "\\negative_rotated");
        deleteDirectory(rotatedNegativeDirectory);
        List<File> rotatedNegativeFiles = Rotater.rotateImages(negativeFiles, format, rotatedNegativeDirectory, angles);
        File negativeZipFile = new File(negativeDirectory.getPath() + "\\negative.zip");
        Selector.zip(rotatedNegativeFiles, 0, rotatedNegativeFiles.size() - 1, "", negativeZipFile);

        VisualClassifier classifier = Trainer.createClassifier(Trainer.createClassifierOptions(positiveZipFiles,
                negativeZipFile, classifierName), apiKey);

    }

    /**
     * Randomly select images in positive and negative directory
     * Then classify both positive and negative images
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param positiveCount positive count of testing images
     * @param negativeCount negative count of testing images
     * @param sleepTime sleep time between 2 testing
     * @return a map containing both positive and negative scores
     * @throws Exception
     */
    public static Map<String, List<Double>> classifyRandomImages(String apiKey, String positiveDirectoryPath,
                                                           String negativeDirectoryPath, long sleepTime,
                                                           int positiveCount, int negativeCount) throws Exception {
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

        // Create a directory to save positive testing files
        File positiveTestDirectory = new File(String.format("%s\\positive_test", positiveDirectoryPath));
        deleteDirectory(positiveTestDirectory);

        // Create a directory to save positive testing files
        File negativeTestDirectory = new File(String.format("%s\\negative_test", negativeDirectoryPath));
        deleteDirectory(negativeTestDirectory);

        // Randomly select testing files
        List<File> positiveTestFiles = Selector.selectFiles(positiveFiles, positiveCount, positiveTestDirectory);
        List<File> negativeTestFiles = Selector.selectFiles(negativeFiles, negativeCount, negativeTestDirectory);

        return handleClassify(apiKey, sleepTime, classifier, positiveTestFiles, negativeTestFiles);
    }

    /**
     * Classify both positive and negative images in existed directories
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory
     * @param negativeDirectoryPath negative images directory
     * @param sleepTime sleep time between 2 testing
     * @return a map containing both positive and negative scores
     * @throws Exception
     */
    public static Map<String, List<Double>> classifyRotated(String apiKey, String positiveDirectoryPath,
                                                   String negativeDirectoryPath, long sleepTime) throws Exception {
        VisualClassifier classifier = Trainer.fetchClassifier(apiKey);
        if (classifier == null) {
            System.out.printf("Classifier Not Found!%n");
            return null;
        }

        System.out.printf("Classifier id: %s%n", classifier.getId());

        // Randomly select testing files
        File positiveTestDirectory = new File(positiveDirectoryPath + "\\positive_test");
        File negativeTestDirectory = new File(negativeDirectoryPath + "\\negative_test");

        List<File> positiveTestFiles = Arrays.asList(positiveTestDirectory.listFiles());
        List<File> negativeTestFiles = Arrays.asList(negativeTestDirectory.listFiles());

        return handleClassify(apiKey, sleepTime, classifier, positiveTestFiles, negativeTestFiles);
    }

    /**
     * Print Confidence Intervals in console
     * @param apiKey api key
     * @param positiveDirectoryPath positive images directory path
     * @param negativeDirectoryPath negative images directory path
     * @param sleepTime sleep time between 2 tests
     * @param count number of train images
     * @param classifierName classifier's name
     * @param CITimes classifying times
     * @param offset offset of threshold
     * @param CIRate rate for confidence intervals
     * @param positiveTestCount number of positive testing images
     * @param negativeTestCount number of negative testing images      @throws Exception
     * */
    public static void getCI(String apiKey, String positiveDirectoryPath, String negativeDirectoryPath,
                             long sleepTime, int count, String classifierName, int CITimes, double offset,
                             double CIRate, int positiveTestCount, int negativeTestCount) throws Exception {
        List<List<Double>> tprCIs = new ArrayList<>();
        List<List<Double>> fprCIs = new ArrayList<>();
        for (int i = 0; i < 1 / offset; i++) {
            List<Double> tprCI = new ArrayList<>();
            List<Double> fprCI = new ArrayList<>();
            tprCIs.add(tprCI);
            fprCIs.add(fprCI);
        }
        for (int i = 0; i < CITimes; i++) {
            Handler.trainNormal(apiKey, positiveDirectoryPath, negativeDirectoryPath,
                    count, classifierName);
            Map<String, List<Double>> map = Handler.classifyRandomImages(apiKey, positiveDirectoryPath,
                    negativeDirectoryPath, sleepTime, positiveTestCount, negativeTestCount);
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
        int ciCount = (int) (CITimes * CIRate);
        int start = (CITimes - ciCount) >> 1;
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
     * @param sleepTime sleep time between 2 tests
     * @param classifier classifier to be classified
     * @param positiveTestFiles list of positive test files
     * @param negativeTestFiles list of negative test files
     * @return
     * @throws Exception
     */
    private static Map<String, List<Double>> handleClassify(String apiKey,
                                                            long sleepTime,
                                                            VisualClassifier classifier,
                                                            List<File> positiveTestFiles,
                                                            List<File> negativeTestFiles) {
        Map<String, List<Double>> map = new HashMap<>();

        List<Double> positiveScores = new ArrayList<>();
        List<Double> negativeScores = new ArrayList<>();

        System.out.printf("Positive classification begins.%n");

        for (int i = 0; i < Math.round(positiveTestFiles.size() / ZIP_LIMIT); i++) {
            try {
                System.out.printf("Classify time: %d.%n", i + 1);
                positiveScores.addAll(classify(apiKey, Handler.POSITIVE, positiveTestFiles,
                        i * ZIP_LIMIT, (i + 1) * ZIP_LIMIT, classifier));
                System.out.printf("Success! Now sleep for %d seconds...%n", sleepTime);
                Thread.sleep(sleepTime);
            } catch (Exception e) {}
        }

        System.out.printf("Positive Classification finished.%n");
        System.out.printf("Negative classification begins.%n");


        for (int i = 0; i < Math.round(negativeTestFiles.size() / ZIP_LIMIT); i++) {
            try {
                System.out.printf("Classify time: %d.%n", i + 1);
                negativeScores.addAll(classify(apiKey, Handler.NEGATIVE, negativeTestFiles,
                        i * ZIP_LIMIT, (i + 1) * ZIP_LIMIT, classifier));
                System.out.printf("Sleep for %d seconds...%n", sleepTime);
                Thread.sleep(sleepTime);
            } catch (Exception e) {}
        }

        System.out.printf("Negative Classification finished.%n");

        map.put(Handler.POSITIVE, positiveScores);
        map.put(Handler.NEGATIVE, negativeScores);

        return map;
    }

    private static List<Double> classify(String apiKey, String type, List<File> files, int begin, int end,
                                         VisualClassifier classifier) throws Exception {
        String grandparentPath = files.get(begin).getParentFile().getParentFile().getPath();

        File testZipFile = new File(String.format("%s\\%s_test.zip", grandparentPath, type));

        testZipFile.delete();

        Selector.zip(files, begin, end, "", testZipFile);

        System.out.printf("+++++++++++++++++%s+++++++++++++++++%n", type);
        VisualClassification classification = Tester.classify(testZipFile, classifier, apiKey);
//        System.out.printf("%s classification: %n%s%n", type, classification);
        List<Double> scores = Calculator.getScores(classification);
        System.out.printf("scores: %s%n", scores);
        return scores;
    }

    private static List<File> copyToDirectory(List<File> randomFiles) throws IOException {
        List<File> newFiles = new ArrayList<>();
        File parent = randomFiles.get(0).getParentFile();
        File directory = new File(String.format("%s\\%s_train", parent.getPath(), parent.getName()));
        deleteDirectory(directory);
        int name = 1;
        for (File file : randomFiles) {
            File newFile = new File(String.format("%s\\%d.jpg", directory.getPath(), name++));
            System.out.printf("Copying %s to %s...%n", file.getPath(), newFile.getPath());
            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            newFiles.add(newFile);
        }
        return newFiles;
    }

    /**
     * Recursively delete all files in a directory.
     * If not exist, create a new directory.
     * @param directory directory to delete
     */
    public static void deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        else {
            directory.mkdir();
        }
    }
}
