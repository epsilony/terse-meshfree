/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.mf.cons_law.RawConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyWeakformAssemblierTest {

    public MechanicalPenaltyWeakformAssemblierTest() {
    }

    public ConstitutiveLaw sampleConstutiveLaw(final boolean isSym) {
        return new RawConstitutiveLaw(isSym, new DenseMatrix(new double[][]{{11, 12, 0}, {12, 22, 0}, {0, 0, 33}}));
    }

    public TDoubleArrayList[] sampleShapeFuncVals() {
        TDoubleArrayList v = new TDoubleArrayList(new double[]{1.1, 2.0, -3.3, 0.4, 5.2, -6.0});
        TDoubleArrayList v_x = new TDoubleArrayList(new double[]{21.1, 22.0, -23.3, 0.24, 25.2, -2.60});
        TDoubleArrayList v_y = new TDoubleArrayList(new double[]{31.1, 22.0, -23.3, 3.4, 35.2, -36.0});
        return new TDoubleArrayList[]{v, v_x, v_y};
    }

    public MechanicalPenaltyWeakformAssemblier sampleAsm(int nodesSize, double penalty, boolean upperSym) {
        MechanicalPenaltyWeakformAssemblier res = new MechanicalPenaltyWeakformAssemblier(penalty);
        res.setNodesNum(nodesSize);
        res.setConstitutiveLaw(sampleConstutiveLaw(upperSym));
        res.setMatrixDense(true);
        res.prepare();
        return res;
    }

    public TIntArrayList sampleNodeIds() {
        return new TIntArrayList(new int[]{9, 1, 3, 0, 7, 5});
    }
    double[] volumnForce = new double[]{2.7, -3.6};
    int nodesSize = 10;
    double penalty = 1e4;
    TIntArrayList nodesAssemblyIndes = sampleNodeIds();
    TDoubleArrayList[] shapeFuncVals = sampleShapeFuncVals();

    @Test
    public void testVolumeNeumann() {

        double[][] exps = new double[][]{{382.1136, 36.72, 2526.48, 2531.76, 0.0, 0.0, -2675.772, -2681.364, 0.0, 0.0,
                -4046.064, -395.4, 0.0, 0.0, 4015.968, 2928.816, 0.0, 0.0, 3545.124, 2456.988},
            {36.72, 256.2208, 1071.84, 1819.84, 0.0, 0.0, -1135.176, -1927.376, 0.0, 0.0, -391.2, -2713.392, 0.0, 0.0,
                1306.944, 2832.544, 0.0, 0.0, 1107.192, 2493.392},
            {2526.48, 1071.84, 21296.0, 21780.0, 0.0, 0.0, -22554.4, -23067.0, 0.0, 0.0, -26765.2, -11391.6, 0.0, 0.0,
                31653.6, 27588.0, 0.0, 0.0, 27684.8, 23529.0},
            {2531.76, 1819.84, 21780.0, 26620.0, 0.0, 0.0, -23067.0, -28193.0, 0.0, 0.0, -26822.4, -19311.6, 0.0, 0.0,
                32208.0, 35332.0, 0.0, 0.0, 28149.0, 30371.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-2675.772, -1135.176, -22554.4, -23067.0, 0.0, 0.0, 23887.16, 24430.05, 0.0, 0.0, 28346.78, 12064.74, 0.0,
                0.0, -33524.04, -29218.2, 0.0, 0.0, -29320.72, -24919.35},
            {-2681.364, -1927.376, -23067.0, -28193.0, 0.0, 0.0, 24430.05, 29858.95, 0.0, 0.0, 28407.36, 20452.74, 0.0,
                0.0, -34111.2, -37419.8, 0.0, 0.0, -29812.35, -32165.65},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-4046.064, -391.2, -26765.2, -26822.4, 0.0, 0.0, 28346.78, 28407.36, 0.0, 0.0, 42842.36, 4212.0, 0.0, 0.0,
                -42538.32, -31035.84, 0.0, 0.0, -37550.26, -26037.12},
            {-395.4, -2713.392, -11391.6, -19311.6, 0.0, 0.0, 12064.74, 20452.74, 0.0, 0.0, 4212.0, 28735.08, 0.0, 0.0,
                -13906.56, -30040.56, 0.0, 0.0, -11783.58, -26441.58},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {4015.968, 1306.944, 31653.6, 32208.0, 0.0, 0.0, -33524.04, -34111.2, 0.0, 0.0, -42538.32, -13906.56, 0.0,
                0.0, 47873.76, 39916.8, 0.0, 0.0, 41974.68, 33914.4},
            {2928.816, 2832.544, 27588.0, 35332.0, 0.0, 0.0, -29218.2, -37419.8, 0.0, 0.0, -31035.84, -30040.56, 0.0,
                0.0, 39916.8, 48215.2, 0.0, 0.0, 34775.4, 41630.6},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {3545.124, 1107.192, 27684.8, 28149.0, 0.0, 0.0, -29320.72, -29812.35, 0.0, 0.0, -37550.26, -11783.58, 0.0,
                0.0, 41974.68, 34775.4, 0.0, 0.0, 36815.24, 29529.45},
            {2456.988, 2493.392, 23529.0, 30371.0, 0.0, 0.0, -24919.35, -32165.65, 0.0, 0.0, -26037.12, -26441.58, 0.0,
                0.0, 33914.4, 41630.6, 0.0, 0.0, 29529.45, 35970.55}};
        double[] exps_v = new double[]{1.08, -1.44, 5.4, -7.2, 0.0, 0.0, -8.91, 11.88, 0.0, 0.0, -16.2, 21.6, 0.0, 0.0,
            14.04, -18.72, 0.0, 0.0, 2.97, -3.96};

        //test twice for test if any mistake of add and set
        double weight = 0.42;
        for (boolean upperSym : new boolean[]{true, false}) {
            MechanicalPenaltyWeakformAssemblier asm = sampleAsm(nodesSize, penalty, upperSym);
            for (int test = 1; test <= 2; test++) {
                asm.setWeight(weight);
                asm.setShapeFunctionValue(nodesAssemblyIndes, shapeFuncVals);
                asm.setLoad(volumnForce, null);
                asm.assembleVolume();
                Matrix acts = asm.getMainMatrix();
                for (int i = 0; i < acts.numRows(); i++) {
                    for (int j = 0; j < acts.numColumns(); j++) {
                        double act = acts.get(i, j);
                        double exp = exps[i][j] * test * weight;
                        try {
                            assertEquals(exp, act, 1e-7);
                        } catch (Throwable e) {
                            System.out.println(i + " " + j);
                            throw e;
                        }
                    }
                }

                DenseVector act_v = asm.getMainVector();
                for (int i = 0; i < act_v.size(); i++) {
                    assertEquals(exps_v[i] * test * weight, act_v.get(i), 1e-10);
                }
            }
            asm.setWeight(weight);
            asm.setShapeFunctionValue(nodesAssemblyIndes, shapeFuncVals);
            asm.setLoad(volumnForce, null);
            asm.assembleNeumann();
            DenseVector act_v = asm.getMainVector();
            for (int i = 0; i < act_v.size(); i++) {
                assertEquals(exps_v[i] * 3 * weight, act_v.get(i), 1e-10);
            }
        }
    }

    @Test
    public void testDirichlet() {

        double[][] exps = new double[][]{
            {1600.0, 0.0, 8000.0, 0.0, 0.0, 0.0, -13200.0, 0.0, 0.0, 0.0, -24000.0, 0.0, 0.0, 0.0, 20800.0, 0.0, 0.0,
                0.0, 4400.0, 0.0},
            {0.0, 1600.0, 0.0, 8000.0, 0.0, 0.0, 0.0, -13200.0, 0.0, 0.0, 0.0, -24000.0, 0.0, 0.0, 0.0, 20800.0, 0.0,
                0.0, 0.0, 4400.0},
            {8000.0, 0.0, 40000.0, 0.0, 0.0, 0.0, -66000.0, 0.0, 0.0, 0.0, -120000.0, 0.0, 0.0, 0.0, 104000.0, 0.0,
                0.0, 0.0, 22000.0, 0.0},
            {0.0, 8000.0, 0.0, 40000.0, 0.0, 0.0, 0.0, -66000.0, 0.0, 0.0, 0.0, -120000.0, 0.0, 0.0, 0.0, 104000.0,
                0.0, 0.0, 0.0, 22000.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-13200.0, 0.0, -66000.0, 0.0, 0.0, 0.0, 108900.0, 0.0, 0.0, 0.0, 198000.0, 0.0, 0.0, 0.0, -171600.0, 0.0,
                0.0, 0.0, -36300.0, 0.0},
            {0.0, -13200.0, 0.0, -66000.0, 0.0, 0.0, 0.0, 108900.0, 0.0, 0.0, 0.0, 198000.0, 0.0, 0.0, 0.0, -171600.0,
                0.0, 0.0, 0.0, -36300.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-24000.0, 0.0, -120000.0, 0.0, 0.0, 0.0, 198000.0, 0.0, 0.0, 0.0, 360000.0, 0.0, 0.0, 0.0, -312000.0, 0.0,
                0.0, 0.0, -66000.0, 0.0},
            {0.0, -24000.0, 0.0, -120000.0, 0.0, 0.0, 0.0, 198000.0, 0.0, 0.0, 0.0, 360000.0, 0.0, 0.0, 0.0,
                -312000.0, 0.0, 0.0, 0.0, -66000.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {20800.0, 0.0, 104000.0, 0.0, 0.0, 0.0, -171600.0, 0.0, 0.0, 0.0, -312000.0, 0.0, 0.0, 0.0, 270400.0,
                0.0, 0.0, 0.0, 57200.0, 0.0},
            {0.0, 20800.0, 0.0, 104000.0, 0.0, 0.0, 0.0, -171600.0, 0.0, 0.0, 0.0, -312000.0, 0.0, 0.0, 0.0,
                270400.0, 0.0, 0.0, 0.0, 57200.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {4400.0, 0.0, 22000.0, 0.0, 0.0, 0.0, -36300.0, 0.0, 0.0, 0.0, -66000.0, 0.0, 0.0, 0.0, 57200.0, 0.0,
                0.0, 0.0, 12100.0, 0.0},
            {0.0, 4400.0, 0.0, 22000.0, 0.0, 0.0, 0.0, -36300.0, 0.0, 0.0, 0.0, -66000.0, 0.0, 0.0, 0.0,
                57200.0, 0.0, 0.0, 0.0, 12100.0}};

        double[] exps_v = new double[]{10800.0, -14400.0, 54000.0, -72000.0, 0.0, 0.0, -89100.0, 118800.0, 0.0,
            0.0, -162000.0, 216000.0, 0.0, 0.0, 140400.0, -187200.0, 0.0, 0.0, 29700.0, -39600.0};

        //test twice for test if any mistake of add and set
        double weight = 0.42;
        for (boolean upperSym : new boolean[]{true, false}) {
            MechanicalPenaltyWeakformAssemblier asm = sampleAsm(nodesSize, penalty, upperSym);
            for (int test = 1; test <= 2; test++) {
                asm.setWeight(weight);
                asm.setShapeFunctionValue(nodesAssemblyIndes, shapeFuncVals);
                asm.setLoad(volumnForce, new boolean[]{true, true});
                asm.assembleDirichlet();
                Matrix acts = asm.getMainMatrix();
                for (int i = 0; i < acts.numRows(); i++) {
                    for (int j = 0; j < acts.numColumns(); j++) {
                        double act = acts.get(i, j);
                        double exp = exps[i][j] * test * weight;
                        try {
                            assertEquals(exp, act, 1e-7);
                        } catch (Throwable e) {
                            System.out.println(i + " " + j);
                            throw e;
                        }
                    }
                }

                DenseVector act_v = asm.getMainVector();
                for (int i = 0; i < act_v.size(); i++) {
                    assertEquals(exps_v[i] * test * weight, act_v.get(i), 1e-10);
                }
            }
        }
    }
}