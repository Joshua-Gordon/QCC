package mathLib;


import Jama.*;
import java.util.*;


public class HermitianDecomposition {
	public static List<Matrix<Complex>> decompose( Matrix<Complex> mat ) {

		//Matrix<double> A = Matrix.map(mat, x -> x.getReal());
		//Matrix<double> A = new Matrix<>((double)0.0, mat.getRows(), mat.getColumns());
		
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
		// CHECK: MV = VD
		
		/*
		 * prepare the output
		 * TODO: 
		 *  - the eigenvalues of M come in repeated pairs: take only half of these
		 *  - transform the eigenvectors as well: cutting the lengths into half
		 * Currently: return the matrices in the non-JAMA format.
		 */
		Matrix<Complex> DD = new Matrix<>(Complex.ZERO(), bigrows, bigcols);
		for (int i=0; i<bigrows; i++) {
			for (int j=0; j<bigcols; j++) {
				Complex val = new Complex(D.get(i, j), 0.0);
				DD.r( val, i, j);
			}
		}

		Matrix<Complex> VV = new Matrix<>(Complex.ZERO(), bigrows, bigcols);
		for (int i=0; i<bigrows; i++) {
			for (int j=0; j<bigcols; j++) {
				Complex val = new Complex(V.get(i, j), 0.0);
				VV.r( val, i, j);
			}
		}
		
		List<Matrix<Complex>> boo = new ArrayList<Matrix<Complex>>();
		boo.add(DD);
		boo.add(VV);
				
		return boo;
	}

}
