/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.epsilony.tsmf.adaptive.AdaptiveCellEdge;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCell;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.ui.SingleModelShapeDrawer;

/**
 *
 * @author epsilon
 */
public class QuadrangleCellDemoDrawer extends SingleModelShapeDrawer {

    QuadrangleAdaptiveCell cell;
    private NodeDrawer nodeDrawer = new NodeDrawer(null);

    public QuadrangleCellDemoDrawer(QuadrangleAdaptiveCell cell) {
        _setQuadrangleAdaptiveCell(cell);
    }

    public void setCell(QuadrangleAdaptiveCell cell) {
        _setQuadrangleAdaptiveCell(cell);
    }

    protected final void _setQuadrangleAdaptiveCell(QuadrangleAdaptiveCell cell) {
        this.cell = cell;
        Shape shape = null;
        AdaptiveCellEdge[] edges = cell.getEdges();
        if (edges != null) {
            GeneralPath path = new GeneralPath();
            path.moveTo(edges[0].getHead().coord[0], edges[0].getHead().coord[1]);
            for (int i = 0; i < edges.length; i++) {
                path.lineTo(edges[i].getHead().coord[0], edges[i].getHead().coord[1]);
            }
            path.closePath();
            shape = path;
        }
        _setShape(shape);
    }

    public Color getNodeColor() {
        return nodeDrawer.getColor();
    }

    public void setNodeColor(Color color) {
        nodeDrawer.setColor(color);
    }

    @Override
    public void drawModel(Graphics2D g2) {
        super.drawModel(g2);
        if (cell.getEdges() == null) {
            return;
        }
        for (AdaptiveCellEdge edge : cell.getEdges()) {
            nodeDrawer.setNode(edge.getHead());
            nodeDrawer.drawModel(g2);
        }
    }

    @Override
    public void setComponent(Component component) {
        super.setComponent(component);
        nodeDrawer.setComponent(component);
    }

    @Override
    public void setModelToComponentTransform(AffineTransform modelToComponentTransform) {
        super.setModelToComponentTransform(modelToComponentTransform);
        nodeDrawer.setModelToComponentTransform(modelToComponentTransform);
    }

    public QuadrangleAdaptiveCell getCell() {
        return cell;
    }

    public boolean isComponentPointInside(int x, int y) throws NoninvertibleTransformException {
        if (cell == null) {
            return false;
        }
        AdaptiveCellEdge[] edges = cell.getEdges();
        if (edges == null) {
            return false;
        }
        Point2D pt;

        pt = modelToComponentTransform.inverseTransform(new Point2D.Double(x, y), null);

        for (AdaptiveCellEdge edge : edges) {
            double[] headCoord = edge.getHead().coord;
            double[] rearCoord = edge.getRear().coord;
            double cross = Math2D.cross(
                    rearCoord[0] - headCoord[0],
                    rearCoord[1] - headCoord[1],
                    pt.getX() - headCoord[0],
                    pt.getY() - headCoord[1]);
            if (cross <= 0) {
                return false;
            }
        }
        return true;
    }
}
