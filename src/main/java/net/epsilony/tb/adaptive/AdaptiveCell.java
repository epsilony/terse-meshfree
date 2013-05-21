/* (c) Copyright by Man YUAN */
package net.epsilony.tb.adaptive;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface AdaptiveCell {

    void fissionToChildren();

    boolean isAbleToFissionToChildren();

    AdaptiveCell findOneFissionObstrutor();

    void fusionFromChildren();

    boolean isAbleToFusionFromChildren();

    AdaptiveCell[] getChildren();

    public AdaptiveCellEdge[] getEdges();
}
