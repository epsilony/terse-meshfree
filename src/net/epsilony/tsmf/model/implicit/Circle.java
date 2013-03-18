/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import net.epsilony.tsmf.util.ArrvarFunction;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Circle implements ArrvarFunction, GenericFunction<double[], double[]> {

    double radius;
    double centerX, centerY;
    boolean concrete = true;

    public Circle(double centerX, double centerY, double radius) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public Circle() {
    }

    @Override
    public double value(double[] vec) {
        double result = radius - Math2D.distance(vec[0], vec[1], centerX, centerY);
        if (!concrete) {
            result = -result;
        }
        return result;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    Shape genProfile() {
        return new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public boolean isConcrete() {
        return concrete;
    }

    public void setConcrete(boolean concrete) {
        this.concrete = concrete;
    }

    @Override
    public double[] value(double[] input, double[] output) {
        if (null == output) {
            output = new double[]{value(input)};
        } else {
            output[0] = value(input);
        }
        return output;
    }
}
