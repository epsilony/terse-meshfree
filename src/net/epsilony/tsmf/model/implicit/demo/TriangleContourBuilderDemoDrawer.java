/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.implicit.TriangleContourBuilder;
import net.epsilony.tsmf.model.implicit.TriangleContourCell;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.util.ui.ModelDrawer;
import net.epsilony.tsmf.util.ui.ModelDrawerAdapter;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilderDemoDrawer extends ModelDrawerAdapter {

    public static Color DEFAULT_CONTOUR_COLOR = Color.PINK;
    TriangleContourBuilder trianglePolygonizer;
    List<TriangleContourCellDemoDrawer> triangleDrawers;
    NodeDrawer nodeDrawer = new NodeDrawer();

    public TriangleContourBuilderDemoDrawer(TriangleContourBuilder trianglePolygonizer) {
        this.trianglePolygonizer = trianglePolygonizer;
        genDrawers();
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        Rectangle2D rect = new Rectangle2D.Double();
        for (ModelDrawer drawer : triangleDrawers) {
            Rectangle2D.union(rect, drawer.getBoundsInModelSpace(), rect);
        }
        return rect;
    }

    @Override
    public void drawModel(Graphics2D g2) {
        for (ModelDrawer drawer : triangleDrawers) {
            drawer.drawModel(g2);
        }

        drawContourNodes(g2);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(DEFAULT_CONTOUR_COLOR);
        g2.draw(modelToComponentTransform.createTransformedShape(genContourPath()));
    }

    private void drawContourNodes(Graphics2D g2) {
        for (Segment2D chainHead : trianglePolygonizer.contourHeads) {
            nodeDrawer.setColor(DEFAULT_CONTOUR_COLOR);
            nodeDrawer.setNode(chainHead.getHead());
            nodeDrawer.drawModel(g2);
            Segment2D seg = chainHead.getSucc();
            while (seg != null && seg != chainHead) {
                nodeDrawer.setNode(seg.getHead());
                nodeDrawer.drawModel(g2);
                seg = seg.getSucc();
            }
        }
    }

    private Path2D genContourPath() {
        Path2D path = new Path2D.Double();
        for (Segment2D chainHead : trianglePolygonizer.contourHeads) {
            double[] headCoord = chainHead.getHeadCoord();
            path.moveTo(headCoord[0], headCoord[1]);
            Segment2D seg = chainHead.getSucc();
            while (seg != null && seg != chainHead) {
                double[] segHeadCoord = seg.getHeadCoord();
                path.lineTo(segHeadCoord[0], segHeadCoord[1]);
                seg = seg.getSucc();
            }
            if (seg == chainHead) {
                path.closePath();
            }
        }
        return path;
    }

    @Override
    public void setComponent(Component component) {
        super.setComponent(component);
        for (ModelDrawer drawer : triangleDrawers) {
            drawer.setComponent(component);
        }
        nodeDrawer.setComponent(component);
    }

    @Override
    public void setModelToComponentTransform(AffineTransform modelToComponentTransform) {
        super.setModelToComponentTransform(modelToComponentTransform);
        for (ModelDrawer drawer : triangleDrawers) {
            drawer.setModelToComponentTransform(modelToComponentTransform);
        }
        nodeDrawer.setModelToComponentTransform(modelToComponentTransform);
    }

    private void genDrawers() {
        triangleDrawers = new LinkedList<>();
        for (TriangleContourCell cell : trianglePolygonizer.cells) {
            triangleDrawers.add(new TriangleContourCellDemoDrawer(cell));
        }
    }
}
