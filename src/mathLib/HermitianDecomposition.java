package mathLib;

import Jama.*;
import java.util.*;


public class HermitianDecomposition {
	public static List<Matrix<Complex>> decompose( Matrix<Complex> mat ) {
		boolean debugMode = false;
		
		int rows = mat.getRows();
		int cols = mat.getColumns();
		
		if ( rows != cols ) return null;
		if ( rows == 0 || cols == 0 ) return null;

		/*
		 * if the input Hermitian matrix is A + iB
		 * extract the real-symmetric A and the skew-symmetric B
		 */
		double[][] avals = new double[rows][cols];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				avals[i][j] = mat.v(i, j).getReal();
			}
		}
		Jama.Matrix A = new Jama.Matrix(avals);
		
		double[][] bvals = new double[rows][cols];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				bvals[i][j] = mat.v(i, j).getImaginary();
			}
		}
		Jama.Matrix B = new Jama.Matrix(bvals);
		
		/*
		 * build the real-symmetric matrix M
		 *         [  A   B  ]
		 *    M =  |         |
		 *         [ -B   A  ]
		 */
		int bigrows = 2*rows;
		int bigcols = 2*cols;
		Jama.Matrix M = new Jama.Matrix(bigrows, bigcols);
		M.setMatrix( 0, rows-1, 0, cols-1, A );
		M.setMatrix( rows, bigrows-1, cols, bigcols-1, A );
		M.setMatrix( 0,  rows-1,  cols, bigcols-1, B );
		M.setMatrix( rows,  bigrows-1,  0, cols-1, B.uminus() );
		
		/*
		 * compute the spectral decomposition of M
		 */
		Jama.EigenvalueDecomposition Eig = M.eig();
		Jama.Matrix D = Eig.getD();
		Jama.Matrix V = Eig.getV();

		System.out.println("Jama.D = ");
		D.print(10, 3);
		System.out.println("Jama V = ");
		V.print(10,  3);
		
		/* 
		 * Sanity check: JAMA-style
		 */
		System.out.println("Jama M*V = ");
		M.times(V).print(10, 3);
		System.out.println("Jama V*D = ");
		V.times(D).print(10,  3);
		
	    try {
	    	check(M.times(V),V.times(D));
	        try_success("EigenvalueDecomposition (symmetric)...","");
	    } catch ( java.lang.RuntimeException e ) {
	        try_failure("EigenvalueDecomposition (symmetric)...","incorrect symmetric Eigenvalue decomposition calculation");
	    }

	    /*
	     * prepare list of eigenvalues
	     */
	    double [] eigvals = new double[bigrows];
	    for (int i=0; i<bigrows; ++i) {
	    	eigvals[i] = D.get(i, i);
	    }
	    System.err.println("Eigenvalues = " + Arrays.toString(eigvals));
	    
	    
		/*
		 * mapping back to complex Hermitian
		 */
		Matrix<Complex> DD = new Matrix<>(Complex.ZERO(), rows, cols);
		for (int i=0; i<rows; i++) {
			Complex val = new Complex(D.get(2*i, 2*i), 0.0);
			DD.r( val, i, i );
		}

		Matrix<Complex> VV = new Matrix<>(Complex.ZERO(), rows, cols);
		for (int j=0; j<cols; j++) {
			for (int i=0; i<rows; i++) {
				Complex val = new Complex(V.get(i, 2*j), V.get(rows+i,  2*j));
				VV.r( val, i, j );
			}
		}
		
		// SANITY CHECK: mat * VV = VV * DD
		Matrix<Complex> MV = mat.mult(VV);
		Matrix<Complex> VD = VV.mult(DD);

		if ( !debugMode ) {
			System.err.println("mat = \n" + mat.toString());
			System.err.println("DD = \n" + DD.toString());
			System.err.println("VV = \n" + VV.toString());
			System.err.println("mat * VV = \n" + MV.toString());
			System.err.println("VV * DD = \n" + VD.toString());
		}
		
		if ( withinTolerance( MV, VD, 0.01 )) 
			System.err.println("HermitianDecomposition: ok");
		else
			System.err.println("HermitianDecomposition: fail");
		
		
		List<Matrix<Complex>> boo = new ArrayList<Matrix<Complex>>();
		boo.add(DD);
		boo.add(VV);
				
		return boo;
	}
	
    private static boolean withinTolerance(Matrix<Complex> a, Matrix<Complex> b, double epsilon) {
    	Matrix<Complex> c = a.sub(b);
    	double diff = 0.0;
    	for(int x = 0; x < c.getColumns(); ++x) {
			for(int y = 0; y < c.getRows(); ++y) {
				//Complex cdiff = a.o.op(a.v(y,x)).sub(b.v(y, x));
				diff += c.v(y,x).abs();
			}
		}	
    	
    	System.err.println("Difference = \n" + c.toString());
    	
		return diff < epsilon;
	}
    
    
    /* The code below is ported from JAMA
     * temporarily used for testing 
     */
    
    /** Check magnitude of difference of scalars. **/
    private static void check(double x, double y) {
       double eps = Math.pow(2.0,-52.0);
       if (x == 0 & Math.abs(y) < 10*eps) return;
       if (y == 0 & Math.abs(x) < 10*eps) return;
       if (Math.abs(x-y) > 10*eps*Math.max(Math.abs(x),Math.abs(y))) {
          throw new RuntimeException("The difference x-y is too large: x = " + Double.toString(x) + "  y = " + Double.toString(y));
       }
    }

    /** Check norm of difference of "vectors". **/
    private static void check(double[] x, double[] y) {
       if (x.length == y.length ) {
          for (int i=0;i<x.length;i++) {
             check(x[i],y[i]);
          }
       } else {
          throw new RuntimeException("Attempt to compare vectors of different lengths");
       }
    }

    /** Check norm of difference of arrays. **/
    private static void check(double[][] x, double[][] y) {
       Jama.Matrix A = new Jama.Matrix(x);
       Jama.Matrix B = new Jama.Matrix(y);
       check(A,B);
    }

    /** Check norm of difference of Matrices. **/
    private static void check(Jama.Matrix X, Jama.Matrix Y) {
       double eps = Math.pow(2.0,-52.0);
       if (X.norm1() == 0. & Y.norm1() < 10*eps) return;
       if (Y.norm1() == 0. & X.norm1() < 10*eps) return;
       if (X.minus(Y).norm1() > 1000*eps*Math.max(X.norm1(),Y.norm1())) {
          throw new RuntimeException("The norm of (X-Y) is too large: " +  Double.toString(X.minus(Y).norm1()));
       }
    }

    /** Shorten spelling of print. **/
    private static void print (String s) {
       System.out.print(s);
    }
   
    /** Print appropriate messages for successful outcome try **/
    private static void try_success (String s,String e) {
       print(">    " + s + "success\n");
       if ( e != "" ) {
         print(">      Message: " + e + "\n");
       }
    }
   
    /** Print appropriate messages for unsuccessful outcome try **/
    private static void try_failure (String s,String e) {
       print(">    " + s + "*** failure ***\n>      Message: " + e + "\n");
    }

   /** Print appropriate messages for unsuccessful outcome try **/
    private static void try_warning (String s,String e) {
       print(">    " + s + "*** warning ***\n>      Message: " + e + "\n");
    }

}
