/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformProcessRunnerObserver {

    void volumeProcessed(WeakformProcessRunnable weakformProcessRunnable);

    void neumannProcessed(WeakformProcessRunnable weakformProcessRunnable);

    void dirichletProcessed(WeakformProcessRunnable weakformProcessRunnable);
}
