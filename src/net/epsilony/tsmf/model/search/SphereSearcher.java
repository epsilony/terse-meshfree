/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.search;

import java.util.List;

/**
 *
 * @author epsilon
 */
public interface SphereSearcher<T> {

    List<T> searchInSphere(double[] center, double rad);
}
