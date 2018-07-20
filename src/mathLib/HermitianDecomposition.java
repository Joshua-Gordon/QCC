package mathLib;

import Jama.*;
import java.util.*;


public class HermitianDecomposition {
	
	/** eigh
	 *   computes the spectral decomposition of a hermitian matrix (Matlab style)
	 *   uses decompose() method to build the orthogonal projections onto distinct eigenspaces
	 *   
	 * @param mat
	 * @return a list where each item is a pair of (eigenvalue, eigenprojector)
	 */
	public static List<Eigenspace> eigh( Matrix<Complex> mat ) {
		boolean debugMode = false;
		
		List<Eigenspace> answer = new ArrayList<Eigenspace>();
		
		List<Matrix<Complex>> spectra = decompose( mat );
		Matrix<Complex> evals = spectra.get(0);	// diagonal matrix of eigenvalues
		Matrix<Complex> evecs = spectra.get(1);	// matrix whose columns are the (normalized?) eigenvectors
		
		int dim = mat.getRows();
		
		int j = 0;
		while ( j < dim ) {
			// current eigenvalue
			double eigval = evals.v(j, j).getReal();	// eigenvalue must be real
			if ( debugMode ) System.err.println("Eval = " + eigval);
			
			// mark the initial index
			int i = j;
			if ( debugMode ) System.err.println("i = " + i);
			
			// handle repeated eigenvalues if any
			while ( j+1 < dim ) {
				double nextEigVal = evals.v(j+1,  j+1).getReal();
				if ( Math.abs(nextEigVal - eigval) < 0.0001 ) {
					// advance index only if next eigenvalue is close enough (epsilon should NOT be hardwired!)
					j++;
				}
				else break;
			}
			if ( debugMode ) System.err.println("j = " + j);
			
			// extract the relevant eigenvectors (or columns)
			Matrix<Complex> submat = evecs.getSlice(0, mat.getRows()-1, i, j);
			
			// add eigenspace to the answer list
			Eigenspace eigspace = new Eigenspace( eigval, submat );
			answer.add( eigspace );
			
			if ( debugMode ) {
				Matrix<Complex> gram = eigspace.gramMatrix(submat);
				System.err.println("SUBMAT = \n" + submat);
				System.err.println("GRAM = \n" + gram);
			}
			
			j++;
		}
		
		return answer;
	}
	
