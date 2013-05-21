/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.search;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SphereSearcher<T> {

    void setAll(Collection<? extends T> datas);

    List<T> searchInSphere(double[] center, double rad);
}
