/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.search.SegmentsMidPointLRTreeRangeSearcher;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.quadrature.QuadraturePoint;
import net.epsilony.tb.quadrature.Segment2DQuadrature;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PolygonTask2D implements WeakformQuadratureTask {

    Polygon2D polygon;
    SegmentsMidPointLRTreeRangeSearcher polygonSegmentsRangeSearcher;
    List<BCSpecification> neumannBCs;
    List<BCSpecification> dirichletBCs;
    Collection<? extends QuadraturePoint> volumeQuadraturePoints;
    GenericFunction<double[], double[]> volumeForceFunc;
    int segQuadDegree;

    public PolygonTask2D(Polygon2D polygon) {
        initPolygonProject2D(polygon);
    }

    protected PolygonTask2D() {
    }

    final protected void initPolygonProject2D(Polygon2D polygon) {
        this.polygon = polygon;
        polygonSegmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(polygon);
        neumannBCs = new LinkedList<>();
        dirichletBCs = new LinkedList<>();
    }

    public void setSegmentQuadratureDegree(int segQuadDegree) {
        this.segQuadDegree = segQuadDegree;
    }

    public void setVolumeSpecification(
            GenericFunction<double[], double[]> volumnForceFunc,
            Collection<? extends QuadraturePoint> quadraturePoints) {
        this.volumeForceFunc = volumnForceFunc;
        volumeQuadraturePoints = quadraturePoints;
    }

    @Override
    public List<WeakformQuadraturePoint> volumeTasks() {
        LinkedList<WeakformQuadraturePoint> res = new LinkedList<>();
        for (QuadraturePoint qp : volumeQuadraturePoints) {
            double[] volForce = volumeForceFunc == null ? null : volumeForceFunc.value(qp.coord, null);
            res.add(new WeakformQuadraturePoint(qp, volForce, null));
        }
        return res;
    }

    @Override
    public List<WeakformQuadraturePoint> neumannTasks() {
        LinkedList<WeakformQuadraturePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature(segQuadDegree);
        for (BCSpecification spec : neumannBCs) {
            List<Segment2D> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment2D seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    res.add(new WeakformQuadraturePoint(qp, value, null));
                }
            }
        }
        return res;
    }

    @Override
    public List<WeakformQuadraturePoint> dirichletTasks() {
        LinkedList<WeakformQuadraturePoint> res = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature(segQuadDegree);
        for (BCSpecification spec : dirichletBCs) {
            List<Segment2D> segs = polygonSegmentsRangeSearcher.rangeSearch(spec.from, spec.to);
            for (Segment2D seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                GenericFunction<double[], boolean[]> markFunc = spec.markFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    boolean[] mark = markFunc.value(qp.coord, null);
                    res.add(new WeakformQuadraturePoint(qp, value, mark));
                }
            }
        }
        return res;
    }

    public void addNeumannBoundaryCondition(BCSpecification spec) {
        neumannBCs.add(spec);
    }

    public void addNeumannBoundaryCondition(
            double[] from, double[] to,
            GenericFunction<double[], double[]> valueFunc) {
        addNeumannBoundaryCondition(new BCSpecification(from, to, valueFunc, null));
    }

    public void addDirichletBoundaryCondition(
            double[] from, double[] to,
            GenericFunction<double[], double[]> valueFunc,
            GenericFunction<double[], boolean[]> markFunc) {
        addDirichletBoundaryCondition(new BCSpecification(from, to, valueFunc, markFunc));
    }

    public void addDirichletBoundaryCondition(BCSpecification spec) {
        dirichletBCs.add(spec);
    }

    public static class BCSpecification {

        double[] from, to;
        GenericFunction<double[], double[]> valueFunc;
        GenericFunction<double[], boolean[]> markFunc;

        public BCSpecification(
                double[] from, double[] to,
                GenericFunction<double[], double[]> valueFunc,
                GenericFunction<double[], boolean[]> markFunc) {
            this.from = from;
            this.to = to;
            this.valueFunc = valueFunc;
            this.markFunc = markFunc;
        }
    }
}
