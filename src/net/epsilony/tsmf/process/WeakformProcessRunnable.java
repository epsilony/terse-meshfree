/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.process.assemblier.SupportLagrange;
import net.epsilony.tsmf.process.assemblier.WeakformAssemblier;
import net.epsilony.tsmf.util.synchron.SynchronizedIteratorWrapper;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessRunnable implements Runnable {

    public static int DEFAULT_VOLUME_DIFF_ORDER = 1;
    public static int DEFAULT_NEUMANN_DIFF_ORDER = 0;
    public static int DEFAULT_DIRICHLET_DIFF_ORDER = 0;
    WeakformAssemblier assemblier;
    Mixer mixer;
    LinearLagrangeDirichletProcessor lagProcessor;
    SynchronizedIteratorWrapper<TaskUnit> volumeSynchronizedIterator;
    SynchronizedIteratorWrapper<TaskUnit> neumannSynchronizedIterator;
    SynchronizedIteratorWrapper<TaskUnit> dirichletSynchronizedIterator;
    WeakformProcessRunnerObserver observer;
    int volumeDiffOrder = DEFAULT_VOLUME_DIFF_ORDER;
    int neumannDiffOrder = DEFAULT_NEUMANN_DIFF_ORDER;
    int dirichletDiffOrder = DEFAULT_DIRICHLET_DIFF_ORDER;

    public void setObserver(WeakformProcessRunnerObserver observer) {
        this.observer = observer;
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assemblier instanceof SupportLagrange;
    }

    public void processVolume() {
        mixer.setDiffOrder(volumeDiffOrder);
        while (true) {
            TaskUnit pt = volumeSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            assemblier.asmVolume(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
            if (null != observer) {
                observer.volumeProcessed(this);
            }
        }
    }

    public void processNeumann() {
        mixer.setDiffOrder(neumannDiffOrder);
        while (true) {
            TaskUnit pt = neumannSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            assemblier.asmNeumann(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
            if (null != observer) {
                observer.neumannProcessed(this);
            }
        }
    }

    public void processDirichlet() {
        mixer.setDiffOrder(dirichletDiffOrder);
        boolean lagDiri = isAssemblyDirichletByLagrange();
        while (true) {
            TaskUnit pt = dirichletSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            if (lagDiri) {
                lagProcessor.process(pt, mixResult.nodeIds, mixResult.shapeFunctionValueLists[0]);
            }
            assemblier.asmDirichlet(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value, pt.mark);
            if (null != observer) {
                observer.dirichletProcessed(this);
            }
        }
    }

    @Override
    public void run() {
        processVolume();
        processNeumann();
        processDirichlet();
    }

    public WeakformAssemblier getAssemblier() {
        return assemblier;
    }

    public void setAssemblier(WeakformAssemblier assemblier) {
        this.assemblier = assemblier;
    }

    public Mixer getMixer() {
        return mixer;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public LinearLagrangeDirichletProcessor getLagProcessor() {
        return lagProcessor;
    }

    public void setLagrangeProcessor(LinearLagrangeDirichletProcessor lagProcessor) {
        this.lagProcessor = lagProcessor;
    }

    public SynchronizedIteratorWrapper<TaskUnit> getVolumeSynchronizedIterator() {
        return volumeSynchronizedIterator;
    }

    public void setVolumeSynchronizedIterator(SynchronizedIteratorWrapper<TaskUnit> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<TaskUnit> getNeumannSynchronizedIterator() {
        return neumannSynchronizedIterator;
    }

    public void setNeumannSynchronizedIterator(SynchronizedIteratorWrapper<TaskUnit> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<TaskUnit> getDirichletSynchronizedIterator() {
        return dirichletSynchronizedIterator;
    }

    public void setDirichletSynchronizedIterator(SynchronizedIteratorWrapper<TaskUnit> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    public int getVolumeDiffOrder() {
        return volumeDiffOrder;
    }

    public void setVolumeDiffOrder(int volumeDiffOrder) {
        this.volumeDiffOrder = volumeDiffOrder;
    }

    public int getNeumannDiffOrder() {
        return neumannDiffOrder;
    }

    public void setNeumannDiffOrder(int neumannDiffOrder) {
        this.neumannDiffOrder = neumannDiffOrder;
    }

    public int getDirichletDiffOrder() {
        return dirichletDiffOrder;
    }

    public void setDirichletDiffOrder(int dirichletDiffOrder) {
        this.dirichletDiffOrder = dirichletDiffOrder;
    }
}
