/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.mf.model.Model2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.tb.solid.SegmentChainsIterator;
import net.epsilony.mf.process.WeakformQuadraturePoint;
import net.epsilony.mf.process.WeakformQuadratureTask;
import net.epsilony.tb.ArrvarFunction;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadrature;
import net.epsilony.tb.quadrature.SymTriangleQuadrature;
import net.epsilony.tb.ui.UIUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHoles implements ArrvarFunction, GenericFunction<double[], double[]>, NeedPreparation {

    public static double DEFAULT_MODEL_NODES_EXTENTION = 10;
    public static double DEFAULT_QUADRATURE_DOMAIN_SIZE = 10;
    public static double DEFAULT_SEGMENT_SIZE = 10;
    public static int DEFAULT_QUADRATURE_POWER = 2;
    Rectangle2D rectangle;
    Polygon2D rectanglePolygon;
    double holeRadius, holeDistance;
    int numOfHoleRows;
    int numOfHoleCols;
    List<Circle> holes;
    double spacesNodesExtension = DEFAULT_MODEL_NODES_EXTENTION;
    double triangleSize = DEFAULT_QUADRATURE_DOMAIN_SIZE;
    double segmentSize = DEFAULT_SEGMENT_SIZE;
    List<TriangleContourCell> triangles;
    List<Node> spaceNodes;
    List<QuadraturePoint> volumeQuadraturePoints;
    List<QuadraturePoint> boundaryQuadraturePoints;
    List<Segment2D> chainsHeads;
    int quadraturePower = DEFAULT_QUADRATURE_POWER;

    public RectangleWithHoles(Rectangle2D rectangle, double holeRadius, double holeDistance) {
        this.rectangle = UIUtils.tidyRectangle2D(rectangle, null);
        this.holeRadius = holeRadius;
        this.holeDistance = holeDistance;
        double rectSize = Math.min(rectangle.getHeight(), rectangle.getWidth());
        if (rectSize < (holeRadius + holeDistance / 2) * 2) {
            throw new IllegalArgumentException("hole radius or hold distance is too large ("
                    + rectangle + ", holeRadius" + holeRadius + ", holeDistance)");
        }
        genHoles();
        genRectanglePolygon();
    }

    public void setSegmentSize(double segmentSize) {
        this.segmentSize = segmentSize;
    }

    public void setQuadraturePower(int quadraturePower) {
        this.quadraturePower = quadraturePower;
    }

    public void setSpaceNodesExtension(double spaceNodesExtension) {
        this.spacesNodesExtension = spaceNodesExtension;
    }

    public void setTriangleSize(double triangleSize) {
        if (triangleSize <= 0) {
            throw new IllegalArgumentException("quadrature domain size should be positive");
        }
        this.triangleSize = triangleSize;
    }

    @Override
    public void prepare() {
        genTriangleContourCells();
        genSpaceNodes();
        genVolumeQuadraturePoints();
        genBoundaryQuadraturePoints();
    }

    public Model2D getModel() {
        return new Model2D(null, spaceNodes);
    }

    public WeakformQuadratureTask getWeakformQuadratureTask() {
        return new ZeroLevelTask();
    }

    private void genNumOfHoleRows() {
        double d = (holeRadius + holeDistance / 2) * 2;
        numOfHoleRows = (int) Math.floor((rectangle.getHeight() / d - 1) * 2 / Math.sqrt(3) + 1);
    }

    private void genNumOfHoleCols() {
        numOfHoleCols = (int) Math.floor(rectangle.getWidth() / (holeDistance / 2 + holeRadius) / 2);
    }

    private void genHoles() {
        genNumOfHoleRows();
        genNumOfHoleCols();
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
                Circle circle = new Circle(holeCenterX, holeCenterY, holeRadius);
                circle.setConcrete(false);
                holes.add(circle);
            }
        }
    }

    private void genRectanglePolygon() {
        List<LinearSegment2D> polygonChainsHeads =
                UIUtils.pathIteratorToSegment2DChains(rectangle.getPathIterator(null));
        rectanglePolygon = new Polygon2D();
        rectanglePolygon.setChainsHeads(polygonChainsHeads);
    }

    @Override
    public double value(double[] vec) {
        double result = rectanglePolygon.distanceFunc(vec[0], vec[1]);
        for (Circle circle : holes) {
            double value = circle.value(vec);
            if (Math.abs(value) < Math.abs(result)) {
                result = value;
            }
        }
        return result;
    }

    public Shape genShape() {
        Area area = new Area(rectangle);
        for (Circle cir : holes) {
            area.subtract(new Area(cir.genProfile()));
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

    public void genSegmentChains() {
        chainsHeads = new LinkedList<>();
        Polygon2D rectFraction = rectanglePolygon.fractionize(segmentSize);
        chainsHeads.addAll(rectFraction.getChainsHeads());
        for (Circle cir : holes) {
            chainsHeads.add(cir.toArcs(segmentSize));
        }
    }

    private void genSpaceNodes() {
        spaceNodes = new LinkedList<>();
        for (TriangleContourCell cell : triangles) {
            for (Segment2D seg : cell.getEdges()) {
                Node node = seg.getHead();
                if (node.getId() <= IntIdentityMap.NULL_INDEX_SUPREMUM) {
                    spaceNodes.add(node);
                    node.setId(Integer.MAX_VALUE);
                }
            }
        }
        for (Node nd : spaceNodes) {
            nd.setId(Integer.MIN_VALUE);
        }
    }

    private void genVolumeQuadraturePoints() {
        volumeQuadraturePoints = new LinkedList<>();
        SymTriangleQuadrature symTriangleQuadrature = new SymTriangleQuadrature();
        symTriangleQuadrature.setPower(quadraturePower);
        for (TriangleContourCell cell : triangles) {
            symTriangleQuadrature.setTriangle(
                    cell.getNode(0).getCoord()[0], cell.getNode(0).getCoord()[1],
                    cell.getNode(1).getCoord()[0], cell.getNode(1).getCoord()[1],
                    cell.getNode(2).getCoord()[0], cell.getNode(2).getCoord()[1]);
            for (QuadraturePoint qp : symTriangleQuadrature) {
                volumeQuadraturePoints.add(qp);
            }
        }
    }

    private void genBoundaryQuadraturePoints() {
        genSegmentChains();
        SegmentChainsIterator<Segment2D> iterator = new SegmentChainsIterator<>(chainsHeads);
        Segment2DQuadrature segment2DQuadrature = new Segment2DQuadrature(quadraturePower);
        boundaryQuadraturePoints = new LinkedList<>();
        while (iterator.hasNext()) {
            Segment2D segment = iterator.next();
            segment2DQuadrature.setSegment(segment);
            for (QuadraturePoint qp : segment2DQuadrature) {
                boundaryQuadraturePoints.add(qp);
            }
        }
    }

    private void genTriangleContourCells() {
        Rectangle2D nodesBounds = genNodesBounds();
        TriangleContourCellFactory factory = new TriangleContourCellFactory();
        TriangleContourCell[][] coverRectangle = factory.coverRectangle(nodesBounds, triangleSize);
        triangles = new LinkedList<>();
        MiscellaneousUtils.addToList(coverRectangle, triangles);
    }

    private Rectangle2D genNodesBounds() {
        Rectangle2D nodesBounds = new Rectangle2D.Double(
                rectangle.getX() - spacesNodesExtension,
                rectangle.getY() - spacesNodesExtension,
                rectangle.getWidth() + 2 * spacesNodesExtension,
                rectangle.getHeight() + 2 * spacesNodesExtension);
        return nodesBounds;
    }

    public double getTriangleSize() {
        return triangleSize;
    }

    public double getSegmentSize() {
        return segmentSize;
    }

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public List<Circle> getHoles() {
        return holes;
    }

    public List<QuadraturePoint> getBoundaryQuadraturePoints() {
        return boundaryQuadraturePoints;
    }

    public List<TriangleContourCell> getTriangles() {
        return triangles;
    }

    class ZeroLevelTask implements WeakformQuadratureTask {

        @Override
        public List<WeakformQuadraturePoint> volumeTasks() {
            List<WeakformQuadraturePoint> result = new LinkedList<>();
            for (QuadraturePoint qp : volumeQuadraturePoints) {
                WeakformQuadraturePoint taskPoint =
                        new WeakformQuadraturePoint(qp, new double[]{value(qp.coord)}, null);
                result.add(taskPoint);
            }
            return result;
        }

        @Override
        public List<WeakformQuadraturePoint> neumannTasks() {
            return null;
        }

        @Override
        public List<WeakformQuadraturePoint> dirichletTasks() {
            List<WeakformQuadraturePoint> result = new LinkedList<>();
            double[] value = new double[]{0};
            boolean[] validity = new boolean[]{true};
            for (QuadraturePoint qp : boundaryQuadraturePoints) {
                WeakformQuadraturePoint taskPoint = new WeakformQuadraturePoint(qp, value, validity);
                result.add(taskPoint);
            }
            return result;
        }
    }
}
