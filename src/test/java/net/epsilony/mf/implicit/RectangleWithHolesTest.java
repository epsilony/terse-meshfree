/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import java.awt.geom.Rectangle2D;
import java.util.List;
import net.epsilony.tb.quadrature.QuadraturePoint;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleWithHolesTest {

    public RectangleWithHolesTest() {
    }

    @Test
    public void testPerimeter() {
        RectangleWithHoles rectangleWithHole = genInstance();
        List<QuadraturePoint> boundaryQuadraturePoints = rectangleWithHole.getBoundaryQuadraturePoints();
        double act = 0;
        for (QuadraturePoint qp : boundaryQuadraturePoints) {
            act += qp.weight;
        }
        Rectangle2D rect = rectangleWithHole.getRectangle();

        double exp = 0;
        exp += rect.getWidth() * 2 + rect.getHeight() * 2;
        for (Circle circle : rectangleWithHole.getHoles()) {
            exp += circle.getRadius() * 2 * Math.PI;
        }

        assertEquals(exp, act, 1e-11);
//        System.out.println("exp = " + exp);
//        System.out.println("act = " + act);
    }

    RectangleWithHoles genInstance() {
        Rectangle2D rectangle = new Rectangle2D.Double(10, 20, 100, 60);
        double holeRadius = 5;
        double holeDistance = 2;
        double triangleSize = 1;
        double spaceNodesExtention = triangleSize * 2;
        RectangleWithHoles rectangleWithHoles = new RectangleWithHoles(rectangle, holeRadius, holeDistance);
        rectangleWithHoles.setTriangleSize(triangleSize);
        rectangleWithHoles.setSpaceNodesExtension(spaceNodesExtention);
        rectangleWithHoles.prepare();
        return rectangleWithHoles;
    }
}