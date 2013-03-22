/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.epsilony.tsmf.process.assemblier.SupportLagrange;
import net.epsilony.tsmf.process.assemblier.WeakformAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.influence.ArrayInfluenceDomianRadiusMapperFactory;
import net.epsilony.tsmf.model.influence.InfluenceRadiusCalculator;
import net.epsilony.tsmf.model.influence.InfluenceRadiusMapper;
import net.epsilony.tsmf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.tsmf.model.search.LRTreeSegment2DIntersectingSphereSearcher;
import net.epsilony.tsmf.model.search.SphereSearcher;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.NeedPreparation;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tsmf.util.matrix.ReverseCuthillMcKeeSolver;
import net.epsilony.tsmf.util.synchron.SynchronizedIteratorWrapper;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformProcessor2D implements NeedPreparation {

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
    DenseVector nodesValue;
    private List<WeakformQuadraturePoint> volumeProcessPoints;
    private List<WeakformQuadraturePoint> dirichletProcessPoints;
    private List<WeakformQuadraturePoint> neumannProcessPoints;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> volumeIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> neumannIteratorWrapper;
    SynchronizedIteratorWrapper<WeakformQuadraturePoint> dirichletIteratorWrapper;
    private InfluenceRadiusMapper influenceRadiusMapper;
    SupportDomainSearcherFactory supportDomainSearcherFactory;
    boolean enableMultiThread = DEFAULT_ENABLE_MULTITHREAD;

    public void setup(WeakformProject project) {
        setModel(project.getModel());
        setInfluenceRadiusCalculator(project.getInfluenceRadiusCalculator());
        setWeakformQuadratureTask(project.getWeakformTask());
        setShapeFunction(project.getShapeFunction());
        setAssemblier(project.getAssemblier());
        setConstitutiveLaw(project.getConstitutiveLaw());
    }

    public void process() {
        prepare();
        if (isActuallyMultiThreadable()) {
            multiThreadProcess();
        } else {
            singleThreadProcess();
        }
    }

    private void singleThreadProcess() {

        Mixer mixer = new Mixer(
                shapeFunction, supportDomainSearcherFactory.produce(), influenceRadiusMapper);
        WeakformProcessRunnable runnable = new WeakformProcessRunnable();
        runnable.setAssemblier(assemblier);
        runnable.setMixer(mixer);
        runnable.setLagrangeProcessor(lagProcessor);
        runnable.setVolumeSynchronizedIterator(volumeIteratorWrapper);
        runnable.setDirichletSynchronizedIterator(dirichletIteratorWrapper);
        runnable.setNeumannSynchronizedIterator(neumannIteratorWrapper);
        runnable.run();
    }

    private void multiThreadProcess() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        ArrayList<WeakformAssemblier> assemblierAvators = new ArrayList<>(coreNum);
        assemblierAvators.add(assemblier);
        for (int i = 1; i < coreNum; i++) {
            assemblierAvators.add(assemblier.synchronizeClone());
        }
        ExecutorService executor = Executors.newFixedThreadPool(coreNum);
        for (int i = 0; i < assemblierAvators.size(); i++) {
            Mixer mixer = new Mixer(
                    shapeFunction.synchronizeClone(), supportDomainSearcherFactory.produce(), influenceRadiusMapper);
            WeakformProcessRunnable runnable = new WeakformProcessRunnable();
            runnable.setAssemblier(assemblierAvators.get(i));
            runnable.setMixer(mixer);
            runnable.setLagrangeProcessor(lagProcessor);
            runnable.setVolumeSynchronizedIterator(volumeIteratorWrapper);
            runnable.setDirichletSynchronizedIterator(dirichletIteratorWrapper);
            runnable.setNeumannSynchronizedIterator(neumannIteratorWrapper);
            executor.execute(runnable);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                executor.awaitTermination(1000, TimeUnit.MICROSECONDS);
            } catch (InterruptedException ex) {
                break;
            }
        }
        for (int i = 1; i < assemblierAvators.size(); i++) {
            assemblier.addToMainMatrix(assemblierAvators.get(i));
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
        SphereSearcher<Node> nodesSearcher = new LRTreeNodesSphereSearcher(model.getAllNodes());
        SphereSearcher<Segment2D> segmentSearcher = new LRTreeSegment2DIntersectingSphereSearcher(model.getPolygon());
        supportDomainSearcherFactory = new SupportDomainSearcherFactory(nodesSearcher, segmentSearcher);
        influenceRadiusMapper = new ArrayInfluenceDomianRadiusMapperFactory(
                model, influenceRadiusCalculator,
                supportDomainSearcherFactory.produce())
                .produce();
        supportDomainSearcherFactory.setInfluenceDomainRadiusMapper(influenceRadiusMapper);
        volumeProcessPoints = weakformQuadratureTask.volumeTasks();
        dirichletProcessPoints = weakformQuadratureTask.dirichletTasks();
        neumannProcessPoints = weakformQuadratureTask.neumannTasks();
        volumeIteratorWrapper =
                new SynchronizedIteratorWrapper<>(volumeProcessPoints.iterator());
        neumannIteratorWrapper =
                new SynchronizedIteratorWrapper<>(neumannProcessPoints.iterator());
        dirichletIteratorWrapper =
                new SynchronizedIteratorWrapper<>(dirichletProcessPoints.iterator());
        prepareAssemblier(assemblier);
    }

    void prepareAssemblier(WeakformAssemblier wfAssemblier) {
        wfAssemblier.setConstitutiveLaw(constitutiveLaw);
        wfAssemblier.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= DENSE_MATRIC_SIZE_THRESHOLD;
        wfAssemblier.setMatrixDense(dense);
        if (isAssemblyDirichletByLagrange()) {
            lagProcessor = new LinearLagrangeDirichletProcessor(dirichletProcessPoints, model.getAllNodes().size());
            SupportLagrange sL = (SupportLagrange) wfAssemblier;
            sL.setDirichletNodesNums(lagProcessor.getDirichletNodesSize());
        }
        wfAssemblier.prepare();
    }

    public boolean isAssemblyDirichletByLagrange() {
        return assemblier instanceof SupportLagrange;
    }

    public void solve() {
        Matrix mainMatrix = assemblier.getMainMatrix();
        DenseVector mainVector = assemblier.getMainVector();
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(mainMatrix, assemblier.isUpperSymmertric());
        nodesValue = rcm.solve(mainVector);
    }

    public PostProcessor postProcessor() {
        return new PostProcessor(shapeFunction, supportDomainSearcherFactory.produce(), influenceRadiusMapper, nodesValue);
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

    public static WeakformProcessor2D genTimoshenkoProjectProcess() {
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoStandardTask task = new TimoshenkoStandardTask(timoBeam, quadDomainSize, quadDomainSize, quadDegree);
        WeakformProcessor2D res = new WeakformProcessor2D();
        res.setup(task.processPackage(quadDomainSize, inflRads));
        return res;
    }

    public static void main(String[] args) {
        WeakformProcessor2D process = genTimoshenkoProjectProcess();
        process.process();
        process.solve();
        PostProcessor pp = process.postProcessor();
        pp.value(new double[]{0.1, 0}, null);
    }
}