/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import java.util.List;

/**
 *
 * @author epsilon
 */
public interface WeakformTask {

    List<TaskUnit> balance();

    List<TaskUnit> neumann();

    List<TaskUnit> dirichlet();
}
