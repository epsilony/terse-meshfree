/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import org.junit.Test;
import static org.junit.Assert.*;
import static java.lang.Math.*;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ArcSegment2DTest {

    public ArcSegment2DTest() {
    }

    @Test
    public void testCalcCenter() {
        double radius = 3;
        double xTrans = -3.1;
        double yTrans = 2.2;
        ArcSegment2D arc = new ArcSegment2D();
        arc.setRadius(radius);
        arc.setHead(new Node(radius * cos(PI / 6) + xTrans, radius * sin(PI / 6) + yTrans));
        arc.setSucc(new LinearSegment2D(new Node(
                radius * cos(PI / 3) + xTrans,
                radius * sin(PI / 3) + yTrans)));
        double[] center = arc.calcCenter(null);
        double[] exp = new double[]{xTrans, yTrans};
        assertArrayEquals(exp, center, 1e-14);

        arc.setCenterOnChordLeft(false);
        center = arc.calcCenter(null);
        exp = new double[]{xTrans + 2 * radius * cos(PI / 12) * cos(PI / 4),
            yTrans + 2 * radius * cos(PI / 12) * sin(PI / 4)};
        assertArrayEquals(exp, center, 1e-14);
    }

    @Test
    public void testValues() {
        double radius = 3;
        double xTrans = -3.1;
        double yTrans = 2.2;
        ArcSegment2D arc = new ArcSegment2D();
        arc.setRadius(radius);
        arc.setHead(new Node(radius * cos(PI / 6) + xTrans, radius * sin(PI / 6) + yTrans));
        arc.setSucc(new LinearSegment2D(new Node(
                radius * cos(PI / 3) + xTrans,
                radius * sin(PI / 3) + yTrans)));
        double[] samples = new double[]{0, 1, 0.5, 0.35};
        double[][] exps = new double[][]{
            {arc.getHeadCoord()[0], arc.getHeadCoord()[1]},
            {arc.getRearCoord()[0], arc.getRearCoord()[1]},
            {radius * cos(PI / 4) + xTrans, radius * sin(PI / 4) + yTrans},
            {radius * cos(PI / 6 + PI / 6 * 0.35) + xTrans, radius * sin(PI / 6 + PI / 6 * 0.35) + yTrans}
        };

        for (int i = 0; i < samples.length; i++) {
            double t = samples[i];
            double[] exp = exps[i];
            double[] act = arc.values(t, null);
            assertArrayEquals(exp, act, 1e-14);
        }

        arc.setGreatArc(true);

        exps[2] = new double[]{radius * cos(5 * PI / 4) + xTrans, radius * sin(5 * PI / 4) + yTrans};
        exps[3] = new double[]{
            radius * cos(PI / 6 - 5 * PI / 6 * 0.35) + xTrans,
            radius * sin(PI / 6 - 5 * PI / 6 * 0.35) + yTrans};

        arc.setGreatArc(false);
        arc.setDiffOrder(1);
        double sample = 0.33;
        double[] exp = new double[]{
            radius * cos(PI / 6 + PI / 6 * 0.33) + xTrans,
            radius * sin(PI / 6 + PI / 6 * 0.33) + yTrans,
            -radius * sin(PI / 6 + PI / 6 * 0.33) * PI / 6,
            radius * cos(PI / 6 + PI / 6 * 0.33) * PI / 6
        };
        double[] act = arc.values(sample, null);
        assertArrayEquals(act, exp, 1e-14);
    }

    @Test
    public void testDistanceTo() {
        double radius = 3;
        double xTrans = -3.1;
        double yTrans = 2.2;
        ArcSegment2D arc = new ArcSegment2D();
        arc.setRadius(radius);
        arc.setHead(new Node(radius * cos(PI / 6) + xTrans, radius * sin(PI / 6) + yTrans));
        arc.setSucc(new LinearSegment2D(new Node(
                radius * cos(PI / 3) + xTrans,
                radius * sin(PI / 3) + yTrans)));

        double sampleRad = 2;
        double sampleAmpAngles = PI / 6 * 1.32;
        double exp = radius - sampleRad;
        double x = sampleRad * cos(sampleAmpAngles) + xTrans;
        double y = sampleRad * sin(sampleAmpAngles) + yTrans;
        double act = arc.distanceTo(x, y);
        assertEquals(exp, act, 1e-14);

        sampleAmpAngles = PI / 6 * 0.9;
        x = sampleRad * cos(sampleAmpAngles) + xTrans;
        y = sampleRad * sin(sampleAmpAngles) + yTrans;
        act = arc.distanceTo(x, y);
        exp = Math2D.distance(x, y, arc.getHeadCoord()[0], arc.getHeadCoord()[1]);
        assertEquals(exp, act, 1e-14);
        
        arc.setGreatArc(true);
        exp = radius - sampleRad;
        act = arc.distanceTo(x, y);
        assertEquals(exp, act, 1e-14);
    }
}