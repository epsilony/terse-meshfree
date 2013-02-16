/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

/**
 *
 * @author epsilon
 */
public interface WeakformProcessRunnerObserver {

    void balanceProcessed(WeakformProcessRunnable weakformProcessRunnable);

    void neumannProcessed(WeakformProcessRunnable weakformProcessRunnable);

    void dirichletProcessed(WeakformProcessRunnable weakformProcessRunnable);
}
