/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import java.util.List;
import net.epsilony.tsmf.assemblier.SupportLagrange;
import net.epsilony.tsmf.assemblier.WFAssemblier;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.model.LinearLagrangeDirichletProcessor;
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

    public static final int DEFAULT_CAPACITY = 60;
    public static final int DENSE_MATRIC_SIZE_THRESHOLD = 200;
    WeakformTask weakformTask;
    Model2D model;
    ShapeFunction shapeFunction;
    WFAssemblier assemblier;
    InfluenceRadiusCalculator inflRadCalc;
    double maxIfluenceRad;
    public static final boolean SUPPORT_COMPLEX_CRITERION = false;
    LinearLagrangeDirichletProcessor lagProcessor;
    ConstitutiveLaw constitutiveLaw;
    DenseVector nodesValue;
    private List<TaskUnit> balanceProcessPoints;
    private List<TaskUnit> dirichletProcessPoints;
    private List<TaskUnit> neumannProcessPoints;
    SynchronizedIteratorWrapper<TaskUnit> balanceIteratorWrapper;
    SynchronizedIteratorWrapper<TaskUnit> neumannIteratorWrapper;
    SynchronizedIteratorWrapper<TaskUnit> dirichletIteratorWrapper;
    private InfluenceRadiusMapper influenceRadiusMapper;
    private SphereSearcher<Node> nodesSearcher;
    private SphereSearcher<Segment2D> segmentSearcher;
    SupportDomainSearcherFactory supportDomainSearcherFactory;

    public WeakformProcessor2D(
            Model2D model,
            InfluenceRadiusCalculator inflRadCalc,
            WeakformTask project,
            ShapeFunction shapeFunction,
            WFAssemblier assemblier,
            ConstitutiveLaw constitutiveLaw) {
        this.model = model;
        this.shapeFunction = shapeFunction;
        this.assemblier = assemblier;
        this.inflRadCalc = inflRadCalc;
        this.weakformTask = project;
        this.constitutiveLaw = constitutiveLaw;
        nodesSearcher = new LRTreeNodesSphereSearcher(model.getAllNodes());
        segmentSearcher = new LRTreeSegment2DIntersectingSphereSearcher(model.getPolygon());
        supportDomainSearcherFactory = new SupportDomainSearcherFactory(nodesSearcher, segmentSearcher);
        influenceRadiusMapper = new ArrayInfluenceDomianRadiusMapperFactory(
                model,
                inflRadCalc,
                supportDomainSearcherFactory.produce())
                .produce();
        supportDomainSearcherFactory.setInfluenceDomainRadiusMapper(influenceRadiusMapper);
    }

    public WeakformProcessor2D(WeakformProject pack) {
        this(pack.model, pack.influenceRadCalc, pack.project, pack.shapeFunc, pack.assemblier, pack.constitutiveLaw);
    }

    public void process() {
        prepare();
        Mixer mixer = new Mixer(
                shapeFunction, supportDomainSearcherFactory.produce(), influenceRadiusMapper);
        WeakformProcessRunnable runnable = new WeakformProcessRunnable(assemblier, mixer, shapeFunction, lagProcessor,
                balanceIteratorWrapper, neumannIteratorWrapper, dirichletIteratorWrapper);
        runnable.run();
    }

    @Override
    public void prepare() {
        balanceProcessPoints = weakformTask.balance();
        dirichletProcessPoints = weakformTask.dirichlet();
        neumannProcessPoints = weakformTask.neumann();
        balanceIteratorWrapper =
                new SynchronizedIteratorWrapper<>(balanceProcessPoints.iterator());
        neumannIteratorWrapper =
                new SynchronizedIteratorWrapper<>(neumannProcessPoints.iterator());
        dirichletIteratorWrapper =
                new SynchronizedIteratorWrapper<>(dirichletProcessPoints.iterator());
        prepareAssemblier(assemblier);
    }

    void prepareAssemblier(WFAssemblier wfAssemblier) {
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

    public static WeakformProcessor2D genTimoshenkoProjectProcess() {
        TimoshenkoAnalyticalBeam2D timoBeam = new TimoshenkoAnalyticalBeam2D(48, 12, 3e7, 0.3, -1000);
        int quadDomainSize = 2;
        int quadDegree = 4;
        double inflRads = quadDomainSize * 4.1;
        TimoshenkoStandardTask tProject = new TimoshenkoStandardTask(timoBeam, quadDomainSize, quadDomainSize, quadDegree);
        WeakformProcessor2D res = new WeakformProcessor2D(tProject.processPackage(quadDomainSize, inflRads));
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