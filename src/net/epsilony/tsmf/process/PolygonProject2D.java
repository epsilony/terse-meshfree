/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.quadrature.QuadraturePoint;
import net.epsilony.tsmf.util.quadrature.Segment2DQuadrature;

/**
 *
 * @author epsilon
 */
public class PolygonProject2D implements Project {

    Polygon2D polygon;
    List<BCSpecification> neumannBCs;
    List<BCSpecification> dirichletBCs;
    Collection<? extends QuadraturePoint> balanceQuadraturePoints;
    GenericFunction<double[], double[]> volumnForceFunc;
    int segQuadDegree;

    public PolygonProject2D(Polygon2D polygon) {
        initPolygonProject2D(polygon);
    }

    protected PolygonProject2D() {
    }

    final protected void initPolygonProject2D(Polygon2D polygon) {
        this.polygon = polygon;
        neumannBCs = new LinkedList<>();
        dirichletBCs = new LinkedList<>();
    }

    public void setSegmentQuadratureDegree(int segQuadDegree) {
        this.segQuadDegree = segQuadDegree;
    }

    public void setBalanceSpecification(GenericFunction<double[], double[]> volumnForceFunc, Collection<? extends QuadraturePoint> quadraturePoints) {
        this.volumnForceFunc = volumnForceFunc;
        balanceQuadraturePoints = quadraturePoints;
    }

    @Override
    public List<ProcessPoint> balance() {
        LinkedList<ProcessPoint> res = new LinkedList<>();
        for (QuadraturePoint qp : balanceQuadraturePoints) {
            double[] volForce = volumnForceFunc == null ? null : volumnForceFunc.value(qp.coord, null);
            res.add(new ProcessPoint(qp.weight, qp.coord, null, volForce, null));
        }
        return res;
    }

    @Override
    public List<ProcessPoint> neumann() {
        LinkedList<ProcessPoint> res = new LinkedList<>();
        LinkedList<Segment2D> segs = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature(segQuadDegree);
        for (BCSpecification spec : neumannBCs) {
            polygon.rangeSearch(segs, spec.from, spec.to);
            for (Segment2D seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    res.add(new ProcessPoint(qp.weight, qp.coord, seg, value, null));
                }
            }
        }
        return res;
    }

    @Override
    public List<ProcessPoint> dirichlet() {
        LinkedList<ProcessPoint> res = new LinkedList<>();
        LinkedList<Segment2D> segs = new LinkedList<>();
        Segment2DQuadrature segQuad = new Segment2DQuadrature(segQuadDegree);
        for (BCSpecification spec : dirichletBCs) {
            polygon.rangeSearch(segs, spec.from, spec.to);
            for (Segment2D seg : segs) {
                GenericFunction<double[], double[]> func = spec.valueFunc;
                GenericFunction<double[], boolean[]> markFunc = spec.markFunc;
                segQuad.setSegment(seg);
                for (QuadraturePoint qp : segQuad) {
                    double[] value = func.value(qp.coord, null);
                    boolean[] mark = markFunc.value(qp.coord, null);
                    res.add(new ProcessPoint(qp.weight, qp.coord, seg, value, mark));
                }
            }
        }
        return res;
    }

    public void addNeumannBoundaryCondition(BCSpecification spec) {
        neumannBCs.add(spec);
    }

    public void addNeumannBoundaryCondition(double[] from, double[] to, GenericFunction<double[], double[]> valueFunc) {
        addNeumannBoundaryCondition(new BCSpecification(from, to, valueFunc, null));
    }

    public void addDirichletBoundaryCondition(double[] from, double[] to, GenericFunction<double[], double[]> valueFunc, GenericFunction<double[], boolean[]> markFunc) {
        addDirichletBoundaryCondition(new BCSpecification(from, to, valueFunc, markFunc));
    }

    public void addDirichletBoundaryCondition(BCSpecification spec) {
        dirichletBCs.add(spec);
    }

    public static class BCSpecification {

        double[] from, to;
        GenericFunction<double[], double[]> valueFunc;
        GenericFunction<double[], boolean[]> markFunc;

        public BCSpecification(double[] from, double[] to, GenericFunction<double[], double[]> valueFunc, GenericFunction<double[], boolean[]> markFunc) {
            this.from = from;
            this.to = to;
            this.valueFunc = valueFunc;
            this.markFunc = markFunc;
        }
    }
}
