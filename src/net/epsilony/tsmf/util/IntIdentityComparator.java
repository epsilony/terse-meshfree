/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class IntIdentityComparator<T extends IntIdentity> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return o1.getId() - o2.getId();
    }
}
