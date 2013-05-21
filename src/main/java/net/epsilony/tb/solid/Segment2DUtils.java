/* (c) Copyright by Man YUAN */
package net.epsilony.tb.solid;

import net.epsilony.tb.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a> St-Pierre</a>
 */
public class Segment2DUtils {

    public static double chordLength(Segment2D seg) {
        return Math2D.distance(seg.getHeadCoord(), seg.getRearCoord());
    }

    public static double[] chordMidPoint(Segment2D seg, double[] result) {
        return Math2D.pointOnSegment(seg.getHeadCoord(), seg.getRearCoord(), 0.5, result);
    }

    public static boolean isPointStrictlyAtChordLeft(Segment2D seg, double[] xy) {
        double[] headCoord = seg.getHead().coord;
        double[] rearCoord = seg.getRear().coord;
        double dhrX = rearCoord[0] - headCoord[0];
        double dhrY = rearCoord[1] - headCoord[1];
        double dx = xy[0] - headCoord[0];
        double dy = xy[1] - headCoord[1];
        double cross = Math2D.cross(dhrX, dhrY, dx, dy);
        return cross > 0 ? true : false;
    }

    public static double distanceToChord(Segment2D seg, double x, double y) {
        double[] v1 = seg.getHead().coord;
        double[] v2 = seg.getRear().coord;
        double d12_x = v2[0] - v1[0];
        double d12_y = v2[1] - v1[1];
        double len12 = Math.sqrt(d12_x * d12_x + d12_y * d12_y);
        double d1p_x = x - v1[0];
        double d1p_y = y - v1[1];
        double project_len = Math2D.dot(d1p_x, d1p_y, d12_x, d12_y) / len12;
        if (project_len > len12) {
            double dx = x - v2[0];
            double dy = y - v2[1];
            return Math.sqrt(dx * dx + dy * dy);
        } else if (project_len < 0) {
            return Math.sqrt(d1p_x * d1p_x + d1p_y * d1p_y);
        } else {
            return Math.abs(Math2D.cross(d12_x, d12_y, d1p_x, d1p_y)) / len12;
        }
    }

    public static double distanceToChord(Segment2D seg, double[] pt) {
        return distanceToChord(seg, pt[0], pt[1]);
    }

    public static double maxChordLength(Iterable<? extends Segment2D> segments) {
        double maxLength = 0;
        for (Segment2D seg : segments) {
            double chordLength = chordLength(seg);
            if (chordLength > maxLength) {
                maxLength = chordLength;
            }
        }
        return maxLength;
    }

    public static void link(Segment2D asPred, Segment2D asSucc) {
        asPred.setSucc(asSucc);
        asSucc.setPred(asPred);
    }
}
