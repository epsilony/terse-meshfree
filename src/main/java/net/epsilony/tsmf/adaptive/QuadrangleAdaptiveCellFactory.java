/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive;

import net.epsilony.tsmf.model.Node;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleAdaptiveCellFactory {

    public static QuadrangleAdaptiveCell[][] byCoordGrid(double[] xs, double[] ys) {

        int numRow = ys.length - 1;
        int numCol = xs.length - 1;
        QuadrangleAdaptiveCell[][] result = new QuadrangleAdaptiveCell[numRow][numCol];
        Node[][] nodeGrid = xysToNodes(xs, ys);
        for (int rowIndex = 0; rowIndex < numRow; rowIndex++) {
            QuadrangleAdaptiveCell[] resultRow = result[rowIndex];
            for (int colIndex = 0; colIndex < resultRow.length; colIndex++) {
                resultRow[colIndex] = produce(new Node[]{
                            nodeGrid[rowIndex + 1][colIndex],
                            nodeGrid[rowIndex + 1][colIndex + 1],
                            nodeGrid[rowIndex][colIndex + 1],
                            nodeGrid[rowIndex][colIndex]
                        });
            }
        }
        linkCellGridOpposites(result);
        return result;
    }

    private static void linkCellGridOpposites(QuadrangleAdaptiveCell[][] cellGrid) {
        for (int i = 0; i < cellGrid.length - 1; i++) {
            QuadrangleAdaptiveCell[] gridRow = cellGrid[i];
            QuadrangleAdaptiveCell[] nextRow = cellGrid[i + 1];
            for (int j = 0; j < gridRow.length; j++) {
                gridRow[j].edges[0].addOpposite(0, nextRow[j].edges[2]);
                nextRow[j].edges[2].addOpposite(0, gridRow[j].edges[0]);
            }
        }
        for (int i = 0; i < cellGrid.length; i++) {
            QuadrangleAdaptiveCell[] gridRow = cellGrid[i];
            for (int j = 0; j < gridRow.length - 1; j++) {
                gridRow[j].edges[1].addOpposite(0, gridRow[j + 1].edges[3]);
                gridRow[j + 1].edges[3].addOpposite(0, gridRow[j].edges[1]);
            }
        }
    }

    private static Node[][] xysToNodes(double[] xs, double[] ys) {
        Node[][] result = new Node[ys.length][xs.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = new Node(xs[j], ys[i]);
            }
        }
        return result;
    }

    public static QuadrangleAdaptiveCell produce(Node[] counterClockwiseVetes) {
        if (counterClockwiseVetes.length != 4) {
            throw new IllegalArgumentException();
        }
        QuadrangleAdaptiveCell result = new QuadrangleAdaptiveCell();
        AdaptiveCellEdge[] edges = new AdaptiveCellEdge[QuadrangleAdaptiveCell.NUM_OF_EDGES];
        for (int i = 0; i < counterClockwiseVetes.length; i++) {
            edges[i] = new AdaptiveCellEdge(counterClockwiseVetes[i]);
        }
        result.setEdges(edges);
        return result;
    }
}
