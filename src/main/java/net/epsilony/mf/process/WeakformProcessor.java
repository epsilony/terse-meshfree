/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.mf.process.assemblier.WeakformLagrangeAssemblier;
import net.epsilony.mf.process.assemblier.WeakformAssemblier;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.model.Model2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tb.shape_func.ShapeFunction;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.mf.model.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tb.matrix.ReverseCuthillMcKeeSolver;
import net.epsilony.tb.synchron.SynchronizedIteratorWrapper;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessor implements NeedPreparation {

    public static final Logger logger = LoggerFactory.getLogger(WeakformProcessor.class);
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    public static final boolean DEFAULT_ENABLE_MULTITHREAD = true;
    WeakformQuadratureTask weakformQuadratureTask;
    InfluenceRadiusCalculator influenceRadiusCalculator;
    Model2D model;
    ShapeFunction shapeFunction;
    WeakformAssemblier assemblier;
    LinearLagrangeDirichletProcessor lagProcessor;
    ConstitutiveLaw constitutiveLaw;
    private List<WeakformQuadraturePoint> volumeProcessPoints;
    private List<WeakformQuadraturePoint> dirichletProcessPoints;
    private List<WeakformQuadraturePoint> neumannProcessPoints;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletIteratorWrapper;
    IntIdentityMap<Node, ProcessNodeData> nodesProcessDataMap;
    SupportDomainSearcherFactory supportDomainSearcherFactory;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;

    public void setup(WeakformProject project) {
        setModel(project.getModel());
        setInfluenceRadiusCalculator(project.getInfluenceRadiusCalculator());
        setWeakformQuadratureTask(project.getWeakformQuadratureTask());
        setShapeFunction(project.getShapeFunction());
        setAssemblier(project.getAssemblier());
        setConstitutiveLaw(project.getConstitutiveLaw());
    }

    public void process() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        if (!enableMultiThread) {
            coreNum = 1;
        }

        ArrayList<WeakformAssemblier> assemblierAvators = new ArrayList<>(coreNum);
        assemblierAvators.add(assemblier);
        for (int i = 1; i < coreNum; i++) {
            assemblierAvators.add(assemblier.synchronizeClone());
        }
        ExecutorService executor = Executors.newFixedThreadPool(coreNum);
        for (int i = 0; i < assemblierAvators.size(); i++) {
            Mixer mixer = new Mixer();
            mixer.setShapeFunction(shapeFunction.synchronizeClone());
            mixer.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
            mixer.setNodesProcessDatasMap(nodesProcessDataMap);
            WeakformProcessRunnable runnable = new WeakformProcessRunnable();
            runnable.setAssemblier(assemblierAvators.get(i));
            runnable.setMixer(mixer);
            runnable.setLagrangeProcessor(lagProcessor.synchronizeClone());
            runnable.setVolumeSynchronizedIterator(volumeIteratorWrapper);
            runnable.setDirichletSynchronizedIterator(dirichletIteratorWrapper);
            runnable.setNeumannSynchronizedIterator(neumannIteratorWrapper);
            executor.execute(runnable);
            logger.info("execute {}", runnable);
        }
        logger.info("Processing with {} threads", coreNum);

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException ex) {
                logger.error("Processing interrupted {}", ex);
                break;
            }
        }

        for (int i = 1; i < assemblierAvators.size(); i++) {
            assemblier.mergeWithBrother(assemblierAvators.get(i));
        }
    }

    public boolean isActuallyMultiThreadable() {
        if (!isEnableMultiThread()) {
            return false;
        }
        int coreNum = Runtime.getRuntime().availableProcessors();
        if (coreNum <= 1) {
            return false;
        }
        return true;
    }

    public boolean isEnableMultiThread() {
        return enableMultiThread;
    }

    public void setEnableMultiThread(boolean enableMultiThread) {
        this.enableMultiThread = enableMultiThread;
    }

    @Override
    public void prepare() {
        prepareProcessIteratorWrappers();

        prepareSupportDomainSearcherFactoryWithoutInfluenceRadiusFilter();

        prepareProcessNodesDatas();

        supportDomainSearcherFactory.setNodesProcessDatasMap(nodesProcessDataMap);

        prepareAssemblier();
    }

    private void prepareProcessIteratorWrappers() {
        volumeProcessPoints = weakformQuadratureTask.volumeTasks();
        dirichletProcessPoints = weakformQuadratureTask.dirichletTasks();
        neumannProcessPoints = weakformQuadratureTask.neumannTasks();
        volumeIteratorWrapper = volumeProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(volumeProcessPoints.iterator());
        neumannIteratorWrapper = neumannProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(neumannProcessPoints.iterator());
        dirichletIteratorWrapper = dirichletProcessPoints == null ? null
                : new SynchronizedIteratorWrapper<>(dirichletProcessPoints.iterator());
    }

    private void prepareSupportDomainSearcherFactoryWithoutInfluenceRadiusFilter() {
        supportDomainSearcherFactory = new SupportDomainSearcherFactory();
        supportDomainSearcherFactory.getNodesSearcher().setAll(model.getAllNodes());
        if (null != model.getPolygon()) {
            supportDomainSearcherFactory.getSegmentsSearcher().setAll(model.getPolygon().getSegments());
        } else {
            supportDomainSearcherFactory.setSegmentsSearcher(null);
        }
    }

    private void prepareProcessNodesDatas() {
        nodesProcessDataMap = new IntIdentityMap<>();
        nodesProcessDataMap.appendNullValues(model.getAllNodes().size());
        int index = 0;
        influenceRadiusCalculator.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        for (Node nd : model.getSpaceNodes()) {
            double rad = influenceRadiusCalculator.calcInflucenceRadius(nd, null);
            ProcessNodeData data = new ProcessNodeData();
            data.setInfluenceRadius(rad);
            data.setAssemblyIndex(index++);
            nodesProcessDataMap.put(nd, data);
        }

        if (null != model.getPolygon()) {
            for (Segment2D seg : model.getPolygon()) {
                Node nd = seg.getHead();
                double rad = influenceRadiusCalculator.calcInflucenceRadius(nd, seg);
                ProcessNodeData data = new ProcessNodeData();
                data.setInfluenceRadius(rad);
                data.setAssemblyIndex(index++);
                nodesProcessDataMap.put(nd, data);
            }
        }

        if (isAssemblyDirichletByLagrange()) {
            for (WeakformQuadraturePoint qp : dirichletProcessPoints) {
                Node node = qp.segment.getHead();
                ProcessNodeData nodeData = nodesProcessDataMap.get(node);
                for (int i = 0; i < 2; i++) {
                    if (null != nodeData) {
                        if (nodeData.getLagrangeAssemblyIndex() < 0) {
                            nodeData.setLagrangeAssemblyIndex(index++);
                        }
                    } else {
                        node.setId(IntIdentityMap.NULL_INDEX_SUPREMUM);
                        ProcessNodeData newData = new ProcessNodeData();
                        newData.setLagrangeAssemblyIndex(index++);
                        nodesProcessDataMap.put(node, newData);
                    }
                    node = qp.segment.getRear();
                    nodeData = nodesProcessDataMap.get(node);
                }
            }
        }
    }

    void prepareAssemblier() {
        if (null != constitutiveLaw) {
            assemblier.setConstitutiveLaw(constitutiveLaw);
        }
        assemblier.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= DENSE_MATRIC_SIZE_THRESHOLD;
        assemblier.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor(nodesProcessDataMap);
            WeakformLagrangeAssemblier sL = (WeakformLagrangeAssemblier) assemblier;
            sL.setDirichletNodesNum(lagProcessor.getDirichletNodesSize());
        }
        assemblier.prepare();
        logger.info(
                "prepared assemblier: {}",
                assemblier);
    }

    public boolean isAssemblyDirichletByLagrange() {
        return assemblier instanceof WeakformLagrangeAssemblier;
    }

    public void solve() {
        Matrix mainMatrix = assemblier.getMainMatrix();
        DenseVector mainVector = assemblier.getMainVector();
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(mainMatrix, assemblier.isUpperSymmertric());
        logger.info("solving main matrix:{}, bandwidth ori/opt: {}/{}",
                rcm,
                rcm.getOriginalBandWidth(),
                rcm.getOptimizedBandWidth());
        DenseVector nodesValue = rcm.solve(mainVector);
        logger.info("solved main matrix");
        int nodeValueDimension = getNodeValueDimension();
        for (ProcessNodeData nodeData : nodesProcessDataMap) {

            int nodeValueIndex = nodeData.getAssemblyIndex() * nodeValueDimension;
            if (nodeValueIndex >= 0) {
                double[] nodeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    nodeValue[i] = nodesValue.get(i + nodeValueIndex);
                    nodeData.setValue(nodeValue);
                }
            }

            int lagrangeValueIndex = nodeData.getLagrangeAssemblyIndex() * nodeValueDimension;
            if (lagrangeValueIndex >= 0) {
                double[] lagrangeValue = new double[nodeValueDimension];
                for (int i = 0; i < nodeValueDimension; i++) {
                    lagrangeValue[i] = nodesValue.get(i + lagrangeValueIndex);
                    nodeData.setLagrangleValue(lagrangeValue);
                }
            }
        }
        logger.info("filled nodes values to nodes processor data map");
    }

    public PostProcessor postProcessor() {
        PostProcessor result = new PostProcessor();
        result.setShapeFunction(shapeFunction.synchronizeClone());
        result.setNodeValueDimension(getNodeValueDimension());
        result.setSupportDomainSearcher(supportDomainSearcherFactory.produce());
        result.setNodesProcessDatasMap(nodesProcessDataMap);
        return result;
    }

    public WeakformQuadratureTask getWeakformQuadratureTask() {
        return weakformQuadratureTask;
    }

    public void setWeakformQuadratureTask(WeakformQuadratureTask weakformQuadratureTask) {
        this.weakformQuadratureTask = weakformQuadratureTask;
    }

    public Model2D getModel() {
        return model;
    }

    public void setModel(Model2D model) {
        this.model = model;
    }

    public ShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(ShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
    }

    public WeakformAssemblier getAssemblier() {
        return assemblier;
    }

    public void setAssemblier(WeakformAssemblier assemblier) {
        this.assemblier = assemblier;
    }

    public ConstitutiveLaw getConstitutiveLaw() {
        return constitutiveLaw;
    }

    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    public InfluenceRadiusCalculator getInfluenceRadiusCalculator() {
        return influenceRadiusCalculator;
    }

    public void setInfluenceRadiusCalculator(InfluenceRadiusCalculator influenceRadiusCalculator) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
    }

    public static WeakformProcessor genTimoshenkoProjectProcess() {
        TimoshenkoAnalyticalBeam2D timoBeam =
                new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoStandardTask task =
                new TimoshenkoStandardTask(timoBeam, quadDomainSize, quadDomainSize, quadDegree);
        WeakformProcessor res = new WeakformProcessor();
        res.setup(task.processPackage(quadDomainSize, inflRads));
        return res;
    }

    public static void main(String[] args) {
        WeakformProcessor process = genTimoshenkoProjectProcess();
        process.prepare();
        process.process();
        process.solve();
        PostProcessor pp = process.postProcessor();
        pp.value(new double[]{0.1, 0}, null);
    }

    public int getNodeValueDimension() {
        return assemblier.getNodeValueDimension();
    }
}