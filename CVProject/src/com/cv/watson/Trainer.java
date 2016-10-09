package com.cv.watson;


import com.cv.Main;
import com.ibm.watson.developer_cloud.util.Validator;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Ray on 2016/10/3.
 */
public class Trainer {

//    private static final String PATH_CLASSIFIERS = "https://gateway-a.watsonplatform.net/visual-recognition/api/v3/classifiers";
//    private static final String VERSION = "version";
//    private static final String PARAM_NAME = "name";
//    private static final String PARAM_POSITIVE_EXAMPLES = "positive_examples";
//    private static final String PARAM_NEGATIVE_EXAMPLES = "negative_examples";

    private static byte[] buffer = new byte[2048];
    private final String apiKey;

    public Trainer(String apiKey) {
        this.apiKey = apiKey;
    }

    /*
      *
      * @param animalDirectory the directory of images
      * @param trainZipFile zip file to be written into
      * @param testZipFile test zip file to be written into
      * @return test file array
     */
    public Map<String, List<File>> getZips(File positiveDirectory, File negativeDirectory, File positiveZipFile,
                               File negativeZipFile) throws Exception {
        System.out.println(positiveDirectory.getPath());
        System.out.println(negativeDirectory.getPath());

        positiveZipFile.delete();
//        testZipFile.delete();
        negativeZipFile.delete();

        List<File> trainPositiveImages = Arrays.asList(positiveDirectory.listFiles());
        List<File> trainNegativeImages = Arrays.asList(negativeDirectory.listFiles());

        // Generate set of positive numbers
        int positiveCount = trainPositiveImages.size();
        int negativeCount = trainNegativeImages.size();
        int count = trainPositiveImages.size() * 8 / 10;
        Set<Integer> randomPositiveNumbers = getRandomSet(positiveCount, count);
        Set<Integer> randomNegativeNumbers = getRandomSet(negativeCount, count);

        // Generate positive training files and test files
        List<File> trainPositiveFiles = new ArrayList<>();
        List<File> trainNegativeFiles = new ArrayList<>();
        List<File> testPositiveFiles = new ArrayList<>();
        List<File> testNegativeFiles = new ArrayList<>();

        for (int i = 0; i < positiveCount; i++) {
            if (randomPositiveNumbers.contains(i)) {
                trainPositiveFiles.add(trainPositiveImages.get(i));
            }
            else {
                testPositiveFiles.add(trainPositiveImages.get(i));
            }
        }

        //Generate negative training files
        for (int i = 0; i < negativeCount; i++) {
            if (randomNegativeNumbers.contains(i)) {
                trainNegativeFiles.add(trainNegativeImages.get(i));
            }
            else {
                testNegativeFiles.add(trainNegativeImages.get(i));
            }
        }

        zip(trainPositiveFiles, "", positiveZipFile);
        zip(trainNegativeFiles, "", negativeZipFile);

        Map<String, List<File>> map = new HashMap<>();
        map.put(Handler.POSITIVE, testPositiveFiles);
        map.put(Handler.NEGATIVE, testNegativeFiles);

        return map;
    }

    /*
    * @param classifierName the name of classifer
    * @param positiveExample positive zip file
    * @param negativeExample negative zip file
    * @return new ClassifierOptions object
    * */
    public ClassifierOptions createClassifierOptions(String classifierName, File positiveExample, File negativeExample) {
        return new ClassifierOptions.Builder()
                .classifierName(classifierName)
                .addClass(classifierName, positiveExample)
                .negativeExamples(negativeExample)
                .build();
    }

    /*
    * @param options options of classifier
    * @return VisualClassifier
    * */
    public VisualClassifier createClassifier(ClassifierOptions options) {
        Validator.notNull(options, "options cannot be null");
        VisualClassifier classifier = fetchClassifier();
        VisualRecognition service =
                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, this.apiKey);
        // delete classifiers if one exists in server
        if (classifier != null) {
            System.out.println("Deleting classifier...");
            service.deleteClassifier(classifier.getId()).execute();
            System.out.println("Deleted classifier");
        }

        System.out.println("Creating classifier...");
        VisualClassifier newClassifier = service.createClassifier(options).execute();
        System.out.println("Created classifier:" + newClassifier.getName() + " with id of " + newClassifier.getId());
        return newClassifier;
    }

    /*
    * @return the classifier fetched from server
    * */
    public VisualClassifier fetchClassifier() {
        VisualRecognition service =
                new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20, this.apiKey);

        // get all classifiers in server
        System.out.println("Fetching classifiers...");
        List<VisualClassifier> classifiers = service.getClassifiers().execute();
        System.out.println("Got classifiers");
        if (!classifiers.isEmpty()) {
            return classifiers.get(0);
        }
        return null;
    }

    /*
    * @param files files to be zipped
    * @param baseFolder base folder
    * @param out file to be written into
    * */
    public void zip(List<File> files, String baseFolder, File out) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out));
        FileInputStream fis;
        ZipEntry entry;
        int count;
        for (File file : files) {
            if (file.isDirectory() || file.getPath().endsWith(".zip")) {
                continue;
            }
            entry = new ZipEntry(baseFolder + file.getName());
            zos.putNextEntry(entry);
            fis = new FileInputStream(file);
            while ((count = fis.read(buffer, 0, buffer.length)) != -1)
                zos.write(buffer, 0, count);
        }
        zos.close();
    }

    /*
    * @param bound bound of random numbers
    * @param size count of random numbers
    * @return a set including all generated random numbers
    * */
    public Set<Integer> getRandomSet(int bound, int size) {
        Set<Integer> randomSet = new HashSet<>();
        while (randomSet.size() < size) {
            int randomNumber = new Random().nextInt(bound);
            randomSet.add(randomNumber);
        }
        return randomSet;
    }
}
