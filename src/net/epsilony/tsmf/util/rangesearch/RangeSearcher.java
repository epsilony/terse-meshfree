/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.rangesearch;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface RangeSearcher<K, V> {

    List<V> rangeSearch(K from, K to);
}
