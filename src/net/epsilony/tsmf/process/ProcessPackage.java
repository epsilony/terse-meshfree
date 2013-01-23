/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.influence.InfluenceRadsCalc;
import net.epsilony.tsmf.shape_func.ShapeFunction;

/**
 *
 * @author epsilon
 */
public class ProcessPackage {

    public Project project;
    public Model2D model;
    public InfluenceRadsCalc influenceRadCalc;
    public WFAssemblier assemblier;
    public ShapeFunction shapeFunc;

    public ProcessPackage(Project project, Model2D model, InfluenceRadsCalc influenceRadCalc, WFAssemblier assemblier, ShapeFunction shapeFunc) {
        this.project = project;
        this.model = model;
        this.influenceRadCalc = influenceRadCalc;
        this.assemblier = assemblier;
        this.shapeFunc = shapeFunc;
    }
}
