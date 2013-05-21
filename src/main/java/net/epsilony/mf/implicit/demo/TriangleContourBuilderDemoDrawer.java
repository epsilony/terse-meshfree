/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.mf.implicit.TriangleContourBuilder;
import net.epsilony.mf.implicit.TriangleContourCell;
import net.epsilony.tb.solid.ui.NodeDrawer;
import net.epsilony.tb.ui.ModelDrawer;
import net.epsilony.tb.ui.ModelDrawerAdapter;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilderDemoDrawer extends ModelDrawerAdapter {

    public static Color DEFAULT_CONTOUR_COLOR = Color.PINK;
    public static boolean DEFAULT_TRIANGLE_NODES_VISIBLE = true;
    TriangleContourBuilder trianglePolygonizer;
    List<TriangleContourCellDemoDrawer> triangleDrawers;
    NodeDrawer nodeDrawer = new NodeDrawer();
    boolean triangleNodesVisible = DEFAULT_TRIANGLE_NODES_VISIBLE;

    public TriangleContourBuilderDemoDrawer(TriangleContourBuilder trianglePolygonizer) {
        this.trianglePolygonizer = trianglePolygonizer;
        genDrawers();
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        Rectangle2D rect = triangleDrawers.get(0).getBoundsInModelSpace();
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
        for (LinearSegment2D chainHead : trianglePolygonizer.getContourHeads()) {
            nodeDrawer.setColor(DEFAULT_CONTOUR_COLOR);
            nodeDrawer.setNode(chainHead.getHead());
            nodeDrawer.drawModel(g2);
            LinearSegment2D seg = (LinearSegment2D) chainHead.getSucc();
            while (seg != null && seg != chainHead) {
                nodeDrawer.setNode(seg.getHead());
                nodeDrawer.drawModel(g2);
                seg = (LinearSegment2D) seg.getSucc();
            }
        }
    }

    private Path2D genContourPath() {
        Path2D path = new Path2D.Double();
        for (LinearSegment2D chainHead : trianglePolygonizer.getContourHeads()) {
            double[] headCoord = chainHead.getHeadCoord();
            path.moveTo(headCoord[0], headCoord[1]);
            LinearSegment2D seg = (LinearSegment2D) chainHead.getSucc();
            while (seg != null && seg != chainHead) {
                double[] segHeadCoord = seg.getHeadCoord();
                path.lineTo(segHeadCoord[0], segHeadCoord[1]);
                seg = (LinearSegment2D) seg.getSucc();
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
        for (TriangleContourCell cell : trianglePolygonizer.getCells()) {
            TriangleContourCellDemoDrawer cellDrawer =
                    new TriangleContourCellDemoDrawer(cell, trianglePolygonizer.getNodesValuesMap());
            triangleDrawers.add(cellDrawer);
            cellDrawer.setNodesVisible(triangleNodesVisible);
        }
    }

    public boolean isTriangleNodesVisible() {
        return triangleNodesVisible;
    }

    public void setTriangleNodesVisible(boolean triangleNodesVisible) {
        this.triangleNodesVisible = triangleNodesVisible;
        for (TriangleContourCellDemoDrawer cellDrawer : triangleDrawers) {
            cellDrawer.setNodesVisible(triangleNodesVisible);
        }
    }
}
