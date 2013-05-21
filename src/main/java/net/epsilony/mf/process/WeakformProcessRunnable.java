/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.mf.process.assemblier.WeakformLagrangeAssemblier;
import net.epsilony.mf.process.assemblier.WeakformAssemblier;
import net.epsilony.tb.synchron.SynchronizedIteratorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessRunnable implements Runnable {

    public static Logger logger = LoggerFactory.getLogger(WeakformProcessRunnable.class);
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
        return lagProcessor != null && assemblier instanceof WeakformLagrangeAssemblier;
    }

    public void processVolume() {
        if (null == volumeSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.getVolumeDiffOrder());
        while (true) {
            WeakformQuadraturePoint pt = volumeSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assemblier.setWeight(pt.weight);
            assemblier.setShapeFunctionValue(mixResult.nodesAssemblyIndes, mixResult.shapeFunctionValueLists);
            assemblier.setLoad(pt.value, null);
            assemblier.assembleVolume();
            if (null != observer) {
                observer.volumeProcessed(this);
            }
        }
    }

    public void processNeumann() {
        if (null == neumannSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.getNeumannDiffOrder());
        while (true) {
            WeakformQuadraturePoint pt = neumannSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);
            assemblier.setWeight(pt.weight);
            assemblier.setShapeFunctionValue(mixResult.nodesAssemblyIndes, mixResult.shapeFunctionValueLists);
            assemblier.setLoad(pt.value, null);
            assemblier.assembleNeumann();
            if (null != observer) {
                observer.neumannProcessed(this);
            }
        }
    }

    public void processDirichlet() {
        if (null == dirichletSynchronizedIterator) {
            return;
        }
        mixer.setDiffOrder(assemblier.getDirichletDiffOrder());
        boolean lagDiri = isAssemblyDirichletByLagrange();
        WeakformLagrangeAssemblier lagAssemblier = null;
        if (lagDiri) {
            lagAssemblier = (WeakformLagrangeAssemblier) assemblier;
        }
        while (true) {
            WeakformQuadraturePoint pt = dirichletSynchronizedIterator.nextItem();
            if (pt == null) {
                break;
            }
            Mixer.MixResult mixResult = mixer.mix(pt.coord, pt.segment);

            assemblier.setWeight(pt.weight);
            assemblier.setShapeFunctionValue(mixResult.nodesAssemblyIndes, mixResult.shapeFunctionValueLists);
            if (null != lagAssemblier) {
                lagProcessor.process(pt);
                lagAssemblier.setLagrangeShapeFunctionValue(
                        lagProcessor.getLagrangleAssemblyIndes(),
                        lagProcessor.getLagrangleShapeFunctionValue());
            }
            assemblier.setLoad(pt.value, pt.mark);
            assemblier.assembleDirichlet();
            if (null != observer) {
                observer.dirichletProcessed(this);
            }
        }
    }

    @Override
    public void run() {
        logger.info("processing with :{}", mixer);
        processVolume();
        logger.info("processed volume");
        processNeumann();
        logger.info("processed neumann");
        processDirichlet();
        logger.info("processed dirichlet");
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

    public void setVolumeSynchronizedIterator(
            SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeSynchronizedIterator) {
        this.volumeSynchronizedIterator = volumeSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<WeakformQuadraturePoint> getNeumannSynchronizedIterator() {
        return neumannSynchronizedIterator;
    }

    public void setNeumannSynchronizedIterator(
            SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannSynchronizedIterator) {
        this.neumannSynchronizedIterator = neumannSynchronizedIterator;
    }

    public SynchronizedIteratorWrapper<WeakformQuadraturePoint> getDirichletSynchronizedIterator() {
        return dirichletSynchronizedIterator;
    }

    public void setDirichletSynchronizedIterator(
            SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletSynchronizedIterator) {
        this.dirichletSynchronizedIterator = dirichletSynchronizedIterator;
    }
}
