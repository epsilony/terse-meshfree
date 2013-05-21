/* (c) Copyright by Man YUAN */
package net.epsilony.tb;

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
