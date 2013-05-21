/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.tb.nlopt.demo;

import static net.epsilony.tb.nlopt.NloptLibrary.*;
import static java.lang.Math.*;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NloptLibraryDemo {

    public static void main(String[] args) {
        NloptOpt opt = nloptCreate(NloptAlgorithm.NLOPT_LD_MMA, 2);
        Pointer<Byte> nloptAlgorithmName = nloptAlgorithmName(NloptAlgorithm.NLOPT_LD_MMA);
        System.out.println(nloptAlgorithmName.getCString());

        NloptFunc func = new NloptFunc() {
            @Override
            public double apply(int n, Pointer<Double> x, Pointer<Double> gradient, Pointer<?> func_data) {
                if (gradient != Pointer.NULL) {
                    gradient.setDoubleAtIndex(0, 0);
                    gradient.setDoubleAtIndex(1, 0.5 / sqrt(x.getDoubleAtIndex(1)));
                }
                return sqrt(x.getDoubleAtIndex(1));
            }
        };

        NloptFunc constraint = new NloptFunc() {
            @Override
            public double apply(int n, Pointer<Double> x, Pointer<Double> gradient, Pointer<?> func_data) {
                Pointer<Double> myFuncData = (Pointer<Double>) func_data;
                double a = myFuncData.getDoubleAtIndex(0);
                double b = myFuncData.getDoubleAtIndex(1);
                double t = a * x.getDoubleAtIndex(0) + b;
                if (gradient != Pointer.NULL) {
                    gradient.setDoubleAtIndex(0, 3 * a * t * t);
                    gradient.setDoubleAtIndex(1, -1.0);
                }
                return (t * t * t - x.getDoubleAtIndex(1));
            }
        };
        Pointer<Double> lowerBounds = Pointer.pointerToDoubles(Double.NEGATIVE_INFINITY, 0);
        nloptSetLowerBounds(opt, lowerBounds);
        nloptSetMinObjective(opt, Pointer.pointerTo(func), Pointer.NULL);
        nloptAddInequalityConstraint(opt, Pointer.pointerTo(constraint), Pointer.pointerToDoubles(2, 0), 1e-8);
        nloptAddInequalityConstraint(opt, Pointer.pointerTo(constraint), Pointer.pointerToDoubles(-1, 1), 1e-8);
        nloptSetXtolRel(opt, 1e-4);

        Pointer<Double> start = Pointer.pointerToDoubles(1.234, 5.678);
        Pointer<Double> objValue = Pointer.pointerToDouble(0);
        IntValuedEnum<NloptResult> nloptOptimize = nloptOptimize(opt, start, objValue);
        System.out.println("nloptOptimize = " + nloptOptimize);
        System.out.println("objValue = " + objValue);
        System.out.println(objValue.getDouble());
        nloptDestroy(opt);
    }
}
