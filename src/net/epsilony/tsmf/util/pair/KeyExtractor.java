/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.pair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface KeyExtractor<T, K> {

    K extractKey(T t);
}
