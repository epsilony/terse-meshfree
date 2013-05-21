/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import net.epsilony.tb.solid.ArcSegment2D;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class CircleTest {

    public CircleTest() {
    }

    @Test
    public void testToArcs() {
        double[] center = new double[]{-11.2, 2.3};
        double radius = 4;
        int expArcNum = 8;
        Circle circle = new Circle(center[0], center[1], radius);
        ArcSegment2D head = circle.toArcs(4);
        ArcSegment2D seg = head;
        int arcNum = 0;
        while (true) {
            assertArrayEquals(center, seg.calcCenter(null), 1e-14);
            double actAngle = seg.calcHeadAmplitudeAngle();
            double expAngle = Math.PI * 2 / expArcNum * arcNum;
            if (expAngle > Math.PI) {
                expAngle -= Math.PI * 2;
            }
            assertEquals(expAngle, actAngle, 1e-10);
            arcNum++;
            seg = (ArcSegment2D) seg.getSucc();
            if (seg == head) {
                break;
            }
        }
        assertEquals(expArcNum, arcNum);
    }
}