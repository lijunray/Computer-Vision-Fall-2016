package com.cv.watson;

import org.math.plot.Plot2DPanel;

import javax.swing.*;

/**
 * Created by Ray on 2016/10/10.
 */
public class Drawer {
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
}
