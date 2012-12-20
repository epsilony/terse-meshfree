/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.Arrays;
import net.epsilony.tsmf.util.IntIdentity;

/**
 *
 * @author epsilon
 */
public class Node implements IntIdentity {

    public double[] coord;
    public int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Node(double[] coord, boolean copy) {
        if (copy) {
            this.coord = Arrays.copyOf(coord, coord.length);
        } else {
            this.coord = coord;
        }
    }
    
    public Node(double[] coord){
        this.coord=coord;
    }

    public Node(double x, double y) {
        this.coord = new double[]{x, y};
    }

    public Node() {
        this.coord = new double[2];
    }
}
