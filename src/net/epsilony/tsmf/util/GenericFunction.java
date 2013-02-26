/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface GenericFunction<T, V> {

    V value(T input, V output);
}
