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

    WeakformAssemblier assemblier;
    Mixer mixer;
    LinearLagrangeDirichletProcessor lagProcessor;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeSynchronizedIterator;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannSynchronizedIterator;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletSynchronizedIterator;
    WeakformProcessRunnerObserver observer;

    public void setObserver(WeakformProcessRunnerObserver observer) {
        this.observer = observer;
    }

    public boolean isAssemblyDirichletByLagrange() {
        return lagProcessor != null && assemblier instanceof SupportLagrange;
    }

    public void processVolume() {
        if (null == volumeSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.volumeDiffOrder());
        while (true) {
            WeakformQuadraturePoint pt = volumeSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assemblier.asmVolume(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
            if (null != observer) {
                observer.volumeProcessed(this);
            }
        }
    }

    public void processNeumann() {
        if (null == neumannSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.neumannDiffOrder());
        while (true) {
            WeakformQuadraturePoint pt = neumannSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assemblier.asmNeumann(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
            if (null != observer) {
                observer.neumannProcessed(this);
            }
        }
    }

    public void processDirichlet() {
        if (null == dirichletSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.dirichletDiffOrder());
        boolean lagDiri = isAssemblyDirichletByLagrange();
        while (true) {
            WeakformQuadraturePoint pt = dirichletSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);
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

    public SynchronizedIteratorWrapper<WeakformQuadraturePoint> getVolumeSynchronizedIterator() {
        return volumeSynchronizedIterator;
    }

    public void setVolumeSynchronizedIterator(SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<WeakformQuadraturePoint> getNeumannSynchronizedIterator() {
        return neumannSynchronizedIterator;
    }

    public void setNeumannSynchronizedIterator(SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<WeakformQuadraturePoint> getDirichletSynchronizedIterator() {
        return dirichletSynchronizedIterator;
    }

    public void setDirichletSynchronizedIterator(SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }
}
