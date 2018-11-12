package Simulator;

import mathLib.Complex;
import mathLib.Matrix;

public class POVM {

    private Operation operation;

    public POVM(Operation o) {
        this.operation = o;
    }

    public static POVM computationalBasis() {
        Matrix<Complex> E1 = Qubit.ZERO().outerProduct(Qubit.ZERO());
        Matrix<Complex> E2 = Qubit.ONE().outerProduct(Qubit.ONE());
        Operation o = new Operation(E1,E2);
        return new POVM(o);
    }

    public Matrix<Complex> measure(Matrix<Complex> density) {
        return operation.operateND(density);
    }

}
