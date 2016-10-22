package com.cv.watson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Ray on 2016/10/10.
 */
public class Selector {
    private static final byte[] buffer = new byte[2048];

    /**
     * Select count files from directory randomly
     * @param files a list of files to be selected from
     * @param count number of files
     * @return a list of files selected randomly
     */
    public static List<File> select(List<File> files, int count) {
        if (files.isEmpty()) {
            return null;
        }
        System.out.println(files.get(0).getParentFile().getPath());

        List<File> trainFiles = new ArrayList<>();

        int bound = files.size();
        Set<Integer> randomNumbers = getRandomSet(bound, count);

        for (int i = 0; i < bound; i++) {
            if (randomNumbers.contains(i)) {
                String fileName = files.get(i).getName();
                if (isImage(fileName)) {
                    trainFiles.add(files.get(i));
                }
            }
        }

        return trainFiles;
    }

    public static List<File> selectFiles(List<File> files, int count, File directory) {
        if (files.isEmpty()) {
            return null;
        }
        System.out.println(files.get(0).getParentFile().getPath());

        List<File> trainFiles = new ArrayList<>();

        int bound = files.size();
        Set<Integer> randomNumbers = getRandomSet(bound, count);

        for (int i = 0; i < bound; i++) {
            if (randomNumbers.contains(i)) {
                String fileName = files.get(i).getName();
                if (isImage(fileName)) {
                    trainFiles.add(files.get(i));
                }
            }
        }

        List<File> newFiles = new ArrayList<>();
        int inc = 1;
        for (File file : trainFiles) {
            File newFile = new File(String.format("%s\\%d.jpg", directory.getPath(), inc++));
            try {
                Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                newFiles.add(newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFiles;
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

    /**
     * @param bound bound of random numbers
     * @param size count of random numbers
     * @return a set including all generated random numbers
     */
    public static Set<Integer> getRandomSet(int bound, int size) {
        Set<Integer> randomSet = new HashSet<>();
        if (bound <= 0) {
            return randomSet;
        }
        while (randomSet.size() < size) {
            int randomNumber = new Random().nextInt(bound);
            randomSet.add(randomNumber);
        }
        return randomSet;
    }

    /**
     * @param files files to be zipped
     * @param baseFolder base folder
     * @param out file to be written into
     */
    public static void zip(List<File> files, int begin, int end, String baseFolder, File out) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out));
        FileInputStream fis;
        ZipEntry entry;
        int count;
        for (int i = begin; i <= end; i++) {
            File file = files.get(i);
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

    private static boolean isImage(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG")
                || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith("JPEG")
                || fileName.endsWith(".gif") || fileName.endsWith(".GIF");
    }
}
