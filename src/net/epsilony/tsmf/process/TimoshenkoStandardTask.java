/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import java.util.List;
import net.epsilony.tsmf.assemblier.LagrangeWFAssemblier;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.tsmf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tsmf.shape_func.MLS;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoStandardTask implements WeakformTask {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleTask rectProject;

    @Override
    public List<TaskUnit> balance() {
        return rectProject.balance();
    }

    @Override
    public List<TaskUnit> neumann() {
        return rectProject.neumann();
    }

    @Override
    public List<TaskUnit> dirichlet() {
        return rectProject.dirichlet();
    }

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalytical() {
        return timoBeam;
    }

    public TimoshenkoStandardTask(TimoshenkoAnalyticalBeam2D timoBeam, double segLengthUpBnd, double quadDomainSize, int quadDegree) {

        this.timoBeam = timoBeam;
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectProject = new RectangleTask(left, down, right, up, segLengthUpBnd);
        rectProject.setSegmentQuadratureDegree(quadDegree);
        rectProject.setBalanceSpecification(null, quadDomainSize, quadDegree);
        rectProject.addBoundaryConditionOnEdge("r", timoBeam.new NeumannFunction(), null);
        rectProject.addBoundaryConditionOnEdge("l", timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }

    public WeakformProject processPackage(double spaceNdsGap, double influenceRad) {
        WeakformTask project = this;
        Model2D model = rectProject.model(spaceNdsGap);
        ShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        WFAssemblier assemblier = new LagrangeWFAssemblier();
        InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(influenceRad);
        return new WeakformProject(project, model, influenceRadsCalc, assemblier, shapeFunc, constitutiveLaw);
    }
}