	/**
	 * checkEigh
	 *  sanity check for eigh (which builds the eigenspace decomposition of a hermitian matrix)
	 * @param mat: a hermitian matrix
	 * @param eigspaces: a list of (eigenvalue, eigenprojector) pairs that represents mat
	 * @return
	 */
	public boolean checkEigh( Matrix<Complex> mat, List<Eigenspace> eigspaces, double epsilon ) {
		boolean debugMode = false;
	
		Matrix<Complex> clone = new Matrix<Complex>(Complex.ZERO(), mat.getRows(), mat.getColumns());
		Matrix<Complex> unity = new Matrix<Complex>(Complex.ZERO(), mat.getRows(), mat.getColumns());
		for (int i = 0; i < eigspaces.size(); i++) {
			Eigenspace eigspace = eigspaces.get(i);
			clone = clone.add( eigspace.getEigenprojector().mult( Complex.real(eigspace.getEigenvalue())));
			unity = unity.add( eigspace.getEigenprojector() );
		}

		if ( debugMode ) {
			System.err.println("Matrix =\n" + mat);
			System.err.println("Clone =\n" + clone);
			System.err.println("Identity ?=\n" + unity);
		}
		
		return withinTolerance(mat, clone, epsilon);
	}

	
	/** decompose
	 *   computes the eigenvalue decomposition of a hermitian matrix 
	 *   uses a JAMA implementation for eigendecomposition of real-symmetric matrices
	 *   
	 * @param mat
	 * @return a list of two matrices d and v where d is diagonal and v is unitary
	 *   so that mat * v = v * d and the diagonal entries of d are sorted in ascending order.
	 */
	public static List<Matrix<Complex>> decompose( Matrix<Complex> mat ) {
		boolean debugMode = true;
		boolean paranoidMode = false;
		
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
		 * compute spectral decomposition of M
		 */
		Jama.EigenvalueDecomposition Eig = M.eig();
		Jama.Matrix D = Eig.getD();
		Jama.Matrix V = Eig.getV();
		
		if ( paranoidMode ) {
			// Sanity check: JAMA-style
			System.out.println("Jama.D = ");
			D.print(10, 3);
			System.out.println("Jama V = ");
			V.print(10,  3);
			System.out.println("Jama M*V = ");
			M.times(V).print(10, 3);
			System.out.println("Jama V*D = ");
			V.times(D).print(10,  3);
		}
		
		if ( debugMode ) {
			try {
				check(M.times(V),V.times(D));
				try_success("EigenvalueDecomposition (symmetric)...","");
			} catch ( java.lang.RuntimeException e ) {
				try_failure("EigenvalueDecomposition (symmetric)...","incorrect symmetric Eigenvalue decomposition calculation");
			}
		}
	    
		/*
		 * mapping back to complex Hermitian
		 */	
		List<Matrix<Complex>> theAnswer = shrink( D, V );
		Matrix<Complex> DD = theAnswer.get(0);
		Matrix<Complex> VV = theAnswer.get(1);

		if ( debugMode ) {
			// SANITY CHECK: mat * VV = VV * DD
			Matrix<Complex> MV = mat.mult(VV);
			Matrix<Complex> VD = VV.mult(DD);

			if ( paranoidMode ) {
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
		}
				
		return theAnswer;
	}
	
	/**
	 * shrink
 	 *                                  [ A  -B ]
	 *  takes the decomposition for M = |       | and output decomposition for hermitian H = A + iB
	 *                                  [ B   A ]
	 *  Note: (a) if [x y] is an eigenvector of M then x+iy is an eigenvector for H.
	 *        (b) if [x y] is an eigenvector of M for eigenvalue lambda, then so is [-y x].
	 *            condition (b) defines an equivalence relation on the eigenvectors.    
	 * @param D: diagonal matrix of eigenvalues of M
	 * @param V: matrix whose columns are eigenvectors of M
	 * @return: list containing DD and VV where DD is a diagonal matrix of eigenvalues of H and
	 *   VV is a matrix whose columns are the eigenvectors of H
	 */
	public static List<Matrix<Complex>> shrink( Jama.Matrix D, Jama.Matrix V ) {
		boolean debugMode = false;
		
		int bigrows = D.getRowDimension();
		int bigcols = D.getColumnDimension();
		int rows = bigrows/2;
		int cols = bigcols/2;
		
		// build the diagonal matrix of eigenvalues (take one from each repeated pair)
		Matrix<Complex> DD = new Matrix<>(Complex.ZERO(), rows, cols);
		for (int i=0; i<rows; i++) {
			Complex val = new Complex(D.get(2*i, 2*i), 0.0);
			DD.r( val, i, i );
		}
		
		Matrix<Complex> VV = new Matrix<>(Complex.ZERO(), rows, cols);
		
		// hunt for nonduplicate eigenvectors in every eigenspace
		int j = 0;
		while ( j < cols ) {
			// process next eigenspace
			double eigval = D.get(2*j, 2*j);	// get the eigenvalue
			if ( debugMode ) System.err.println("Eval = " + eigval);
			
			// mark start index and search for repeats if any
			int i = j;
			if ( debugMode) System.err.println("i = " + i);
			while ( j+1 < cols ) {
				double nextEigVal = D.get(2*(j+1),  2*(j+1));
				if ( Math.abs(nextEigVal - eigval) < 0.0001 ) {	// BAD CODE: epsilon shouldn't be hardwired
					// advance index only if next eigenvalue is close enough (identical eigenvvalue)
					j++;
				}
				else break;
			}
			if ( debugMode ) System.err.println("j = " + j);
			
			// mark dimension of this eigenspace
			int eigDim = j-i+1;
			
			// prepare to extract the relevant eigenvectors (or columns) in the current eigenspace 
			Jama.Matrix eigVectors = V.getMatrix(0, bigrows-1, 2*i, 2*j+1);
			
			// looking for a set linearly independent eigenvectors of size dim
			//  modulo the equivalence [x y] = [-y x]
			Set<Jama.Matrix> eigBasis = new HashSet<Jama.Matrix>();
			
			// add first eigenvector
			Jama.Matrix firstVec = eigVectors.getMatrix(0, bigrows-1, 0, 0);
			eigBasis.add( firstVec );
			if ( debugMode ) { 
				System.err.println("Added first eigenvector: " + String.valueOf(i) + "\n");
				firstVec.print(10, 3);
			}
			
			// find the other eigenvectors
			int currIndex = 1;
			while ( currIndex < 2*eigDim ) {
				if ( debugMode ) System.err.println("try currIndex = " + String.valueOf(currIndex));
				
				// get the next candidate eigenvector
				Jama.Matrix nextVec = eigVectors.getMatrix(0, bigrows-1, currIndex, currIndex);
				
				// construct its equivalent twin
				Jama.Matrix twinVec = new Jama.Matrix(bigrows, 1, 0.0);
				twinVec.setMatrix(0, rows-1, 0, 0, nextVec.getMatrix(rows, bigrows-1, 0, 0).timesEquals(-1.0));
				twinVec.setMatrix(rows, bigrows-1, 0, 0, nextVec.getMatrix(0, rows-1, 0, 0));
				
				// if twin is not in the eigenbasis, add the eigenvector, otherwise ignore
				// NOTE: this check fails if twin of [x y] is not exactly [-y x] (but perhaps approximately)
				Iterator<Jama.Matrix> looper = eigBasis.iterator();
				boolean isDuplicate = false;
				while ( looper.hasNext() ) {
					Jama.Matrix currVec = looper.next();
					Jama.Matrix dotProd = currVec.transpose().times(twinVec);
					if ( dotProd.norm1() > 0.1 ) {
						isDuplicate = true;
						break;
					}
				}
				
				if ( !isDuplicate ) {
					eigBasis.add( nextVec );
					if ( debugMode ) {
						System.err.println("Added new eigenvector: " + String.valueOf(i+currIndex) + "\n");
						nextVec.print(10, 3);
					}
				}
				else {
					if ( debugMode ) {
						System.err.println("These eigenvectors are twin duplicates:\n");
						nextVec.print(10,  3);
						twinVec.print(10,  3);
					}
				}
				
				currIndex++;
			}
			
			// did we find enough eigenvectors to form a basis?
			if ( eigBasis.size() < eigDim ) {
				throw new RuntimeException("shrink: did not find full basis");
			}
			
			// place the set of eigenvectors into answer matrix: columns i through j
			Iterator<Jama.Matrix> iter = eigBasis.iterator();
			int columnIndex = i;
			while ( iter.hasNext() && columnIndex <= j ) {
				Jama.Matrix nextVec = iter.next();				
				for (int k=0; k<rows; k++) {
					Complex val = new Complex(nextVec.get(k,0), nextVec.get(rows+k,0));	// build x+iy
					VV.r( val, k, columnIndex );
				}
				columnIndex++;
			}
			
			// move to next distinct eigenvalue
			j++;
		}
		
		List<Matrix<Complex>> answer = new ArrayList<Matrix<Complex>>();
		answer.add(DD);
		answer.add(VV);

		return answer;
	}
	
	

	// TO-DO: synchronize this with the JAMA checker
    public static boolean withinTolerance(Matrix<Complex> a, Matrix<Complex> b, double epsilon) {
    	boolean debugMode = false;
    	
    	Matrix<Complex> c = a.sub(b);
    	double diff = 0.0;
    	for(int x = 0; x < c.getColumns(); ++x) {
			for(int y = 0; y < c.getRows(); ++y) {
				//Complex cdiff = a.o.op(a.v(y,x)).sub(b.v(y, x));
				diff += c.v(y,x).abs();
			}
		}	
    	
    	if ( debugMode ) System.err.println("Difference = \n" + c.toString());
    	
		return diff < epsilon;
	}
    
    
    /*****************************************************************
     * The code below is ported from JAMA                            *
     *  temporarily used for testing                                 *
     *****************************************************************
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
