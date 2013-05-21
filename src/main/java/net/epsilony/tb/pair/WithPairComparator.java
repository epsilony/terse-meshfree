/* (c) Copyright by Man YUAN */
package net.epsilony.tb.pair;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WithPairComparator<K, V> implements Comparator<WithPair<K, V>> {

    Comparator<K> comparator;

    public WithPairComparator(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(WithPair<K, V> o1, WithPair<K, V> o2) {
        return comparator.compare(o1.getKey(), o2.getKey());
    }
}
