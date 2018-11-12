package Simulator;

import mathLib.Complex;
import mathLib.Eigenspace;
import mathLib.Matrix;

import java.util.ArrayList;
import java.util.Collections;

public class Operation {


    int size;
    ArrayList<Matrix<Complex>> matrices;

    public Operation(ArrayList<Matrix<Complex>> matrices) {
        this.matrices = matrices;
        this.size = matrices.get(0).getRows();
    }

    public Operation(Matrix<Complex>... matrices) {
        this.matrices = new ArrayList<>();
        for(int i = 0; i < matrices.length; ++i) {
            this.matrices.add(matrices[i]);
        }
        this.size = this.matrices.get(0).getRows();
    }

    private boolean checksum() {
        Matrix<Complex> acc = new Matrix<Complex>(Complex.ZERO(),size,size);
        for(Matrix m : matrices) {
            acc = acc.add(m);
        }
        //check if identity
        Matrix<Complex> test = acc.sub(Matrix.identity(Complex.ZERO(),size));
        //sum all elements
        Complex sum = Complex.ZERO();
        for(int y = 0; y < size;++y) {
            for(int x = 0; x < size; ++x) {
                sum = sum.add(test.v(x,y));
            }
        }
        return sum.abs() < 0.01;
    }

    public Matrix<Complex> operateND(Matrix<Complex> density) {
        Matrix<Complex> acc = new Matrix<Complex>(Complex.ZERO(),density.getRows(),density.getColumns());
        for(Matrix<Complex> k : matrices) {
            Matrix<Complex> i = k.mult(density).mult(Eigenspace.conjugateTranspose(k));
            acc = acc.add(i).div(k.mult(density).trace());
        }
        return acc;
    }

    public Matrix<Complex> operateD(Matrix<Complex> density) {
        double prob = 1;
        int idx = 0;
        Matrix<Complex> Ei = null;
        double tempProb = -1;
        ArrayList<Matrix<Complex>> shuffled = new ArrayList<>();
        Collections.copy(shuffled,matrices);
        Collections.shuffle(shuffled);
        while(prob > 0) {
            Ei = shuffled.get(idx++);
            tempProb = Ei.mult(density).trace().abs();
            prob -= tempProb;
        }
        return Ei.mult(density).mult(Eigenspace.conjugateTranspose(Ei)).div(new Complex(tempProb,0));
    }

}
