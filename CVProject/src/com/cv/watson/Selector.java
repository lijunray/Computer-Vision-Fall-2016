package com.cv.watson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Ray on 2016/10/10.
 */
public class Selector {
    private static final byte[] buffer = new byte[2048];
    public static final String NEGATIVE_PATH = "\\negative.zip";
    public static final String TRAIN = "_train";
    public static final String TEST = "_test";
    private final File positiveDirectory;
    private final File negativeDirectory;
    private final int testCount;

    public Selector(String positiveDirectoryPath, String negativeDirectoryPath, int testCount) {
        this.positiveDirectory = new File(positiveDirectoryPath);
        this.negativeDirectory = new File(negativeDirectoryPath);
        this.testCount = testCount;
    }

    /**
     * Select count files from directory randomly
     * @param directory to be selected from
     * @param count number of files
     * @return a list of files selected randomly
     */
    public static List<File> select(File directory, int count) {
        System.out.println(directory.getPath());

        List<File> files = Arrays.asList(directory.listFiles());
        List<File> trainFiles = new ArrayList<>();

        int bound = files.size();
        Set<Integer> randomNumbers = getRandomSet(bound, count);

        for (int i = 0; i < bound; i++) {
            if (randomNumbers.contains(i)) {
                trainFiles.add(files.get(i));
            }
        }

        return trainFiles;
    }

    public static List<File> selectTestFiles(List<File> files, int count) {
        Set<String> names = new HashSet<>();
        List<File> testFiles = new ArrayList<>();
        Set<Integer> randomNumbers = getRandomSet(files.size(), count);
        for (int i = 0; i < files.size(); i++) {
            if (!names.contains(files.get(i).getName()) && randomNumbers.contains(i)) {
                testFiles.add(files.get(i));
                names.add(files.get(i).getName());
            }
        }
        return testFiles;
    }

    /*
        * @param bound bound of random numbers
        * @param size count of random numbers
        * @return a set including all generated random numbers
        * */
    public static Set<Integer> getRandomSet(int bound, int size) {
        Set<Integer> randomSet = new HashSet<>();
        while (randomSet.size() < size) {
            int randomNumber = new Random().nextInt(bound);
            randomSet.add(randomNumber);
        }
        return randomSet;
    }

    /*
    * @param files files to be zipped
    * @param baseFolder base folder
    * @param out file to be written into
    * */
    public static void zip(List<File> files, String baseFolder, File out) throws Exception {
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
}
