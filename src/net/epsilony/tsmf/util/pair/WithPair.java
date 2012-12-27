/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.pair;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public interface WithPair<K, V> extends WithKey<K> {

    V getValue();
}
