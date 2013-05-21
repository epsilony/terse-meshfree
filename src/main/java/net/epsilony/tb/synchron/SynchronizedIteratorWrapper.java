/* (c) Copyright by Man YUAN */
package net.epsilony.tb.synchron;

import java.util.Iterator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SynchronizedIteratorWrapper<T> {

    public SynchronizedIteratorWrapper(Iterator<T> iterator) {
        this.iterator = iterator;
    }
    Iterator<T> iterator;

    public synchronized T nextItem() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }

    }
}
