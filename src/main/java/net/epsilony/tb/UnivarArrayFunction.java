/* (c) Copyright by Man YUAN */
package net.epsilony.tb;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface UnivarArrayFunction extends WithDiffOrder {

    double[] values(double x, double[] results);
}
