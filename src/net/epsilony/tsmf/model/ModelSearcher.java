/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ModelSearcher {

    void searchModel(double[] center, Segment2D bnd, double radius, boolean filetByInfluence, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg);
}
