/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit.demo;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.model.implicit.TriangleContourCell;
import net.epsilony.tsmf.model.implicit.TriangleContourCellFactory;
import net.epsilony.tsmf.model.implicit.TriangleContourBuilder;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.MiscellaneousUtils;
import net.epsilony.tsmf.util.ui.CommonFrame;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilderDemo {

    public TriangleContourBuilder polygonizer;

    private void genPolygonizer() {
        TriangleContourCellFactory fatory = new TriangleContourCellFactory();
        TriangleContourCell[][] coverRectangle = fatory.coverRectangle(new Rectangle2D.Double(0, 0, 100, 60), 5);
        LinkedList<TriangleContourCell> cells = new LinkedList<>();
        MiscellaneousUtils.addToList(coverRectangle, cells);
        polygonizer = new TriangleContourBuilder();
        polygonizer.cells = cells;
        polygonizer.levelSetFunction = new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                if (null == output) {
                    output = new double[1];
                }
                double x = input[0];
                double y = input[1];
                double r = (x - 50) * (x - 50) + (y - 30) * (y - 30);
                r = Math.sqrt(r);
                r = 30 - r;
                output[0] = r;
                return output;
            }
        };
        polygonizer.contourLevel = 0;
    }

    public void genUI() {
        genPolygonizer();
        try {
            polygonizer.genCountour();
        } catch (Throwable e) {
            System.out.println("e = " + e);
        }
        CommonFrame frame = new CommonFrame();
        frame.getMainPanel().addAndSetupModelDrawer(new TriangleContourBuilderDemoDrawer(polygonizer));
        frame.getMainPanel().setPreferredSize(new Dimension(800, 600));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TriangleContourBuilderDemo demo = new TriangleContourBuilderDemo();
                demo.genUI();
            }
        });
    }
}
