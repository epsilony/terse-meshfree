/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformTask {

    List<TaskUnit> volumeTasks();

    List<TaskUnit> neumannTasks();

    List<TaskUnit> dirichletTasks();
}
