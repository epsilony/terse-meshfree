/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.rangesearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class DictComparator<T> implements Comparator<T> {

    ArrayList<Comparator<T>> comparators;
    private final int primeComparatorIndex;

    @Override
    public int compare(T o1, T o2) {
        final int size = comparators.size();
        for (int i = 0; i < size; i++) {
            int c = comparators.get((i + primeComparatorIndex) % size).compare(o1, o2);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    public DictComparator(List<? extends Comparator<T>> comparators, int primeComparatorIndex) {
        this.comparators = new ArrayList<>(comparators);
        this.primeComparatorIndex = primeComparatorIndex;
    }

    public DictComparator<T> getSlibing(int primeComparatorIndex) {
        return new DictComparator<>(comparators,primeComparatorIndex);
    }

    public int getPrimeComparatorIndex() {
        return primeComparatorIndex;
    }

    public ArrayList<Comparator<T>> getComparators() {
        return comparators;
    }

    public int getKeyDimensionSize() {
        return comparators.size();
    }
}
