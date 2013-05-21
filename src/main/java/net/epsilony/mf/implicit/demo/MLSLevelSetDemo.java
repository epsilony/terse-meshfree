/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit.demo;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.implicit.LevelSetApproximationAssemblier;
import net.epsilony.mf.implicit.RectangleWithHoles;
import net.epsilony.mf.implicit.TriangleContourBuilder;
import net.epsilony.mf.implicit.TriangleContourCell;
import net.epsilony.mf.implicit.TriangleContourCellFactory;
import net.epsilony.mf.model.influence.ConstantInfluenceRadiusCalculator;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.process.PostProcessor;
import net.epsilony.mf.process.WeakformProcessor;
import net.epsilony.tb.shape_func.MLS;
import net.epsilony.tb.GenericFunction;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.common_func.NormalFunction;
import net.epsilony.tb.ui.CommonFrame;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MLSLevelSetDemo implements NeedPreparation {

    public static double DEFAULT_SIGMA = 20;
    public static Rectangle2D DEFAULT_RECTANGLE = new Rectangle2D.Double(10, 10, 100, 60);
    public static double DEFAULT_HOLE_RADIUS = 4;
    public static double DEFAULT_HOLE_DISTANCE = 2;
    public static InfluenceRadiusCalculator DEFAULT_INFLUENCE_RADIUS_CALCULATOR =
            new ConstantInfluenceRadiusCalculator(8);
    public static int DEFAULT_QUADRATURE_POWER = 2;
    public static double DEFAULT_TRIANGLE_SIZE = 2;
    public static double DEFAULT_SEGMENT_SIZE = 2;
    NormalFunction assemblyWeightFunction = new NormalFunction();
    double sigma = DEFAULT_SIGMA;
    LevelSetApproximationAssemblier assemblier = new LevelSetApproximationAssemblier();
    MLS mls = new MLS();
    WeakformProcessor weakformProcessor = new WeakformProcessor();
    RectangleWithHoles rectangleWithHoles = new RectangleWithHoles(
            DEFAULT_RECTANGLE, DEFAULT_HOLE_RADIUS, DEFAULT_HOLE_DISTANCE);
    double triangleSize = DEFAULT_TRIANGLE_SIZE;
    double segmentSize = DEFAULT_SEGMENT_SIZE;
    int quadraturePower = DEFAULT_QUADRATURE_POWER;

    @Override
    public void prepare() {
        assemblyWeightFunction.setSigma(sigma);
        assemblier.setWeightFunction(assemblyWeightFunction);

        rectangleWithHoles.setQuadraturePower(quadraturePower);
        rectangleWithHoles.setSegmentSize(segmentSize);
        rectangleWithHoles.setTriangleSize(triangleSize);
        rectangleWithHoles.prepare();

        weakformProcessor.setInfluenceRadiusCalculator(DEFAULT_INFLUENCE_RADIUS_CALCULATOR);
        weakformProcessor.setAssemblier(assemblier);
        weakformProcessor.setModel(rectangleWithHoles.getModel());
        weakformProcessor.setWeakformQuadratureTask(rectangleWithHoles.getWeakformQuadratureTask());

        weakformProcessor.setShapeFunction(mls);
        //weakformProcessor.setEnableMultiThread(false);
        weakformProcessor.prepare();
    }

    public static void main(String[] args) {
        MLSLevelSetDemo demo = new MLSLevelSetDemo();
        demo.prepare();
        demo.weakformProcessor.process();
        demo.weakformProcessor.solve();
        final PostProcessor postProcessor = demo.weakformProcessor.postProcessor();
        postProcessor.setDiffOrder(0);
        TriangleContourCellFactory cellFactory = new TriangleContourCellFactory();
        TriangleContourCell[][] coverRectangle = cellFactory.coverRectangle(demo.rectangleWithHoles.getRectangle(), 1);
        TriangleContourBuilder contourBuilder = new TriangleContourBuilder();
        List<TriangleContourCell> cells = new LinkedList<>();
        MiscellaneousUtils.addToList(coverRectangle, cells);
        contourBuilder.setCells(cells);
        contourBuilder.setLevelSetFunction(new GenericFunction<double[], double[]>() {
            @Override
            public double[] value(double[] input, double[] output) {
                double[] result = postProcessor.value(input, null);
                if (null != output) {
                    output[0] = result[0];
                } else {
                    output = result;
                }
                return output;
            }
        });

        contourBuilder.genContour();
        CommonFrame frame = new CommonFrame();
        frame.getMainPanel().addAndSetupModelDrawer(new TriangleContourBuilderDemoDrawer(contourBuilder));
        frame.getMainPanel().setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
