/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

/**
 *
 * @author epsilon
 */
public interface UnivarArrayFunction extends WithDiffOrder{
    double[] values(double x,double[] results);
}
