/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tsmf.util.quadrature.GaussLegendre;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author epsilon
 */
public class TimoshenkoStandardProjectTest {

    public TimoshenkoStandardProjectTest() {
    }

    @Test
    public void testAreaLength() {
        double w = 10, h = 6;
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(w, h, 1000, 0.4, 20);
        double segLen = 1;
        double quadDomainSize = 1;
        double expArea = w * h;
        double expLen = h;
        boolean getHere = false;
        for (int degree = 1; degree <= GaussLegendre.MAXPOINTS * 2 - 1; degree++) {
            TimoshenkoStandardProject project = new TimoshenkoStandardProject(timoBeam, segLen, quadDomainSize, degree);
            double actArea = 0;
            for (ProcessPoint p : project.balance()) {
                actArea += p.weight;
            }
            assertEquals(expArea, actArea, 1e-10);
            double neumannLen = 0;
            for (ProcessPoint p : project.neumann()) {
                neumannLen += p.weight;
            }
            assertEquals(expLen, neumannLen, 1e-10);
            double diriLen = 0;
            for (ProcessPoint p : project.dirichlet()) {
                diriLen += p.weight;
            }
            assertEquals(expLen, diriLen, 1e-10);
            getHere = true;
        }
        assertTrue(getHere);
    }
}