package mathLib.compile;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;

import java.util.ArrayList;


public class UnitaryDecomp {

    public TwoLevelUnitary twoVectorNormMatrix(Vector<Complex> in) {
        Complex x = in.v(0);
        Complex y = in.v(1);
        //Matrix<Complex> mat = new Matrix<Complex>(Complex.ONE(),2,2);
        TwoLevelUnitary tlu = new TwoLevelUnitary(2,0,x.conjugate(),y.conjugate(),y.negative(),x);
        /*mat.r(x.conjugate(),0,0);
        mat.r(y.conjugate(),0,1);
        mat.r(y.negative(),1,0);
        mat.r(x,1,1);*/
        Complex scale = Complex.ONE().div(in.mag());
        //mat.mult(scale); this line was a bug!
        return tlu.scale(scale);
    }

    public Vector<Complex> getRandomTwoVector() {
       double a0 = Math.random();
       double b0 = Math.random();
       double a1 = Math.random();
       double b1 = Math.random();
       Complex c0 = new Complex(a0,b0);
       Complex c1 = new Complex(a1,b1);
       Vector<Complex> vec = new Vector<Complex>(c0,c1);
       return vec;
    }

    public boolean testTwoVectorNormMatrix() {
        Vector<Complex> test = getRandomTwoVector();
        TwoLevelUnitary testmat = twoVectorNormMatrix(test);

        System.out.println("Test Vector:"); System.out.println(test);
        System.out.println("Computed Matrix:"); System.out.println(testmat);
        System.out.println("Result multiplication: ");
        Vector<Complex> out = testmat.multVec(test);
        System.out.println(out);
        System.out.println(out.v(0).sub(test.mag()).abs());
        System.out.println(test.mag().abs());
        return out.v(0).sub(test.mag()).abs() < 0.1;
    }

    public ArrayList<TwoLevelUnitary> nVectorNormMatrix(Vector<Complex> in) { //suffers numeric instability, solve later
        int n = in.length();
        /*ArrayList<Matrix<Complex>> vs = new ArrayList<>();
        Vector<Complex> partwayDecompVector = in; //Initially the input, will be filled with zeros and returned later
        for(int i = 1; i < n-1; ++i) {
            //Generate v_i
            Matrix<Complex> vi = Matrix.identity(Complex.ONE(),n);
            Vector<Complex> subvector = in.getSlice(n-i-2,n-i,0,0).toVector();
            //subvector is the bottom two non-zero elements of the vector
            Matrix<Complex> submatrix = twoVectorNormMatrix(subvector).getMatrix(); //Creates the matrix to
            vi.r(submatrix.v(0,0),n-i-2,n-i-2); //turn the bottom two nonzero elements
            vi.r(submatrix.v(0,1),n-i-2,n-i-1); //into the sum of their magnitudes
            vi.r(submatrix.v(1,1),n-i-1,n-i-1);
            vi.r(submatrix.v(1,0),n-i-1,n-i-2);
            vs.add(vi);
        }
        return vs; //Reverse*/
        ArrayList<TwoLevelUnitary> unitaries = new ArrayList<>();
        Vector<Complex> partial = in.copy().toVector();
        for(int i = 1; i < n-1; ++i) {
            Vector<Complex> subvector = new Vector<Complex>(partial.v(n-i-1,0),partial.v(n-i-2,0));
            TwoLevelUnitary res = twoVectorNormMatrix(subvector);
            Matrix<Complex> components = res.getMatrix();
            TwoLevelUnitary vi = new TwoLevelUnitary(n,n-i-1,components.v(0,0),components.v(1,0),
                                                                    components.v(0,1),components.v(1,1));
            unitaries.add(vi);
            partial = vi.multVec(partial);
        }
        return unitaries;
    }

    public Vector<Complex> getRandomNVector(int logsize) {
        Vector<Complex> begin = getRandomTwoVector();
        for(int i = 0; i < logsize-1; ++i)
            begin = getRandomTwoVector().kronecker(begin).toVector();
        return begin;
    }

    public boolean testNVectorNormMatrix(int s) {
        Vector<Complex> test = getRandomNVector(s);
        double mag = test.mag().abs();
        ArrayList<TwoLevelUnitary> testmats= nVectorNormMatrix(test);
        for(int i = testmats.size()-1; i >= 0; --i) {
            test = testmats.get(i).multVec(test);
        }
        System.out.println(test);
        System.out.println(mag);
        return test.v(0).abs() - mag < 0.1;
    }

    public ArrayList<TwoLevelUnitary> decompUnitaryTwoLevel(Matrix<Complex> U) {
        Matrix<Complex> Ut = Matrix.map(Complex.ONE(),U, Complex::conjugate).transpose();
        Vector<Complex> currentColumn;
        ArrayList<TwoLevelUnitary> vecNorm = new ArrayList<>();
        for(int i = 0; i < Ut.getColumns(); ++i) {
            currentColumn = Ut.getSlice(0,Ut.getRows(),0,0).toVector();
            vecNorm = nVectorNormMatrix(currentColumn);
            //Multiply out entire arraylist
            TwoLevelUnitary newMat = vecNorm.get(vecNorm.size()-1);
            /*for(int c = vecNorm.size()-2; c >= 0; ++c) {
                newMat = newMat.multVec(vecNorm.get(c));
            }
            newMat = newMat.mult(Ut);
            Ut = newMat.getSlice(i+1,newMat.getRows(),i+1,newMat.getColumns());*/
        }
        return vecNorm;
    }

    public TwoLevelUnitary getRandom2Unitary() {
        double a = Math.random() * Math.PI * 2;
        double p = Math.random() * Math.PI * 2;
        double c = Math.random() * Math.PI * 2;
        double e = Math.random();
        double phi = Math.asin(Math.sqrt(e));
        TwoLevelUnitary tlu = new TwoLevelUnitary(2,0,
                Complex.I().mult(p).exponentiated().mult(Math.cos(phi)),
                Complex.I().mult(c).exponentiated().mult(Math.sin(phi)),
                Complex.I().negative().mult(c).exponentiated().negative().mult(Math.sin(phi)),
                Complex.I().mult(p).exponentiated().mult(Math.cos(phi)));
        return tlu.scale(Complex.I().mult(a).exponentiated());
    }

    public Matrix<Complex> getRandomUnitary(int logsize) {
        return null;
    }

    public boolean testDecompUnitaryTwoLevel() {
        Matrix<Complex> test = getRandomUnitary(2);
        ArrayList<TwoLevelUnitary> twoLevelDecomp = decompUnitaryTwoLevel(test);
        TwoLevelUnitary res = twoLevelDecomp.get(0);
        for(int i = 1; i < twoLevelDecomp.size(); ++i) {
           // res = res.multVec(twoLevelDecomp.get(i));
        }
        //res should equal test
        return test.equals(res); //change to allow epsilon error
    }

}
