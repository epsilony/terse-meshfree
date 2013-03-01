/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import java.awt.geom.Rectangle2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.MiscellaneousUtils;

/**
 *
 * @author epsilon
 */
public class TriangleAdaptiveCellFactory {

    public static TriangleAdaptiveCell[][] coverRectangle(Rectangle2D rectangle, double edgeLength) {
        rectangle = MiscellaneousUtils.tidy(rectangle, null);
        final double SQRT_3 = Math.sqrt(3);
        double x0 = rectangle.getX() - edgeLength / 4;
        double y0 = rectangle.getY() - edgeLength * SQRT_3 / 4;
        double t = (rectangle.getWidth() - edgeLength / 4) / edgeLength;
        int numPointCols = (int) Math.ceil(t) + 2;
        if (Math.ceil(t) == t) {
            numPointCols++;
        }
        t = (rectangle.getHeight() - SQRT_3 * edgeLength / 4) / (SQRT_3 / 2 * edgeLength);
        int numPointRows = (int) Math.ceil(t) + 2;
        if (Math.ceil(t) == t) {
            numPointRows++;
        }
        Node[][] nodes = genVertes(x0, y0, numPointRows, numPointCols, edgeLength);
        TriangleAdaptiveCell[][] triangles = genTriangles(nodes);
        linkTrianglesOpposites(triangles);
        return triangles;
    }

    private static Node[][] genVertes(double x0, double y0, int numRows, int numCols, double edgeLength) {
        final double SQRT_3 = Math.sqrt(3);
        Node[][] result = new Node[numRows][numCols];
        for (int i = 0; i < result.length; i++) {
            Node[] row = result[i];
            double startX = x0 - 0.5 * edgeLength * (i % 2);
            double y = y0 + i * edgeLength * SQRT_3 / 2;
            for (int j = 0; j < row.length; j++) {
                row[j] = new Node(startX + edgeLength * j, y);
            }
        }
        return result;
    }

    private static TriangleAdaptiveCell[][] genTriangles(Node[][] nodes) {
        TriangleAdaptiveCell[][] triangleCells = new TriangleAdaptiveCell[nodes.length - 1][(nodes[0].length - 1) * 2];
        for (int i = 0; i < nodes.length - 1; i++) {
            int rowMod = i % 2;
            TriangleAdaptiveCell[] triangleRow = triangleCells[i];
            for (int j = 0; j < triangleRow.length; j++) {
                if (rowMod == 0 && j % 2 == 0 || rowMod == 1 && j % 2 == 1) {
                    AdaptiveCellEdge[] edges = new AdaptiveCellEdge[]{
                        new AdaptiveCellEdge(nodes[i][j / 2 + rowMod]),
                        new AdaptiveCellEdge(nodes[i + 1][j / 2 + 1]),
                        new AdaptiveCellEdge(nodes[i + 1][j / 2])};
                    TriangleAdaptiveCell cell = new TriangleAdaptiveCell();
                    cell.setEdges(edges);
                    triangleRow[j] = cell;
                } else {
                    AdaptiveCellEdge[] edges = new AdaptiveCellEdge[]{
                        new AdaptiveCellEdge(nodes[i + 1][j / 2 + rowMod * -1 + 1]),
                        new AdaptiveCellEdge(nodes[i][j / 2]),
                        new AdaptiveCellEdge(nodes[i][j / 2 + 1])
                    };
                    TriangleAdaptiveCell cell = new TriangleAdaptiveCell();
                    cell.setEdges(edges);
                    triangleRow[j] = cell;
                }
            }
        }
        return triangleCells;
    }

    private static void linkTrianglesOpposites(TriangleAdaptiveCell[][] triangles) {
        for (int i = 0; i < triangles.length; i++) {
            final int rowMod = i % 2;
            for (int j = 0; j < triangles[i].length - 1; j++) {
                if (j % 2 == 0 && rowMod == 0 || j % 2 == 1 && rowMod == 1) {
                    AdaptiveUtils.link(triangles[i][j].edges[0], triangles[i][j + 1].edges[0]);
                } else {
                    AdaptiveUtils.link(triangles[i][j].edges[2], triangles[i][j + 1].edges[2]);
                }
            }
        }
        for (int i = 0; i < triangles.length - 1; i++) {
            final int startJ = i % 2;
            for (int j = 0; j < triangles[i].length; j += 2) {
                AdaptiveUtils.link(triangles[i][j + startJ].edges[1], triangles[i + 1][j + startJ].edges[1]);
            }
        }
    }
}
