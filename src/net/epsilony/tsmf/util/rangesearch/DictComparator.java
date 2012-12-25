/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.rangesearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class DictComparator<T> implements Comparator<T> {

    List<Comparator<T>> comparators;
    private final int mainKey;

    @Override
    public int compare(T o1, T o2) {
        final int size = comparators.size();
        for (int i = 0; i < size; i++) {
            int c = comparators.get((i + mainKey) % size).compare(o1, o2);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    public DictComparator(List<Comparator<T>> comparators, boolean copy, int mainKey) {
        if (copy) {
            this.comparators = new ArrayList<>(comparators);
        } else {
            this.comparators = comparators;
        }
        this.mainKey = mainKey;
    }

    public DictComparator<T> getSlibing(int mainKey) {
        return new DictComparator<>(comparators, false, mainKey);
    }

    public int getMainKey() {
        return mainKey;
    }

    public List<Comparator<T>> getComparators() {
        return comparators;
    }

    public int getKeyDimensionSize() {
        return comparators.size();
    }
}
