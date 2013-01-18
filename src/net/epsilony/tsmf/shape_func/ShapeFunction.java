/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author @author <a href="mailto:my_email@email.exmaple.com">Benoit
 * St-Pierre</a>
 */
public interface ShapeFunction extends WithDiffOrder {

    TDoubleArrayList[] values(double[] xy, List<double[]> coords, TDoubleArrayList influcenceRads, TDoubleArrayList[] dists, TDoubleArrayList[] output);
}
