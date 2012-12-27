/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import java.util.Comparator;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class PairPack<K,A> implements WithPair{
    public K key;
    public A value;

    public PairPack(K value, A attach) {
        this.key = value;
        this.value = attach;
    }
    
    public static <T,V> Comparator<PairPack<T,V>> packComparator(final Comparator<T> comp,Class<V> cls){
        return new Comparator<PairPack<T, V>>() {

            @Override
            public int compare(PairPack<T, V> o1, PairPack<T, V> o2) {
                return comp.compare(o1.key, o2.key);
            }
        };
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public A getValue() {
        return value;
    }
    
}
