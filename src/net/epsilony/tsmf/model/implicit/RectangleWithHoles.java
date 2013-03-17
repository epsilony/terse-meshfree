/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.ArrvarFunction;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.ui.UIUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHoles implements ArrvarFunction, GenericFunction<double[], double[]> {

    Rectangle2D rectangle;
    Polygon2D rectanglePolygon;
    double holeRadius, holeDistance;
    int numOfHoleRows;
    int numOfHoleCols;
    List<Circle> holes;

    public RectangleWithHoles(Rectangle2D rectangle, double holeRadius, double holeDistance) {
        this.rectangle = UIUtils.tidyRectangle2D(rectangle, null);
        this.holeRadius = holeRadius;
        this.holeDistance = holeDistance;
        double rectSize = Math.min(rectangle.getHeight(), rectangle.getWidth());
        if (rectSize < (holeRadius + holeDistance / 2) * 2) {
            throw new IllegalArgumentException("hole radius or hold distance is too large ("
                    + rectangle + ", holeRadius" + holeRadius + ", holeDistance)");
        }
        genNumOfHoleRows();
        genNumOfHoleCols();
        genHoles();
        genRectanglePolygon();
    }

    private void genNumOfHoleRows() {
        double d = (holeRadius + holeDistance / 2) * 2;
        numOfHoleRows = (int) Math.floor((rectangle.getHeight() / d - 1) * 2 / Math.sqrt(3) + 1);
    }

    private void genNumOfHoleCols() {
        numOfHoleCols = (int) Math.floor(rectangle.getWidth() / (holeDistance / 2 + holeRadius) / 2);
    }

    private void genHoles() {
        final double r = holeRadius + holeDistance / 2;
        final double d = 2 * r;
        final double x0 = rectangle.getX() + r + (rectangle.getWidth() - d * numOfHoleCols) / 2;
        final double y0 = rectangle.getY() + r
                + (rectangle.getHeight() - d * ((numOfHoleRows - 1) * Math.cos(Math.PI / 6) + 1)) / 2;
        final double deltaY = d * Math.cos(Math.PI / 6);
        final double deltaX = d;
        holes = new LinkedList<>();
        for (int i = 0; i < numOfHoleRows; i++) {
            double holeCenterY = y0 + i * deltaY;
            for (int j = 0; j < numOfHoleCols; j++) {
                if (j == numOfHoleCols - 1 && i % 2 != 0) {
                    break;
                }
                double holeCenterX = x0 + deltaX * j;
                if (i % 2 != 0) {
                    holeCenterX += r;
                }
                holes.add(new Circle(holeCenterX, holeCenterY, holeRadius));

            }
        }
    }

    private void genRectanglePolygon() {
        List<Segment2D> chainsHeads = UIUtils.pathIteratorToSegment2DChains(rectangle.getPathIterator(null));
        rectanglePolygon = new Polygon2D();
        rectanglePolygon.setChainsHeads(chainsHeads);
    }

    @Override
    public double value(double[] vec) {
        double result = rectanglePolygon.distanceFunc(vec[0], vec[1]);
        for (Circle circle : holes) {
            double value = -circle.value(vec);
            if (Math.abs(value) < Math.abs(result)) {
                result = value;
            }
        }
        return result;
    }

    public Shape genShape() {
        Area area = new Area(rectangle);
        for (Circle cir : holes) {
            area.subtract(new Area(cir.genShape()));
        }
        return area;
    }

    @Override
    public double[] value(double[] input, double[] output) {
        if (null == output) {
            output = new double[]{value(input)};
        } else {
            output[0] = value(input);
        }
        return output;
    }
}
