/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.pair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WithPair<K, V> extends WithKey<K> {

    V getValue();
}
