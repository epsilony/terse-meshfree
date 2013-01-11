/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author epsilon
 */
public interface BasisFunction extends WithDiffOrder {

    TDoubleArrayList[] values(double[] xy, TDoubleArrayList[] output);

    int basisLength();
}
