package mathLib.compile;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;

public class TwoLevelUnitary {

    int size, pos;
    Complex a,b,c,d;

    public TwoLevelUnitary(int size, int pos, Complex a, Complex b, Complex c, Complex d) {
        this.size = size;
        this.pos = pos;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public TwoLevelUnitary(int size, int pos, TwoLevelUnitary tlu) {
        this.size = size;
        this.pos = pos;
        this.a = tlu.a;
        this.b = tlu.b;
        this.c = tlu.c;
        this.d = tlu.d;
    }

    public TwoLevelUnitary scale(Complex s) {
        return new TwoLevelUnitary(size,pos,a.mult(s),b.mult(s),c.mult(s),d.mult(s));
    }

    public Matrix<Complex> getMatrix() {
        Matrix<Complex> mat = Matrix.identity(Complex.ONE(),size);
        mat.r(a,pos,pos); mat.r(b,pos,pos+1);
        mat.r(c,pos+1,pos); mat.r(d,pos+1,pos+1);
        return mat;
    }

    public String toString() {

        String toReturn = "";
        for(int i = 0; i < pos; ++i) {
            int j;
            for(j = 0; j < i; ++j) {
                toReturn += "0 ";
            }
            toReturn += "1 ";
            for(j = i+1; j < size; ++j) {
                toReturn += "0 ";
            }
            toReturn += "\n";
        }
        for(int i = 0; i < pos; ++i) {
            toReturn += "0 ";
        }
        toReturn += a.toString() + " " + b.toString();
        for(int i = pos+2; i < size; ++i) {
            toReturn += "0 ";
        }
        toReturn += "\n";
        for(int i = 0; i < pos; ++i) {
            toReturn += "0 ";
        }
        toReturn += c.toString() + " " + d.toString();
        for(int i = pos+2; i < size; ++i) {
            toReturn += "0 ";
        }
        toReturn += "\n";
        for(int i = pos+2; i < size; ++i) {
            for(int j = 0; j < i; ++j) {
                toReturn += "0 ";
            }
            toReturn += "1 ";
            for(int j = i+1; j < size; ++j) {
                toReturn += "0 ";
            }
            toReturn += "\n";
        }
        return toReturn;
    }

    public Vector<Complex> multVec(Vector<Complex> v) {
        Complex epos = v.v(pos);
        Complex epos1 = v.v(pos+1);
        Vector<Complex> newvec = v.copy().toVector();
        newvec.r(a.mult(epos).add(b.mult(epos1)),pos,0);
        newvec.r(c.mult(epos).add(d.mult(epos1)),pos+1,0);
        return newvec;
    }

}
