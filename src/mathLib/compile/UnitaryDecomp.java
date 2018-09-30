package mathLib.compile;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;

import java.util.ArrayList;


public class UnitaryDecomp {

    public Matrix<Complex> twoVectorNormMatrix(Vector<Complex> in) {
        Complex x = in.v(0);
        Complex y = in.v(1);
        Matrix<Complex> mat = new Matrix<Complex>(Complex.ONE(),2,2);
        mat.r(x.conjugate(),0,0);
        mat.r(y.conjugate(),0,1);
        mat.r(y.negative(),1,0);
        mat.r(x,1,1);
        Complex scale = Complex.ONE().div(in.mag());
        mat.mult(scale);
        return mat;
    }

    public Vector<Complex> getRandomTwoVector() {
       double a0 = Math.random();
       double b0 = Math.random();
       double a1 = Math.random();
       double b1 = Math.random();
       Complex c0 = new Complex(a0,b0);
       Complex c1 = new Complex(a1,b1);
       Vector<Complex> vec = new Vector<Complex>(c0,c1);
       return vec.norm();
    }

    public boolean testTwoVectorNormMatrix() {
        Vector<Complex> test = getRandomTwoVector();
        Matrix<Complex> testmat = twoVectorNormMatrix(test);
        Vector<Complex> out = testmat.mult(test).toVector();
        System.out.println(out);
        System.out.println(test.mag());
        return out.v(0).sub(test.mag()).abs() < 0.1;
    }

    public ArrayList<Matrix<Complex>> nVectorNormMatrix(Vector<Complex> in) { //suffers numeric instability, solve later
        int n = in.length();
        ArrayList<Matrix<Complex>> vs = new ArrayList<>();
        Vector<Complex> partwayDecompVector = in; //Initially the input, will be filled with zeros and returned later
        for(int i = 1; i < n-1; ++i) {
            //Generate v_i
            Matrix<Complex> vi = Matrix.identity(Complex.ONE(),n);
            Vector<Complex> subvector = in.getSlice(n-i-2,n-i,0,0).toVector();
            //subvector is the bottom two non-zero elements of the vector
            Matrix<Complex> submatrix = twoVectorNormMatrix(subvector); //Creates the matrix to
            vi.r(submatrix.v(0,0),n-i-2,n-i-2); //turn the bottom two nonzero elements
            vi.r(submatrix.v(0,1),n-i-2,n-i-1); //into the sum of their magnitudes
            vi.r(submatrix.v(1,1),n-i-1,n-i-1);
            vi.r(submatrix.v(1,0),n-i-1,n-i-2);
            vs.add(vi);
        }
        return vs; //Reverse
    }

    public Vector<Complex> getRandomNVector(int logsize) {
        Vector<Complex> begin = getRandomTwoVector();
        for(int i = 0; i < logsize-1; ++i)
            begin = getRandomTwoVector().kronecker(begin).toVector();
        return begin.norm();
    }

    public boolean testNVectorNormMatrix(int s) {
        Vector<Complex> test = getRandomNVector(s);
        double mag = test.mag().abs();
        ArrayList<Matrix<Complex>> testmats= nVectorNormMatrix(test);
        for(int i = testmats.size()-1; i >= 0; --i) {
            test = testmats.get(i).mult(test).toVector();
        }
        System.out.println(test);
        System.out.println(mag);
        return test.v(0).abs() - mag < 0.1;
    }

    public ArrayList<Matrix<Complex>> decompUnitaryTwoLevel(Matrix<Complex> U) {
        Matrix<Complex> Ut = Matrix.map(Complex.ONE(),U, Complex::conjugate).transpose();
        Vector<Complex> currentColumn;
        ArrayList<Matrix<Complex>> vecNorm = new ArrayList<>();
        for(int i = 0; i < Ut.getColumns(); ++i) {
            currentColumn = Ut.getSlice(0,Ut.getRows(),0,0).toVector();
            vecNorm = nVectorNormMatrix(currentColumn);
            //Multiply out entire arraylist
            Matrix<Complex> newMat = vecNorm.get(vecNorm.size()-1);
            for(int c = vecNorm.size()-2; c >= 0; ++c) {
                newMat = newMat.mult(vecNorm.get(c));
            }
            newMat = newMat.mult(Ut);
            Ut = newMat.getSlice(i+1,newMat.getRows(),i+1,newMat.getColumns());
        }
        return vecNorm;
    }

    public Matrix<Complex> getRandomUnitary() {
        return null;
    }

    public boolean testDecompUnitaryTwoLevel() {
        Matrix<Complex> test = getRandomUnitary();
        ArrayList<Matrix<Complex>> twoLevelDecomp = decompUnitaryTwoLevel(test);
        Matrix<Complex> res = twoLevelDecomp.get(0);
        for(int i = 1; i < twoLevelDecomp.size(); ++i) {
            res = res.mult(twoLevelDecomp.get(i));
        }
        //res should equal test
        return test.equals(res); //change to allow epsilon error
    }

}
