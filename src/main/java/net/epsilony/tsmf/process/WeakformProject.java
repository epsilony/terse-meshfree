/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tsmf.process.assemblier.WeakformAssemblier;
import net.epsilony.tsmf.shape_func.ShapeFunction;

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
