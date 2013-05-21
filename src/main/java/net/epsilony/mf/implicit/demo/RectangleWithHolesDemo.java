/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.epsilony.mf.implicit.RectangleWithHoles;
import net.epsilony.mf.implicit.TriangleContourBuilder;
import net.epsilony.tb.ui.BasicModelPanel;
import net.epsilony.tb.ui.CommonFrame;
import net.epsilony.tb.ui.SingleModelShapeDrawer;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHolesDemo {

    Rectangle2D rectangle = new Rectangle2D.Double(10, 20, 100, 60);
    double holeRadius = 5;
    double holeDistance = 2;
    RectangleWithHoles rectangleWithHoles = new RectangleWithHoles(rectangle, holeRadius, holeDistance);
    SingleModelShapeDrawer singleShapeDrawer = new SingleModelShapeDrawer(rectangleWithHoles.genShape());
    double triangleSize = 1;
    double spaceNodesExtention = triangleSize * 2;
    TriangleContourBuilderDemoDrawer triangleContourDrawer;

    private void genTriangleContourDrawer() {
        rectangleWithHoles.setTriangleSize(triangleSize);
        rectangleWithHoles.setSpaceNodesExtension(spaceNodesExtention);
        rectangleWithHoles.prepare();
        TriangleContourBuilder contourBuilder = new TriangleContourBuilder();
        contourBuilder.setCells(rectangleWithHoles.getTriangles());
        contourBuilder.setLevelSetFunction(rectangleWithHoles);
        contourBuilder.genContour();
        triangleContourDrawer = new TriangleContourBuilderDemoDrawer(contourBuilder);
        triangleContourDrawer.setTriangleNodesVisible(false);
    }

    public void createUI() {
        CommonFrame frame = new CommonFrame();
        frame.getMainPanel().addAndSetupModelDrawer(singleShapeDrawer);
        genTriangleContourDrawer();
        frame.getMainPanel().addAndSetupModelDrawer(triangleContourDrawer);
        singleShapeDrawer.setFilling(true);
        singleShapeDrawer.setFillingAlpha(0.1);
        frame.getMainPanel().setPreferredSize(new Dimension(1024, 768));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RectangleWithHolesDemo().createUI();
            }
        });
    }
}
