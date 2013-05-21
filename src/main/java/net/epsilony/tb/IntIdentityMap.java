/* (c) Copyright by Man YUAN */
package net.epsilony.tb;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author <a href="mailto:epsionyuan@gmail.com">Man YUAN</a>
 */
public class IntIdentityMap<K extends IntIdentity, V> implements Iterable<V> {

    public static final int NULL_INDEX_SUPREMUM = -1;
    ArrayList<V> values;

    public IntIdentityMap() {
        values = new ArrayList<>();
    }

    public IntIdentityMap(int initialCapacity) {
        values = new ArrayList<>(initialCapacity);
    }

    public V put(K key, V value) {
        final int keyId = key.getId();
        if (keyId >= size()) {
            throw new IllegalArgumentException("key.getId() is so big that it bursts the value array");
        }
        if (keyId > NULL_INDEX_SUPREMUM) {
            V old = values.get(keyId);
            values.set(keyId, value);
            return old;
        } else {
            key.setId(values.size());
            values.add(value);
            return null;
        }
    }

    public V get(K key) {
        final int keyId = key.getId();
        return getById(keyId);
    }

    public V getById(int keyId) {
        if (keyId > NULL_INDEX_SUPREMUM && keyId < values.size()) {
            return values.get(keyId);
        } else {
            return null;
        }
    }

    public void ensureCapacity(int minCapacity) {
        values.ensureCapacity(minCapacity);
    }

    public int size() {
        return values.size();
    }

    public void appendNullValues(int newSize) {
        ensureCapacity(newSize);
        for (int i = size(); i < newSize; i++) {
            values.add(null);
        }
    }

    public void clear() {
        values.clear();
    }

    @Override
    public Iterator<V> iterator() {
        return values.iterator();
    }
}
