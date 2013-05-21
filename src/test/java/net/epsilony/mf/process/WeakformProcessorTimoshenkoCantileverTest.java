/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.model.influence.EnsureNodesNum;
import net.epsilony.tb.GenericFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessorTimoshenkoCantileverTest {

    public WeakformProcessorTimoshenkoCantileverTest() {
    }

    @Test
    public void testErrSquareIntegrationOnXAxis() {
        System.out.println("test Timoshenko standard beam, x axis");
        genTimoshenkoStandardCantileverProcessor();
        processAndGenPostProcessor();
        timoPostProcessor.setDiffOrder(0);

        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        boolean compareDistanceU = false;
        double[] results = integrateErrorSquareOnCurve(xAxisCure, compareDistanceU);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-11*accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        //assertEquals(1.0728621297419604E-16, err, 1e-16); //typical value
    }

    @Test
    public void testErrSquareIntegrationOnLeftSide() {
        System.out.println("test Timoshinko standard beam, left edge");
        genTimoshenkoStandardCantileverProcessor();
        processAndGenPostProcessor();
        timoPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        boolean compareDistanceU = false;
        double[] results = integrateErrorSquareOnCurve(curve, compareDistanceU);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-8*accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
        //assertEquals(5.9676721435783116E-18, err, 1e-18); //typical value
    }

    @Test
    public void testOnLeftSide_EnsureNodesNum() {
        System.out.println("test Timoshinko standard beam, left edge");
        genTimoshenkoStandardCantileverProcessor();
        timoProcessor.setInfluenceRadiusCalculator(new EnsureNodesNum(4, 10));
        processAndGenPostProcessor();
        timoPostProcessor.setDiffOrder(0);
        CurveOnLeftSide curve = new CurveOnLeftSide();
        boolean compareDistanceU = false;
        double[] results = integrateErrorSquareOnCurve(curve, compareDistanceU);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7*accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    @Test
    public void testOnXAxis_EnsureNodesNum() {
        System.out.println("test Timoshenko standard beam, x axis");
        genTimoshenkoStandardCantileverProcessor();
        timoProcessor.setInfluenceRadiusCalculator(new EnsureNodesNum(4, 10));
        processAndGenPostProcessor();
        timoPostProcessor.setDiffOrder(0);

        CurveOnXAxis xAxisCure = new CurveOnXAxis();
        boolean compareDistanceU = false;
        double[] results = integrateErrorSquareOnCurve(xAxisCure, compareDistanceU);
        double err = results[0];
        double accurateValue = results[1];
        double expErr = 1e-7*accurateValue;
        System.out.println("err = " + err);
        System.out.println("acurateValue = " + accurateValue);
        assertTrue(err <= expErr);
    }

    public double[] integrateErrorSquareOnCurve(GenericFunction<Double, double[]> curve, boolean displacementU) {
        final UnivariateFunction actFunc = new NumericalDisplacementOnCurve(curve, displacementU);
        final UnivariateFunction expFunc = new PreciseValueOnCurve(curve, displacementU);
        UnivariateFunction func = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = (actFunc.value(x) - expFunc.value(x));
                return d * d;
            }
        };

        UnivariateFunction oriFunc = new UnivariateFunction() {
            @Override
            public double value(double x) {
                double d = actFunc.value(x);
                return d * d;
            }
        };
        SimpsonIntegrator integrator = new SimpsonIntegrator();
        double oriValue = integrator.integrate(10000, oriFunc, 0, 1);
        double errValue = integrator.integrate(10000, func, 0, 1);
        return new double[]{errValue, oriValue};
    }
    PostProcessor timoPostProcessor;
    WeakformProcessor timoProcessor;

    public void genTimoshenkoStandardCantileverProcessor() {
        timoProcessor = WeakformProcessor.genTimoshenkoProjectProcess();
    }

    private void processAndGenPostProcessor() {
        System.out.println("Multi Processing: " + timoProcessor.isActuallyMultiThreadable());
        timoProcessor.prepare();
        timoProcessor.process();
        timoProcessor.solve();
        timoPostProcessor = timoProcessor.postProcessor();
    }
    public static final double SHRINK = 0.000001;

    public class CurveOnXAxis implements GenericFunction<Double, double[]> {

        @Override
        public double[] value(Double tD, double[] output) {
            if (null == output) {
                output = new double[2];
            }
            double t = tD;

            TimoshenkoStandardTask timoTask = (TimoshenkoStandardTask) timoProcessor.weakformQuadratureTask;
            double left = timoTask.rectProject.left;
            double right = timoTask.rectProject.right;
            left += SHRINK;
            right -= SHRINK;
            output[1] = 0;
            output[0] = left * (1 - t) + right * t;
            return output;
        }
    }

    public class CurveOnLeftSide implements GenericFunction<Double, double[]> {

        @Override
        public double[] value(Double tD, double[] output) {
            if (null == output) {
                output = new double[2];
            }
            double t = tD;

            TimoshenkoStandardTask timoTask = (TimoshenkoStandardTask) timoProcessor.weakformQuadratureTask;
            double down = timoTask.rectProject.down;
            double up = timoTask.rectProject.up;
            down += SHRINK;
            up -= SHRINK;
            output[1] = down * (1 - t) + up * t;
            output[0] = SHRINK;
            return output;
        }
    }

    public class NumericalDisplacementOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        boolean outputU;

        @Override
        public double value(double t) {
            double[] pt = curveFunction.value(t, null);
            double[] value = timoPostProcessor.value(pt, null);
            int index = outputU ? 0 : 1;
            return value[index];
        }

        public NumericalDisplacementOnCurve(GenericFunction<Double, double[]> curveFunction, boolean outputU) {
            this.curveFunction = curveFunction;
            this.outputU = outputU;
        }
    }

    public class PreciseValueOnCurve implements UnivariateFunction {

        GenericFunction<Double, double[]> curveFunction;
        boolean outputU;

        @Override
        public double value(double t) {
            TimoshenkoStandardTask timoTask = (TimoshenkoStandardTask) timoProcessor.weakformQuadratureTask;
            double[] pt = curveFunction.value(t, null);
            double[] value = timoTask.timoBeam.displacement(pt[0], pt[1], 0, null);
            int index = outputU ? 0 : 1;
            return value[index];
        }

        public PreciseValueOnCurve(GenericFunction<Double, double[]> curveFunction, boolean outputU) {
            this.curveFunction = curveFunction;
            this.outputU = outputU;
        }
    }
}
