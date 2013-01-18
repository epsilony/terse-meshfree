/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MonomialBasis2DTest {

    public MonomialBasis2DTest() {
    }

    @Test
    public void testSomeMethod() {
        double x = 1.2;
        double y = -2.3;
        int basisOrder = 4;
        int diffOrder = 1;
        double[][] exps = new double[][]{{1., 1.2, -2.3, 1.44, -2.76, 5.29,
                1.728, -3.312, 6.348, -12.167, 2.0736, -3.9744,
                7.6176, -14.6004, 27.9841},
            {0., 1., 0., 2.4, -2.3, 0.,
                4.32, -5.52, 5.29, 0., 6.912, -9.936,
                12.696, -12.167, 0.},
            {0., 0., 1., 0., 1.2, -4.6,
                0., 1.44, -5.52, 15.87, 0., 1.728,
                -6.624, 19.044, -48.668}};

        MonomialBasis2D monoBasis = new MonomialBasis2D(basisOrder);
        monoBasis.setDiffOrder(diffOrder);
        TDoubleArrayList[] acts = monoBasis.values(new double[]{x, y}, null);

        for (int i = 0; i < acts.length; i++) {
            double[] act = acts[i].toArray();
            double[] exp = exps[i];
            assertArrayEquals(exp, act, 1e-12);
        }
    }
}
