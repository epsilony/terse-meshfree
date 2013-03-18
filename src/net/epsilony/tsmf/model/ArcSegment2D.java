/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ArcSegment2D extends AbstractSegment2D {

    double radius;
    boolean greatArc = false;
    boolean centerOnChordLeft = true;//chord is a linear segment start from head node and end at rear node

    public double[] calcCenter(double[] result) {
        if (null == result) {
            result = new double[2];
        }
        double[] headCoord = getHeadCoord();
        double[] rearCoord = getRearCoord();
        double dx = rearCoord[0] - headCoord[0];
        double dy = rearCoord[1] - headCoord[1];
        double midToCenterX, midToCenterY;
        if (centerOnChordLeft) {
            midToCenterX = -dy;
            midToCenterY = dx;
        } else {
            midToCenterX = dy;
            midToCenterY = -dx;
        }
        double centerToMidDistance = Math.sqrt(radius * radius - dx * dx / 4 - dy * dy / 4);
        double tLength = Math.sqrt(midToCenterX * midToCenterX + midToCenterY * midToCenterY);
        double tScale = centerToMidDistance / tLength;
        midToCenterX *= tScale;
        midToCenterY *= tScale;
        double midX = (rearCoord[0] + headCoord[0]) / 2;
        double midY = (rearCoord[1] + headCoord[1]) / 2;
        result[0] = midX + midToCenterX;
        result[1] = midY + midToCenterY;
        return result;
    }

    @Override
    public double distanceTo(double x, double y) {
        double[] center = calcCenter(null);
        double[] headCoord = getHeadCoord();
        double[] rearCoord = getRearCoord();
        double vecX = x - center[0];
        double vecY = y - center[1];
        double crossToHead = Math2D.cross(vecX, vecY, headCoord[0] - center[0], headCoord[1] - center[1]);
        double crossToRear = Math2D.cross(vecX, vecY, rearCoord[0] - center[0], rearCoord[1] - center[1]);
        boolean betwean = crossToHead * crossToRear < 0;
        if (betwean && !greatArc || !betwean && greatArc) {
            return radius - Math.sqrt(vecX * vecX + vecY * vecY);
        } else {
            return Math.min(
                    Math2D.distance(x, y, headCoord[0], headCoord[1]),
                    Math2D.distance(x, y, rearCoord[0], rearCoord[1]));
        }
    }

    @Override
    public ArcSegment2D bisectionAndReturnNewSuccessor() {
        int diffOrderBack = getDiffOrder();
        setDiffOrder(0);
        double[] midPoint = values(0.5, null);
        setDiffOrder(diffOrderBack);
        ArcSegment2D newSucc = new ArcSegment2D();
        newSucc.setHead(new Node(midPoint));
        newSucc.setSucc(succ);
        succ.setPred(newSucc);
        succ = newSucc;
        newSucc.setPred(this);
        newSucc.setDiffOrder(diffOrder);
        newSucc.setRadius(radius);
        newSucc.setGreatArc(greatArc);
        newSucc.setCenterOnChordLeft(centerOnChordLeft);
        return newSucc;
    }

    @Override
    public double[] values(double t, double[] results) {
        if (null == results) {
            results = new double[2 * (diffOrder + 1)];
        }
        calcCenter(results);
        double centerX = results[0];
        double centerY = results[1];
        double centerAngle = calcCenterAngle(centerX, centerY);
        double headAmplitudeAngle = calcHeadAmplitudeAngle(centerX, centerY);
        double resultAmplitudeAngle = headAmplitudeAngle + centerAngle * t;
        double vX = radius * Math.cos(resultAmplitudeAngle);
        double vY = radius * Math.sin(resultAmplitudeAngle);
        double resultX = vX + centerX;
        double resultY = vY + centerY;
        results[0] = resultX;
        results[1] = resultY;
        if (diffOrder >= 1) {
            results[2] = -vY * centerAngle;
            results[3] = vX * centerAngle;
        }
        return results;
    }

    public double calcCenterAngle(double centerX, double centerY) {
        double[] headCoord = getHeadCoord();
        double[] rearCoord = getRearCoord();
        double chordLengthSquare = Math2D.distanceSquare(headCoord, rearCoord);
        double centerAngleCosine = (2 * radius * radius - chordLengthSquare) / 2 / (radius * radius);
        if (Math.abs(centerAngleCosine) > 1) {
            throw new IllegalStateException(
                    "Radius too small, chord length: "
                    + Math.sqrt(chordLengthSquare)
                    + "radius:" + radius);
        }
        double centerAngle = Math.acos(centerAngleCosine);
        if (greatArc) {
            centerAngle -= Math.PI * 2;
        }
        return centerAngle;
    }

    public double calcHeadAmplitudeAngle(double centerX, double centerY) {
        double[] headCoord = getHeadCoord();
        return Math.atan2(headCoord[1] - centerY, headCoord[0] - centerX);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isGreatArc() {
        return greatArc;
    }

    public void setGreatArc(boolean greatArc) {
        this.greatArc = greatArc;
    }

    /**
     * chord is a linear segment start from head node and end at rear node
     *
     * @return
     */
    public boolean isCenterOnChordLeft() {
        return centerOnChordLeft;
    }

    /**
     * chord is a linear segment start from head node and end at rear node
     *
     * @return
     */
    public void setCenterOnChordLeft(boolean centerOnTheLeft) {
        this.centerOnChordLeft = centerOnTheLeft;
    }
}
