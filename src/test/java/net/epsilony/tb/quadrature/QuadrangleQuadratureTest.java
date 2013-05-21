/* (c) Copyright by Man YUAN */
package net.epsilony.tb.quadrature;

import net.epsilony.tb.ArrvarFunction;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleQuadratureTest {

    public QuadrangleQuadratureTest() {
    }

    @Test
    public void testArea() {
        double[] quad = new double[]{4, 4, -2.1, 1.6, 0.8, -1.9, 3, 1.1};
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setQuadrangle(quad);
        final double val = -1.4;
        double exp = 15.845 * val;
        for (int degree = GaussLegendre.MINPOINTS * 2 - 1; degree <= GaussLegendre.MAXPOINTS * 2 - 1; degree++) {
            qQuad.setDegree(degree);
            double act = qQuad.quadrate(new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    return val;
                }
            });
            assertEquals(exp, act, 1e-12);
        }
    }

    @Test
    public void testX() {
        double[] quad = new double[]{0, 0, 4, 0, 4, 3, 0, 3};
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setQuadrangle(quad);
        double exp = 24;
        for (int degree = GaussLegendre.MINPOINTS * 2 - 1; degree <= GaussLegendre.MAXPOINTS * 2 - 1; degree++) {
            qQuad.setDegree(degree);
            double act = qQuad.quadrate(new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    return vec[0];
                }
            });
            assertEquals(exp, act, 1e-12);
        }
    }

    @Test
    public void testY() {
        double[] quad = new double[]{0, 0, 4, 0, 4, 3, 0, 3};
        QuadrangleQuadrature qQuad = new QuadrangleQuadrature();
        qQuad.setQuadrangle(quad);
        double exp = 18;
        for (int degree = GaussLegendre.MINPOINTS * 2 - 1; degree <= GaussLegendre.MAXPOINTS * 2 - 1; degree++) {
            qQuad.setDegree(degree);
            double act = qQuad.quadrate(new ArrvarFunction() {
                @Override
                public double value(double[] vec) {
                    return vec[1];
                }
            });
            assertEquals(exp, act, 1e-12);
        }
    }
}
