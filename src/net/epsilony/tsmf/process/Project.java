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
public interface Project {

    List<ProcessPoint> balance();

    List<ProcessPoint> neumann();

    List<ProcessPoint> dirichlet();
}
