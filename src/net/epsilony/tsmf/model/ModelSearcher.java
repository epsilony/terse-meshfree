/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ModelSearcher {

    boolean isOnlySearchingInfluentialNodes();

    void setOnlySearchingInfluentialNodes(boolean onlySearchingInfluentialNodes);

    boolean isOnlyCareVisibleNodes();

    void setOnlyCareVisbleNodes(boolean onlyCareVisibleNodes);

    ModelSearchResult searchModel(double[] center, Segment2D bndOfCenter, double radius);
}
