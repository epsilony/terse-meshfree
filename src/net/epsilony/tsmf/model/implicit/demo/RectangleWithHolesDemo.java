/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit.demo;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.model.implicit.RectangleWithHoles;
import net.epsilony.tsmf.model.implicit.TriangleContourBuilder;
import net.epsilony.tsmf.model.implicit.TriangleContourCell;
import net.epsilony.tsmf.model.implicit.TriangleContourCellFactory;
import net.epsilony.tsmf.util.MiscellaneousUtils;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.SingleModelShapeDrawer;

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
    TriangleContourBuilderDemoDrawer triangleContourDrawer;

    private void genTriangleContourDrawer() {
        TriangleContourCellFactory factory = new TriangleContourCellFactory();
        TriangleContourCell[][] cellGrid = factory.coverRectangle(rectangle, triangleSize);
        LinkedList<TriangleContourCell> cells = new LinkedList<>();
        MiscellaneousUtils.addToList(cellGrid, cells);
        TriangleContourBuilder contourBuilder = new TriangleContourBuilder();
        contourBuilder.setCells(cells);
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
