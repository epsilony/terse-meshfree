/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.LinkedList;
import net.epsilony.mf.model.Model2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.quadrature.QuadrangleQuadrature;
import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleTask extends PolygonTask2D {

    double left;
    double down;
    double right;
    double up;

    public RectangleTask(double left, double down, double right, double up, double segLengthUpBnd) {
        if (left >= right) {
            throw new IllegalArgumentException(String.format("left (%f) should be less then right (%f)", left, right));
        }
        if (down >= up) {
            throw new IllegalArgumentException(String.format("down (%f) should be less then up (%f)", down, up));
        }
        this.left = left;
        this.down = down;
        this.right = right;
        this.up = up;
        Polygon2D poly = Polygon2D.byCoordChains(
                new double[][][]{{{left, down}, {right, down}, {right, up}, {left, up}}});
        poly = poly.fractionize(segLengthUpBnd);
        initPolygonProject2D(poly);
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            double quadDomainSizeUpBnd,
            int quadratureDegree) {
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setDegree(quadratureDegree);
        LinkedList<QuadraturePoint> qPoints = new LinkedList<>();
        double width = getWidth();
        double height = getHeight();
        int numHor = (int) Math.ceil(width / quadDomainSizeUpBnd);
        double dWidth = width / numHor;
        int numVer = (int) Math.ceil(height / quadDomainSizeUpBnd);
        double dHeight = height / numVer;
        double x0 = left;
        double y0 = down;
        for (int i = 0; i < numVer; i++) {
            double d = y0 + dHeight * i;
            double u = d + dHeight;
            for (int j = 0; j < numHor; j++) {
                double l = x0 + dWidth * j;
                double r = l + dWidth;
                qQuad.setQuadrangle(l, d, r, d, r, u, l, u);
                for (QuadraturePoint qP : qQuad) {
                    qPoints.add(qP);
                }
            }
        }
        setVolumeSpecification(volumnForceFunc, qPoints);
    }

    public void addBoundaryConditionOnEdge(
            String edge,
            GenericFunction<double[], double[]> value,
            GenericFunction<double[], boolean[]> diriMark) {
        edge = edge.toLowerCase();
        double l, d, r, u;
        double t = polygon.getMinSegmentLength() / 3;
        switch (edge) {
            case "l":
            case "left":
                l = left - t;
                d = down;
                r = left + t;
                u = up;
                break;
            case "d":
            case "down":
                l = left;
                r = right;
                d = down - t;
                u = up + t;
                break;
            case "r":
            case "right":
                l = right - t;
                r = right + t;
                d = down;
                u = up;
                break;
            case "u":
            case "up":
                l = left;
                r = right;
                d = up - t;
                u = up + t;
                break;
            default:
                throw new IllegalArgumentException("The edge should only be one of \n"
                        + "[ \"lelf\", \"l\", \"d\", \"down\", \"r\", \"right\", \"u\", \"up\" ]\n"
                        + "but get :" + edge);
        }
        double[] from = new double[]{l, d};
        double[] to = new double[]{r, u};
        if (diriMark != null) {
            addDirichletBoundaryCondition(from, to, value, diriMark);
        } else {
            addNeumannBoundaryCondition(from, to, value);
        }
    }

    public double getWidth() {
        return right - left;
    }

    public double getHeight() {
        return up - down;
    }

    public Model2D model(double spaceNodesDistance) {
        double w = getWidth();
        double h = getHeight();
        int numCol = (int) Math.ceil(w / spaceNodesDistance) - 1;
        int numRow = (int) Math.ceil(h / spaceNodesDistance) - 1;
        double dw = w / (numCol + 1);
        double dh = h / (numRow + 1);
        double x0 = dw;
        double y0 = h * -0.5 + dh;
        ArrayList<Node> spaceNodes = new ArrayList<>(numCol * numRow);
        for (int i = 0; i < numRow; i++) {
            double y = y0 + dw * i;
            for (int j = 0; j < numCol; j++) {
                double x = x0 + dh * j;
                spaceNodes.add(new Node(x, y));
            }
        }
        return new Model2D(polygon, spaceNodes);
    }
}
