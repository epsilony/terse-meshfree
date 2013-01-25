/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import java.util.List;
import net.epsilony.tsmf.assemblier.LagrangeWFAssemblier;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.ConstantInfluenceRadCalc;
import net.epsilony.tsmf.model.influence.InfluenceRadsCalc;
import net.epsilony.tsmf.shape_func.MLS;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;

/**
 *
 * @author epsilon
 */
public class TimoshenkoStandardProject implements Project {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleProject rectProject;

    @Override
    public List<ProcessPoint> balance() {
        return rectProject.balance();
    }

    @Override
    public List<ProcessPoint> neumann() {
        return rectProject.neumann();
    }

    @Override
    public List<ProcessPoint> dirichlet() {
        return rectProject.dirichlet();
    }

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalytical() {
        return timoBeam;
    }

    public TimoshenkoStandardProject(TimoshenkoAnalyticalBeam2D timoBeam, double segLengthUpBnd, double quadDomainSize, int quadDegree) {

        this.timoBeam = timoBeam;
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectProject = new RectangleProject(left, down, right, up, segLengthUpBnd);
        rectProject.setSegmentQuadratureDegree(quadDegree);
        rectProject.setBalanceSpecification(null, quadDomainSize, quadDegree);
        rectProject.addBoundaryConditionOnEdge("r", timoBeam.new NeumannFunction(), null);
        rectProject.addBoundaryConditionOnEdge("l", timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }

    public ProcessPackage processPackage(double spaceNdsGap, double influenceRad) {
        Project project = this;
        Model2D model = rectProject.model(spaceNdsGap);
        ShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        WFAssemblier assemblier = new LagrangeWFAssemblier();
        InfluenceRadsCalc influenceRadsCalc = new ConstantInfluenceRadCalc(influenceRad);
        return new ProcessPackage(project, model, influenceRadsCalc, assemblier, shapeFunc, constitutiveLaw);
    }
}
