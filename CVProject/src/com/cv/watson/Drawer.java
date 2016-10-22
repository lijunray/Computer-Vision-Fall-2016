package com.cv.watson;

import org.math.plot.Plot2DPanel;

import javax.swing.*;

/**
 * Created by Ray on 2016/10/10.
 */
public class Drawer {
    /**
     * Draw the line plot
     * @param name line's name
     * @param title title
     * @param width plot's width
     * @param height plot's height
     * @param x FPR
     * @param y TPR
     */
    public static void draw(String name, String title, int width, int height, double[] x, double[] y) {
        if (x.length == 0 || y.length == 0) {
            System.out.println("One or both of the data array are empty!");
            return ;
        }
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();

        // define the legend position
        plot.addLegend("SOUTH");

        System.out.println("size of TPR:" + x.length);
        System.out.println("size of FNR:" + y.length);

        // add a line plot to the PlotPanel
        plot.addLinePlot(name, x, y);

        // put the PlotPanel in a JFrame like a JPanel
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }

    /**
     * Draw the line plot with 2 lines, one is before rotation, another is after.
     * @param name plot line's name
     * @param title title
     * @param width plot's width
     * @param height plot's height
     * @param x1 FPR before rotation
     * @param y1 TPR before rotation
     * @param x2 FPR after rotation
     * @param y2 TPR after rotation
     */
    public static void draw(String name, String title, int width, int height, double[] x1, double[] y1, double[] x2, double[] y2) {
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();

        // define the legend position
        plot.addLegend("SOUTH");

        // add a line plot to the PlotPanel
        plot.addLinePlot(name, x1, y1);
        plot.addLinePlot(name + " Rotated", x2, y2);

        // put the PlotPanel in a JFrame like a JPanel
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }
}
