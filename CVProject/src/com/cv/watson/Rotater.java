package com.cv.watson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/10/20.
 */
public class Rotater {

    /**
     * Rotate a set of images for different angles and save them as a List of file
     * @param files original image files
     * @param format rotated images' format
     * @param angles angles to rotate
     * @return rotated image files
     */
    public static List<File> rotateImages(List<File> files, String format, int... angles) throws IOException {
        List<File> rotatedImageFiles = new ArrayList<>();
        if (files.isEmpty()) {
            return rotatedImageFiles;
        }
        File parent = files.get(0).getParentFile();
        File directory = new File(String.format("%s\\%s_rotated", parent.getPath(), parent.getName()));
        if (directory.exists()) {
            directory.delete();
        }
        directory.mkdir();
        for (File file : files) {
            List<File> temp = rotateAnImage(directory, file, format, angles);
            rotatedImageFiles.addAll(temp);
        }
        return rotatedImageFiles;
    }

    /**
     * Rotate an image for different angles and save them as a List of file
     * @param file original image file
     * @param format rotated images' format
     * @param angles angles to rotate
     * @return rotated image files
     */
    public static List<File> rotateAnImage(File directory, File file, String format, int... angles) throws IOException {
        System.out.printf("%s%n", file.getPath());

        Image image = ImageIO.read(file);
        int src_width = image.getWidth(null);
        int src_height = image.getHeight(null);
        List<File> rotatedImages = new ArrayList<>();
        for (int angle : angles) {
            // calculate the new image size
            Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(
                    src_width, src_height)), angle);

            BufferedImage rotatedImage = new BufferedImage(rect_des.width, rect_des.height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = rotatedImage.createGraphics();
            // transform
            g2.translate((rect_des.width - src_width) / 2,
                    (rect_des.height - src_height) / 2);
            g2.rotate(Math.toRadians(angle), src_width / 2, src_height / 2);

            g2.drawImage(image, null, null);

            String name = file.getName().split(".jpg")[0];

            File rotatedImageFile = new File(String.format("%s\\%s_%d.%s", directory.getPath(), name, angle, format));

//            System.out.printf("Writing to %s%n", rotatedImageFile.getPath());
            ImageIO.write(rotatedImage, format, rotatedImageFile);

            rotatedImages.add(rotatedImageFile);
        }
        return rotatedImages;
    }

    /**
     * Calculate the rotated image's size
     * @param rectangle rectangle of original size
     * @param angel angle to be rotated as
     * @return a rectangle with new size
     */
    private static Rectangle calcRotatedSize(Rectangle rectangle, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if(angel / 90 % 2 == 1){
                int temp = rectangle.height;
                rectangle.height = rectangle.width;
                rectangle.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(rectangle.height * rectangle.height + rectangle.width * rectangle.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) rectangle.height / rectangle.width);
        double angel_dalta_height = Math.atan((double) rectangle.width / rectangle.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = rectangle.width + len_dalta_width * 2;
        int des_height = rectangle.height + len_dalta_height * 2;
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
    }
}
