/* (c) Copyright by Man YUAN */
package net.epsilony.tb.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tb.WithDiffOrder;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface BasisFunction extends WithDiffOrder, SynchronizedClonable<BasisFunction> {

    TDoubleArrayList[] values(double[] xy, TDoubleArrayList[] output);

    int basisLength();
}
