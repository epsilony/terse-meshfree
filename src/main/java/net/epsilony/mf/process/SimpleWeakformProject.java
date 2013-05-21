/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assemblier.WeakformAssemblier;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tb.shape_func.ShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SimpleWeakformProject implements WeakformProject {

    public WeakformQuadratureTask weakformQuadratureTask;
    public Model2D model;
    public InfluenceRadiusCalculator influenceRadiusCalculator;
    public WeakformAssemblier assemblier;
    public ShapeFunction shapeFunction;
    public ConstitutiveLaw constitutiveLaw;

    public SimpleWeakformProject(
            WeakformQuadratureTask project,
            Model2D model,
            InfluenceRadiusCalculator influenceRadCalc,
            WeakformAssemblier assemblier,
            ShapeFunction shapeFunc,
            ConstitutiveLaw constitutiveLaw) {
        this.weakformQuadratureTask = project;
        this.model = model;
        this.influenceRadiusCalculator = influenceRadCalc;
        this.assemblier = assemblier;
        this.shapeFunction = shapeFunc;
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    public WeakformQuadratureTask getWeakformQuadratureTask() {
        return weakformQuadratureTask;
    }

    @Override
    public Model2D getModel() {
        return model;
    }

    @Override
    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    @Override
    public WeakformAssemblier getAssemblier() {
        return assemblier;
    }

    @Override
    public ShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    @Override
    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }
}
