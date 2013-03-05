/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import java.util.Arrays;
import net.epsilony.tsmf.util.IntIdentity;
import net.epsilony.tsmf.util.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Node implements IntIdentity, WithPair<double[], Node> {

    public double[] coord;
    public int id;
    protected Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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

    public Node(double[] coord) {
        this.coord = coord;
    }

    public Node(double x, double y) {
        this.coord = new double[]{x, y};
    }

    public Node() {
        this.coord = new double[2];
    }

    @Override
    public double[] getKey() {
        return coord;
    }

    @Override
    public Node getValue() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("Node(%d)%s", id, Arrays.toString(coord));
    }
}
