/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tsmf.util.WithDiffOrder;
import net.epsilony.tsmf.util.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface BasisFunction extends WithDiffOrder, SynchronizedClonable<BasisFunction> {

    TDoubleArrayList[] values(double[] xy, TDoubleArrayList[] output);

    int basisLength();
}
