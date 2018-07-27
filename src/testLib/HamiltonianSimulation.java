package testLib;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.MatrixDecomposition;

public class HamiltonianSimulation {

    public static Matrix<Complex> quantumWalk(Matrix<Complex> hamiltonian, double time ) {
        return MatrixDecomposition.map(x -> x.mult(Complex.I().negative().mult(time)).exponentiated(), hamiltonian );
    }

}
