/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ProcessNodeData {

    double influenceRadius;
    int assemblyIndex = -1;
    int lagrangeAssemblyIndex = -1;
    double[] value;
    double[] lagrangleValue;

    public double getInfluenceRadius() {
        return influenceRadius;
    }

    public void setInfluenceRadius(double influenceRadius) {
        this.influenceRadius = influenceRadius;
    }

    public int getAssemblyIndex() {
        return assemblyIndex;
    }

    public void setAssemblyIndex(int assemblyIndex) {
        this.assemblyIndex = assemblyIndex;
    }

    public int getLagrangeAssemblyIndex() {
        return lagrangeAssemblyIndex;
    }

    public void setLagrangeAssemblyIndex(int lagrangeAssemblyIndex) {
        this.lagrangeAssemblyIndex = lagrangeAssemblyIndex;
    }

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }

    public double[] getLagrangleValue() {
        return lagrangleValue;
    }

    public void setLagrangleValue(double[] lagrangleValue) {
        this.lagrangleValue = lagrangleValue;
    }
}
