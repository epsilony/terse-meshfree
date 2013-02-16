/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.assemblier.SupportLagrange;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.model.LinearLagrangeDirichletProcessor;
import net.epsilony.tsmf.util.synchron.SynchronizedIteratorWrapper;

/**
 *
 * @author epsilon
 */
public class WeakformProcessRunnable implements Runnable {

    WFAssemblier assemblier;
    Mixer mixer;
    LinearLagrangeDirichletProcessor lagProcessor;
    SynchronizedIteratorWrapper<TaskUnit> balanceSynchronizedIterator;
    SynchronizedIteratorWrapper<TaskUnit> neumannSynchronizedIterator;
    SynchronizedIteratorWrapper<TaskUnit> dirichletSynchronizedIterator;
    WeakformProcessRunnerObserver observer;

    public void setObserver(WeakformProcessRunnerObserver observer) {
        this.observer = observer;
    }

    public WeakformProcessRunnable(WFAssemblier assemblier, Mixer mixer,
            LinearLagrangeDirichletProcessor lagProcessor,
            SynchronizedIteratorWrapper<TaskUnit> balanceSynchronizedIterator,
            SynchronizedIteratorWrapper<TaskUnit> neumannSynchronizedIterator,
            SynchronizedIteratorWrapper<TaskUnit> dirichletSynchronizedIterator) {
        this.assemblier = assemblier;
        this.mixer = mixer;
        this.lagProcessor = lagProcessor;
        this.balanceSynchronizedIterator = balanceSynchronizedIterator;
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assemblier instanceof SupportLagrange;
    }

    public void processBalance() {
        final int diffOrder = 1;
        mixer.setDiffOrder(diffOrder);
        while (true) {
            TaskUnit pt = balanceSynchronizedIterator.nextItem();
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
        final int diffOrder = 0;
        mixer.setDiffOrder(diffOrder);

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
        final int diffOrder = 0;
        mixer.setDiffOrder(diffOrder);
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
        processBalance();
        processNeumann();
        processDirichlet();
    }
}
