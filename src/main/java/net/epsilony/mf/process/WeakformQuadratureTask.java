/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformQuadratureTask {

    List<WeakformQuadraturePoint> volumeTasks();

    List<WeakformQuadraturePoint> neumannTasks();

    List<WeakformQuadraturePoint> dirichletTasks();
}
