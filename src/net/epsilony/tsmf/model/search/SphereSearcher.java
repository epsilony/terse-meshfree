/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SphereSearcher<T> {

    List<T> searchInSphere(double[] center, double rad);
}
