/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import net.epsilony.tb.adaptive.AdaptiveCellEdge;
import net.epsilony.tb.solid.Node;
import net.epsilony.mf.implicit.TriangleContourCell;
import net.epsilony.tb.solid.ui.NodeDrawer;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.ui.ModelDrawerAdapter;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourCellDemoDrawer extends ModelDrawerAdapter {

    public static Color DEFAULT_VISITIED_FILLING = null;
    public static Color DEFAULT_UNVISITIED_FILLING = Color.CYAN;
    public static Color DEFAULT_NODE_BELOW_LEVEL = Color.RED;
    public static Color DEFAULT_NODE_ABOVE_LEVEL = Color.GREEN;
    public static Color DEFAULT_SEGMENT_COLOR = Color.LIGHT_GRAY;
    public static Color DEFAULT_NODE_NULL_DATA_COLOR = Color.YELLOW;
    Color visitedFilling = DEFAULT_VISITIED_FILLING;
    Color unvisitedFilling = DEFAULT_UNVISITIED_FILLING;
    Color below = DEFAULT_NODE_BELOW_LEVEL;
    Color above = DEFAULT_NODE_ABOVE_LEVEL;
    Color segmentColor = DEFAULT_SEGMENT_COLOR;
    TriangleContourCell cell;
    double value;
    Path2D path;
    NodeDrawer nodeDrawer = new NodeDrawer();
    Color nodeDataNullColor = DEFAULT_NODE_NULL_DATA_COLOR;
    boolean nodesVisible;
    IntIdentityMap<Node, double[]> nodesValuesMap;

    public TriangleContourCellDemoDrawer(TriangleContourCell cell, IntIdentityMap<Node, double[]> nodesValuesMap) {
        this.cell = cell;
        this.nodesValuesMap = nodesValuesMap;
        genSegmentsPathInModelSpace();
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        return path.getBounds2D();
    }

    @Override
    public void setModelToComponentTransform(AffineTransform modelToComponentTransform) {
        super.setModelToComponentTransform(modelToComponentTransform);
        nodeDrawer.setModelToComponentTransform(modelToComponentTransform);
    }

    @Override
    public void setComponent(Component component) {
        super.setComponent(component);
        nodeDrawer.setComponent(component);
    }

    @Override
    public void drawModel(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(segmentColor);
        Shape pathOnScreen = modelToComponentTransform.createTransformedShape(path);
        g2.draw(pathOnScreen);

        Color fillingColor = cell.isVisited() ? visitedFilling : unvisitedFilling;
        if (null != fillingColor) {
            g2.setColor(fillingColor);
            g2.fill(pathOnScreen);
        }

        if (nodesVisible) {
            AdaptiveCellEdge[] edges = cell.getEdges();
            for (AdaptiveCellEdge edge : edges) {
                nodeDrawer.setNode(edge.getHead());
                Node node = edge.getHead();
                double[] data = nodesValuesMap.get(node);
                if (null == data) {
                    continue;
                } else if (data[0] < value) {
                    nodeDrawer.setColor(below);
                } else {
                    nodeDrawer.setColor(above);
                }
                nodeDrawer.drawModel(g2);
            }
        }
    }

    private void genSegmentsPathInModelSpace() {
        path = new Path2D.Double();
        AdaptiveCellEdge[] edges = cell.getEdges();
        double[] coord = edges[0].getHeadCoord();
        path.moveTo(coord[0], coord[1]);
        for (int i = 1; i < edges.length; i++) {
            coord = edges[i].getHeadCoord();
            path.lineTo(coord[0], coord[1]);
        }
        path.closePath();
    }

    public boolean isNodesVisible() {
        return nodesVisible;
    }

    public void setNodesVisible(boolean nodesVisible) {
        this.nodesVisible = nodesVisible;
    }
}