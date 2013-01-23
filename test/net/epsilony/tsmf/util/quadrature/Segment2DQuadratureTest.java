/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.quadrature;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.ArrvarFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class Segment2DQuadratureTest {

    public Segment2DQuadratureTest() {
    }

    @Test
    public void testLength() {
        final double val = 1.3;
        ArrvarFunction func = new ArrvarFunction() {
            @Override
            public double value(double[] vec) {
                return val;
            }
        };

        Segment2D seg = new Segment2D(new Node(1, -1));
        seg.succ = new Segment2D(new Node(-2, 3));
        double exp = 5 * val;
        boolean getHere = false;
        for (int deg = 1; deg < GaussLegendre.MAXPOINTS * 2 - 1; deg++) {
            Segment2DQuadrature sq = new Segment2DQuadrature(seg, deg);
            double act = sq.quadrate(func);
            assertEquals(exp, act, 1e-12);
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testLadderX() {
        ArrvarFunction func = new ArrvarFunction() {
            @Override
            public double value(double[] vec) {
                return vec[0];
            }
        };

        Segment2D seg = new Segment2D(new Node(1, 2));
        seg.succ = new Segment2D(new Node(-2, 6));
        double exp = -2.5;
        boolean getHere = false;
        for (int deg = 1; deg < GaussLegendre.MAXPOINTS * 2 - 1; deg++) {
            Segment2DQuadrature sq = new Segment2DQuadrature(seg, deg);
            double act = sq.quadrate(func);
            assertEquals(exp, act, 1e-12);
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testLadderY() {
        ArrvarFunction func = new ArrvarFunction() {
            @Override
            public double value(double[] vec) {
                return vec[1];
            }
        };

        Segment2D seg = new Segment2D(new Node(1, 2));
        seg.succ = new Segment2D(new Node(-2, 6));
        double exp = 20;
        boolean getHere = false;
        for (int deg = 1; deg < GaussLegendre.MAXPOINTS * 2 - 1; deg++) {
            Segment2DQuadrature sq = new Segment2DQuadrature(seg, deg);
            double act = sq.quadrate(func);
            assertEquals(exp, act, 1e-12);
            getHere = true;
        }
        assertTrue(getHere);
    }
}
