/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface BasisFunction extends WithDiffOrder {

    TDoubleArrayList[] values(double[] xy, TDoubleArrayList[] output);

    int basisLength();
}
