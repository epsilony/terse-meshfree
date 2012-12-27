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
public class PairPack<V,A> {
    public V value;
    public A attach;

    public PairPack(V value, A attach) {
        this.value = value;
        this.attach = attach;
    }
    
    public static <T,V> Comparator<PairPack<T,V>> packComparator(final Comparator<T> comp,Class<V> cls){
        return new Comparator<PairPack<T, V>>() {

            @Override
            public int compare(PairPack<T, V> o1, PairPack<T, V> o2) {
                return comp.compare(o1.value, o2.value);
            }
        };
    }
    
}
