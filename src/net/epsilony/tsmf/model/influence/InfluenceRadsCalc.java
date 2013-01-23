/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.influence;

import net.epsilony.tsmf.model.ModelSearcher;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadsCalc {

    double influcenceRadius(Node node, Segment2D seg, ModelSearcher mSer);
}
