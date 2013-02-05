/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
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
import net.epsilony.tsmf.model.support_domain.SupportDomainData;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.NeedPreparation;
import net.epsilony.tsmf.util.TimoshenkoAnalyticalBeam2D;
import net.epsilony.tsmf.util.WithDiffOrder;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import net.epsilony.tsmf.util.matrix.ReverseCuthillMcKeeSolver;
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
    private boolean lagDiri;
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
        maxIfluenceRad = influenceRadiusMapper.getMaximumInfluenceRadius();

    }

    public WeakformProcessor2D(WeakformProject pack) {
        this(pack.model, pack.influenceRadCalc, pack.project, pack.shapeFunc, pack.assemblier, pack.constitutiveLaw);
    }

    public void process() {
        prepare();
        processBalance();
        processNeumann();
        processDirichlet();
    }

    public void prepareAssemblier() {
        balanceProcessPoints = weakformTask.balance();
        dirichletProcessPoints = weakformTask.dirichlet();
        neumannProcessPoints = weakformTask.neumann();

        assemblier.setConstitutiveLaw(constitutiveLaw);
        assemblier.setNodesNum(model.getAllNodes().size());
        boolean dense = model.getAllNodes().size() <= DENSE_MATRIC_SIZE_THRESHOLD;
        assemblier.setMatrixDense(dense);
        lagDiri = isAssemblyDirichletByLagrange();
        if (lagDiri) {
            lagProcessor = new LinearLagrangeDirichletProcessor(dirichletProcessPoints, model.getAllNodes().size());
            SupportLagrange sL = (SupportLagrange) assemblier;
            sL.setDirichletNodesNums(lagProcessor.getDirichletNodesSize());
        }
        assemblier.prepare();
    }

    public boolean isAssemblyDirichletByLagrange() {
        return assemblier instanceof SupportLagrange;
    }

    public void processBalance() {
        final int diffOrder = 1;

        List<TaskUnit> points = balanceProcessPoints;

        Mixer mixer = new Mixer();
        mixer.setDiffOrder(diffOrder);

        shapeFunction.setDiffOrder(diffOrder);
        for (TaskUnit pt : points) {
            MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            assemblier.asmBalance(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
        }
    }

    public void processNeumann() {
        final int diffOrder = 0;

        List<TaskUnit> points = neumannProcessPoints;

        Mixer mixer = new Mixer();
        mixer.setDiffOrder(diffOrder);

        for (TaskUnit pt : points) {
            MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            assemblier.asmNeumann(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value);
        }
    }

    public void processDirichlet() {
        final int diffOrder = 0;

        List<TaskUnit> points = dirichletProcessPoints;

        Mixer mixer = new Mixer();
        mixer.setDiffOrder(diffOrder);

        for (TaskUnit pt : points) {
            MixResult mixResult = mixer.mix(pt.coord, pt.seg);
            if (lagDiri) {
                lagProcessor.process(pt, mixResult.nodeIds, mixResult.shapeFunctionValueLists[0]);
            }
            assemblier.asmDirichlet(pt.weight, mixResult.nodeIds, mixResult.shapeFunctionValueLists, pt.value, pt.mark);
        }
    }

    public void solve() {
        Matrix mainMatrix = assemblier.getMainMatrix();
        DenseVector mainVector = assemblier.getMainVector();
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(mainMatrix, assemblier.isUpperSymmertric());
        nodesValue = rcm.solve(mainVector);
    }

    void fromNodesToIdsCoordsInfRads(
            Collection<? extends Node> nodes,
            TIntArrayList ids,
            ArrayList<double[]> coords,
            TDoubleArrayList infRads) {
        coords.clear();
        coords.ensureCapacity(nodes.size());
        ids.resetQuick();
        ids.ensureCapacity(nodes.size());
        infRads.resetQuick();
        infRads.ensureCapacity(nodes.size());
        for (Node nd : nodes) {
            coords.add(nd.coord);
            ids.add(nd.getId());
            infRads.add(influenceRadiusMapper.getInfluenceRadius(nd));
        }
    }

    @Override
    public void prepare() {
        prepareAssemblier();
    }

    public static class MixResult {

        public TDoubleArrayList[] shapeFunctionValueLists;
        public TIntArrayList nodeIds;

        public MixResult(TDoubleArrayList[] shapeFunctionValueLists, TIntArrayList nodeIds) {
            this.shapeFunctionValueLists = shapeFunctionValueLists;
            this.nodeIds = nodeIds;
        }
    }

    public class Mixer implements WithDiffOrder {

        ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CAPACITY);
        TIntArrayList nodesIds = new TIntArrayList(DEFAULT_CAPACITY, -1);
        TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CAPACITY);
        SupportDomainSearcher supportDomainSearcher;

        public Mixer() {
            shapeFunction.setDiffOrder(0);
            supportDomainSearcher = supportDomainSearcherFactory.produce();
        }

        public MixResult mix(double[] center, Segment2D bnd) {
            SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, maxIfluenceRad);

            if (SUPPORT_COMPLEX_CRITERION) {
                throw new UnsupportedOperationException();
            }

            fromNodesToIdsCoordsInfRads(searchResult.visibleNodes, nodesIds, coords, infRads);
            TDoubleArrayList[] shapeFunctionValueLists = shapeFunction.values(center, coords, infRads, null);
            return new MixResult(shapeFunctionValueLists, nodesIds);
        }

        @Override
        public int getDiffOrder() {
            return shapeFunction.getDiffOrder();
        }

        @Override
        public void setDiffOrder(int diffOrder) {
            shapeFunction.setDiffOrder(diffOrder);
        }
    }

    public class PostProcessor extends Mixer {

        public double[] value(double[] center, Segment2D bnd) {
            MixResult mixResult = mix(center, bnd);
            double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * 2];

            for (int i = 0; i < mixResult.nodeIds.size(); i++) {
                int nodeId = mixResult.nodeIds.getQuick(i);
                double nu = nodesValue.get(nodeId * 2);
                double nv = nodesValue.get(nodeId * 2 + 1);
                for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                    double sv = mixResult.shapeFunctionValueLists[j].get(i);
                    output[j * 2] += nu * sv;
                    output[j * 2 + 1] += nv * sv;
                }
            }
            return output;
        }
    }

    public PostProcessor postProcessor() {
        return new PostProcessor();
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
        process.prepare();
        process.processBalance();
        process.processDirichlet();
        process.processNeumann();
        process.solve();
        PostProcessor pp = process.new PostProcessor();
        pp.value(new double[]{0.1, 0}, null);
    }
}