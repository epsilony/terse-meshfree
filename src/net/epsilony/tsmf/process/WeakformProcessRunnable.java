/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.assemblier.SupportLagrange;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.util.synchron.SynchronizedIteratorWrapper;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessRunnable implements Runnable {

    public static int DEFAULT_VOLUME_DIFF_ORDER = 1;
    public static int DEFAULT_NEUMANN_DIFF_ORDER = 0;
    public static int DEFAULT_DIRICHLET_DIFF_ORDER = 0;
    WFAssemblier assemblier;
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

    public WeakformProcessRunnable(WFAssemblier assemblier, Mixer mixer,
            LinearLagrangeDirichletProcessor lagProcessor,
            SynchronizedIteratorWrapper<TaskUnit> volumeSynchronizedIterator,
            SynchronizedIteratorWrapper<TaskUnit> neumannSynchronizedIterator,
            SynchronizedIteratorWrapper<TaskUnit> dirichletSynchronizedIterator) {
        this.assemblier = assemblier;
        this.mixer = mixer;
        this.lagProcessor = lagProcessor;
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
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
            assemblier.asmBalance(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
            if (null != observer) {
                observer.balanceProcessed(this);
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
}
