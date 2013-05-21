/* (c) Copyright by Man YUAN */
package net.epsilony.tb.solid;

import org.junit.Test;
import static org.junit.Assert.*;
import static java.lang.Math.*;
import net.epsilony.tb.Math2D;

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
        for (boolean onChordLeft : new boolean[]{true, false}) {
            double headAngle = PI / 6;
            double rearAngle = PI / 3;
            if (!onChordLeft) {
                headAngle = PI / 3;
                rearAngle = PI / 6;
            }
            ArcSegment2D arc = new ArcSegment2D();
            arc.setCenterOnChordLeft(onChordLeft);
            arc.setRadius(radius);
            arc.setHead(new Node(radius * cos(headAngle) + xTrans, radius * sin(headAngle) + yTrans));
            arc.setSucc(new LinearSegment2D(new Node(
                    radius * cos(rearAngle) + xTrans,
                    radius * sin(rearAngle) + yTrans)));
            double[] samples = new double[]{0, 1, 0.5, 0.35};
            double[][] exps = new double[][]{
                {arc.getHeadCoord()[0], arc.getHeadCoord()[1]},
                {arc.getRearCoord()[0], arc.getRearCoord()[1]},
                {radius * cos((headAngle + rearAngle) / 2) + xTrans,
                    radius * sin((headAngle + rearAngle) / 2) + yTrans},
                {radius * cos(headAngle * (1 - 0.35) + rearAngle * 0.35) + xTrans,
                    radius * sin(headAngle * (1 - 0.35) + rearAngle * 0.35) + yTrans}
            };

            for (int i = 0; i < samples.length; i++) {
                double t = samples[i];
                double[] exp = exps[i];
                double[] act = arc.values(t, null);
                assertArrayEquals(exp, act, 1e-14);
            }
        }
    }

    @Test
    public void testValueDiff() {
        double radius = 3;
        double xTrans = -3.1;
        double yTrans = 2.2;
        for (boolean onChordLeft : new boolean[]{true, false}) {
            double headAngle = PI / 6;
            double rearAngle = PI / 3;
            if (!onChordLeft) {
                headAngle = PI / 3;
                rearAngle = PI / 6;
            }
            ArcSegment2D arc = new ArcSegment2D();
            arc.setCenterOnChordLeft(false);
            arc.setRadius(radius);
            arc.setHead(new Node(radius * cos(headAngle) + xTrans, radius * sin(headAngle) + yTrans));
            arc.setSucc(new LinearSegment2D(new Node(
                    radius * cos(rearAngle) + xTrans,
                    radius * sin(rearAngle) + yTrans)));
            arc.setCenterOnChordLeft(onChordLeft);
            arc.setDiffOrder(1);
            double sample = 0.33;
            double angle = headAngle * (1 - sample) + rearAngle * sample;
            double[] exp = new double[]{
                radius * cos(angle) + xTrans,
                radius * sin(angle) + yTrans,
                -radius * sin(angle) * (rearAngle - headAngle),
                radius * cos(angle) * (rearAngle - headAngle)
            };
            double[] act = arc.values(sample, null);
            assertArrayEquals(act, exp, 1e-14);
        }
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
    }

    @Test
    public void testBisection() {
        double radius = 3;
        double xTrans = -3.1;
        double yTrans = 2.2;


        for (boolean centerOnChordLeft : new boolean[]{true, false}) {
            double headAmpAngle = PI / 6;
            double rearAmpAngle = PI * 2 / 3;
            if (!centerOnChordLeft) {
                double t = headAmpAngle;
                headAmpAngle = rearAmpAngle;
                rearAmpAngle = t;
            }
            ArcSegment2D arc = new ArcSegment2D();
            arc.setCenterOnChordLeft(centerOnChordLeft);
            arc.setRadius(radius);
            arc.setHead(new Node(radius * cos(headAmpAngle) + xTrans, radius * sin(headAmpAngle) + yTrans));
            arc.setSucc(new LinearSegment2D(new Node(
                    radius * cos(rearAmpAngle) + xTrans,
                    radius * sin(rearAmpAngle) + yTrans)));
            Segment2D rawTail = arc.getSucc();
            ArcSegment2D newSucc = arc.bisectionAndReturnNewSuccessor();

            assertTrue(arc.getSucc() == newSucc);
            assertTrue(newSucc.getPred() == arc);
            assertTrue(rawTail == newSucc.getSucc());
            assertTrue(rawTail.getPred() == newSucc);

            double[] arcCenter = arc.calcCenter(null);
            double[] expCenter = new double[]{xTrans, yTrans};
            assertArrayEquals(expCenter, arcCenter, 1e-14);

            arcCenter = newSucc.calcCenter(null);
            assertArrayEquals(expCenter, arcCenter, 1e-14);

            double arcCenterAngle = arc.calcCenterAngle();
            double expArcCenterAngle = (headAmpAngle + rearAmpAngle) / 2 - headAmpAngle;
            assertEquals(expArcCenterAngle, arcCenterAngle, 1e-14);

            double newSuccCenterAngle = newSucc.calcCenterAngle();
            assertEquals(expArcCenterAngle, newSuccCenterAngle, 1e-14);
        }
    }
}