/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import java.util.List;
import net.epsilony.tsmf.process.assemblier.MechanicalLagrangeWeakformAssemblier;
import net.epsilony.tsmf.process.assemblier.WeakformAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.tsmf.model.influence.EnsureNodesNum;
import net.epsilony.tsmf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tsmf.shape_func.MLS;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TimoshenkoStandardTask implements WeakformQuadratureTask {

    TimoshenkoAnalyticalBeam2D timoBeam;
    RectangleTask rectProject;

    @Override
    public List<WeakformQuadraturePoint> volumeTasks() {
        return rectProject.volumeTasks();
    }

    @Override
    public List<WeakformQuadraturePoint> neumannTasks() {
        return rectProject.neumannTasks();
    }

    @Override
    public List<WeakformQuadraturePoint> dirichletTasks() {
        return rectProject.dirichletTasks();
    }

    public TimoshenkoAnalyticalBeam2D getTimoshenkoAnalytical() {
        return timoBeam;
    }

    public TimoshenkoStandardTask(
            TimoshenkoAnalyticalBeam2D timoBeam,
            double segLengthUpBnd,
            double quadDomainSize,
            int quadDegree) {

        this.timoBeam = timoBeam;
        double w = timoBeam.getWidth();
        double h = timoBeam.getHeight();
        double left = 0;
        double down = -h / 2;
        double right = w;
        double up = h / 2;
        rectProject = new RectangleTask(left, down, right, up, segLengthUpBnd);
        rectProject.setSegmentQuadratureDegree(quadDegree);
        rectProject.setVolumeSpecification(null, quadDomainSize, quadDegree);
        rectProject.addBoundaryConditionOnEdge("r", timoBeam.new NeumannFunction(), null);
        rectProject.addBoundaryConditionOnEdge("l", timoBeam.new DirichletFunction(), timoBeam.new DirichletMarker());
    }

    public ConstitutiveLaw constitutiveLaw() {
        return timoBeam.constitutiveLaw();
    }

    public SimpleWeakformProject processPackage(double spaceNdsGap, double influenceRad) {
        WeakformQuadratureTask project = this;
        Model2D model = rectProject.model(spaceNdsGap);
        ShapeFunction shapeFunc = new MLS();
        ConstitutiveLaw constitutiveLaw = timoBeam.constitutiveLaw();
        WeakformAssemblier assemblier = new MechanicalLagrangeWeakformAssemblier();
        InfluenceRadiusCalculator influenceRadsCalc = new ConstantInfluenceRadiusCalculator(influenceRad);
//        InfluenceRadiusCalculator influenceRadsCalc = new EnsureNodesNum(4, 10);
        return new SimpleWeakformProject(project, model, influenceRadsCalc, assemblier, shapeFunc, constitutiveLaw);
    }
}
