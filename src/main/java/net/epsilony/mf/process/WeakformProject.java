/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.Model2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.assemblier.WeakformAssemblier;
import net.epsilony.tb.shape_func.ShapeFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformProject {

    WeakformAssemblier getAssemblier();

    ConstitutiveLaw getConstitutiveLaw();

    InfluenceRadiusCalculator getInfluenceRadiusCalculator();

    Model2D getModel();

    ShapeFunction getShapeFunction();

    WeakformQuadratureTask getWeakformQuadratureTask();
    
}
