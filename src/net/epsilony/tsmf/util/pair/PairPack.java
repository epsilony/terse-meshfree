/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.pair;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PairPack<K, V> implements WithPair<K, V> {

    public K key;
    public V value;

    public PairPack(K value, V attach) {
        this.key = value;
        this.value = attach;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public static <K, V> List<WithPair<K, V>> pack(List<? extends K> ks, List<? extends V> vs, List<WithPair<K, V>> result) {
        if (ks.size() != vs.size()) {
            throw new IllegalArgumentException("ks.size() and vs.size() is different (" + ks.size() + " and " + vs.size());
        }

        result.clear();

        Iterator<? extends K> kIter = ks.iterator();
        for (V v : vs) {
            K k = kIter.next();
            result.add(new PairPack<>(k, v));
        }
        return result;
    }
}
