/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.synchron;

import java.util.Iterator;

/**
 *
 * @author epsilon
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
