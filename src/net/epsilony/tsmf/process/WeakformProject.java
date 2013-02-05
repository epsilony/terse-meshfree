/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tsmf.shape_func.ShapeFunction;

/**
 *
 * @author epsilon
 */
public class WeakformProject {

    public WeakformTask project;
    public Model2D model;
    public InfluenceRadiusCalculator influenceRadCalc;
    public WFAssemblier assemblier;
    public ShapeFunction shapeFunc;
    public ConstitutiveLaw constitutiveLaw;

    public WeakformProject(WeakformTask project, Model2D model, InfluenceRadiusCalculator influenceRadCalc, WFAssemblier assemblier, ShapeFunction shapeFunc, ConstitutiveLaw constitutiveLaw) {
        this.project = project;
        this.model = model;
        this.influenceRadCalc = influenceRadCalc;
        this.assemblier = assemblier;
        this.shapeFunc = shapeFunc;
        this.constitutiveLaw = constitutiveLaw;
    }
}
