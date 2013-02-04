/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.rangesearch;

import java.util.List;

/**
 *
 * @author epsilon
 */
public interface RangeSearcher<K, V> {

    List<V> rangeSearch(K from, K to);
}
