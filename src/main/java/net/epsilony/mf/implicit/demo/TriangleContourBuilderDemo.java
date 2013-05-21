/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.epsilony.mf.implicit.TriangleContourCell;
import net.epsilony.mf.implicit.TriangleContourCellFactory;
import net.epsilony.mf.implicit.TriangleContourBuilder;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.ui.BasicModelPanel;
import net.epsilony.tb.ui.CommonFrame;
import net.epsilony.tb.ui.ModelDrawerAdapter;
import net.epsilony.tb.ui.ModelTransform;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilderDemo extends MouseAdapter {

    public static final int DRAG_OUTER = 1;
    public static final int DRAG_HOLE = 2;
    public static final int DRAG_NOTHING = 0;
    public TriangleContourBuilder polygonizer;
    public double holeRadius = 15;
    public double holeX = 44;
    public double holeY = 42;
    public double outerRadius = 30;
    public double outerX = 50;
    public double outerY = 50;
    public static final double HOLD_RADIUS_SUP = 40;
    double dragX, dragY;
    int dragStatus;

    private void genPolygonizer() {
        TriangleContourCellFactory fatory = new TriangleContourCellFactory();
        TriangleContourCell[][] coverRectangle = fatory.coverRectangle(new Rectangle2D.Double(0, 0, 100, 100), 5);
        LinkedList<TriangleContourCell> cells = new LinkedList<>();
        MiscellaneousUtils.addToList(coverRectangle, cells);
        polygonizer = new TriangleContourBuilder();
        polygonizer.setCells(cells);
        polygonizer.setLevelSetFunction(new SampleFunction());
    }

    class SampleFunction implements GenericFunction<double[], double[]> {

        @Override
        public double[] value(double[] input, double[] output) {
            if (null == output) {
                output = new double[1];
            }
            double x = input[0];
            double y = input[1];
            double distanceToDiskCenter = Math.pow(x - outerX, 2) + Math.pow(y - outerY, 2);
            distanceToDiskCenter = Math.sqrt(distanceToDiskCenter);
            double outerValue = outerRadius - distanceToDiskCenter;

            double distanceToHoleCenter = (x - holeX) * (x - holeX) + (y - holeY) * (y - holeY);
            distanceToHoleCenter = Math.sqrt(distanceToHoleCenter);
            double holeValue = distanceToHoleCenter - holeRadius;
            output[0] = Math.min(outerValue, holeValue);
            return output;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragStatus = genDragStatus(e);
        if (dragStatus == DRAG_NOTHING) {
            return;
        }
        dragX = e.getX();
        dragY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int newDragStatus = genDragStatus(e);
        if (dragStatus != DRAG_NOTHING && newDragStatus == DRAG_NOTHING) {
            e.getComponent().repaint();
        }
        dragStatus = newDragStatus;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragStatus = genDragStatus(e);
        if (dragStatus == DRAG_NOTHING) {
            return;
        }

        BasicModelPanel panel = (BasicModelPanel) e.getComponent();
        ModelTransform modelToComponentTransform = panel.getModelToComponentTransform();
        double[] oriVector = new double[]{dragX, dragY};
        double[] dragedVector = new double[]{e.getX(), e.getY()};
        try {
            modelToComponentTransform.inverseTransform(oriVector, 0, oriVector, 0, 1);
            modelToComponentTransform.inverseTransform(dragedVector, 0, dragedVector, 0, 1);
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(TriangleContourBuilderDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

        double centerX, centerY;
        if (dragStatus == DRAG_HOLE) {
            centerX = holeX;
            centerY = holeY;
        } else {
            centerX = outerX;
            centerY = outerY;
        }

        double oriR = Math.sqrt(Math.pow(oriVector[0] - centerX, 2) + Math.pow(oriVector[1] - centerY, 2));
        double dragedR = Math.sqrt(Math.pow(dragedVector[0] - centerX, 2) + Math.pow(dragedVector[1] - centerY, 2));
        double radiusDelta = dragedR - oriR;
        if (dragStatus == DRAG_HOLE) {
            holeRadius += radiusDelta;
        } else {
            outerRadius += radiusDelta;
        }
        dragX = e.getX();
        dragY = e.getY();

        if (holeRadius < 0) {
            holeRadius = 0;
        }

        if (outerRadius < 0) {
            outerRadius = 0;
        }
        polygonizer.genContour();
        panel.repaint();
    }

    public static int genDragStatus(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK
                && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0
                && (e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == 0) {
            if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK) {
                return DRAG_OUTER;
            } else {
                return DRAG_HOLE;
            }
        }
        return DRAG_NOTHING;
    }

    public void genUI() {
        genPolygonizer();
        try {
            polygonizer.genContour();
        } catch (Throwable e) {
            System.out.println("e = " + e);
        }
        CommonFrame frame = new CommonFrame();
        frame.getMainPanel().addAndSetupModelDrawer(new TriangleContourBuilderDemoDrawer(polygonizer));
        frame.getMainPanel().setPreferredSize(new Dimension(800, 600));
        frame.getMainPanel().addMouseListener(this);
        frame.getMainPanel().addMouseMotionListener(this);
        frame.getMainPanel().addAndSetupModelDrawer(new DraggingDrawer());
        frame.getContentPane().add(new JLabel("Draw with right key or +SHILF"));
        frame.getContentPane().setLayout(new FlowLayout());
        frame.pack();
        frame.setVisible(true);
    }

    class DraggingDrawer extends ModelDrawerAdapter {

        @Override
        public Rectangle2D getBoundsInModelSpace() {
            return null;
        }

        @Override
        public void drawModel(Graphics2D g2) {
            if (dragStatus == DRAG_NOTHING) {
                return;
            }
            double centerX, centerY;
            if (dragStatus == DRAG_OUTER) {
                centerX = outerX;
                centerY = outerY;
            } else {
                centerX = holeX;
                centerY = holeY;
            }
            double[] centers = new double[]{centerX, centerY};
            modelToComponentTransform.transform(centers, 0, centers, 0, 1);
            Path2D path = new Path2D.Double();
            path.moveTo(centers[0], centers[1]);
            path.lineTo(dragX, dragY);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.MAGENTA);
            g2.draw(path);
        }
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
