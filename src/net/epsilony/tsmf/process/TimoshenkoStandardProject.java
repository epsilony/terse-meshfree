/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.assemblier.LagrangeWFAssemblier;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.influence.ConstantInfluenceRadCalc;
import net.epsilony.tsmf.model.influence.InfluenceRadsCalc;
import net.epsilony.tsmf.shape_func.MLS;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tsmf.util.quadrature.QuadrangleQuadrature;
import net.epsilony.tsmf.util.quadrature.QuadraturePoint;

/**
 *
 * @author epsilon
 */
public class TimoshenkoStandardProject implements Project {

    PolygonProject2D polyProject;
    TimoshenkoAnalyticalBeam2D timoBeam;
    double segLen;

    @Override
    public List<ProcessPoint> balance() {
        return polyProject.balance();
    }

    @Override
    public List<ProcessPoint> neumann() {
        return polyProject.neumann();
    }

    @Override
    public List<ProcessPoint> dirichlet() {
        return polyProject.dirichlet();
    }

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalytical() {
        return timoBeam;
    }

    public TimoshenkoStandardProject(TimoshenkoAnalyticalBeam2D timoBeam, double segLen, double quadDomainSize, int quadDegree) {
        this.timoBeam = timoBeam;
        this.segLen = segLen;
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double[][][] vertes = new double[][][]{
            {{0, -h / 2}, {w, -h / 2}, {w, h / 2}, {0, h / 2}}
        };

        Polygon2D poly = Polygon2D.byCoordChains(vertes);
        poly = poly.fractionize(segLen);

        polyProject = new PolygonProject2D(poly, quadDegree);

        genBalance(quadDegree, quadDomainSize);

        double t = segLen / 4;
        double[] from = new double[]{vertes[0][0][0] - t, vertes[0][0][1] - t};
        double[] to = new double[]{vertes[0][3][0] + t, vertes[0][3][1] + t};

        polyProject.addNeumannBoundaryCondition(from, to, timoBeam.new NeumannFunction());
        from = new double[]{vertes[0][1][0] - t, vertes[0][1][1] - t};
        to = new double[]{vertes[0][2][0] + t, vertes[0][2][1] + t};
        polyProject.addDirichletBoundaryCondition(from, to, timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
    }

    private void genBalance(int quadDegree, double quadDomainSize) {
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setDegree(quadDegree);
        LinkedList<QuadraturePoint> qPoints = new LinkedList<>();
        double width = timoBeam.getWidth();
        double height = timoBeam.getHeight();
        int numHor = (int) Math.ceil(width / quadDomainSize);
        double dWidth = width / numHor;
        int numVer = (int) Math.ceil(height / quadDomainSize);
        double dHeight = height / numVer;
        double x0 = 0;
        double y0 = -height / 2;
        for (int i = 0; i < numVer; i++) {
            for (int j = 0; j < numHor; j++) {
                double left = x0 + dWidth * i;
                double down = y0 + dHeight * j;
                double right = left + dWidth;
                double up = down + dHeight;
                qQuad.setQuadrangle(left, down, right, down, right, up, left, up);
                for (QuadraturePoint qP : qQuad) {
                    qPoints.add(qP);
                }
            }
        }
        polyProject.setBalanceSpecification(null, qPoints);
    }

    public Model2D model(double spaceNodesDistance) {
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
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
        return new Model2D(polyProject.polygon, spaceNodes);
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }

    public ProcessPackage processPackage(double spaceNdsGap, double influenceRad) {
        Project project = this;
        Model2D model = model(spaceNdsGap);
        ShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        WFAssemblier assemblier = new LagrangeWFAssemblier(constitutiveLaw, model.getAllNodes().size(), false);
        InfluenceRadsCalc influenceRadsCalc = new ConstantInfluenceRadCalc(influenceRad);
        return new ProcessPackage(project, model, influenceRadsCalc, assemblier, shapeFunc);
    }
}
