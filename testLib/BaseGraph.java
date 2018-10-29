package testLib;

import mathLib.Complex;
import mathLib.Matrix;
import testLib.BaseMatrix;


public class BaseGraph {
	
	public static Matrix<Complex> completeGraph( int size ) {
		if ( size <= 1 ) return null;
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), size, size );
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < i; ++j) {
				mat.r(Complex.ONE(), i, j);
				mat.r(Complex.ONE(), j, i);
			}
		}
		return mat;
	}

	public static Matrix<Complex> emptyGraph( int size ) {
		if ( size <= 1 ) return null;
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), size, size );
		return mat;
	}

	public static Matrix<Complex> pathGraph( int size ) {
		if ( size <= 1 ) return null;
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), size, size );
		for (int i = 0; i < size-1; ++i) {
			mat.r(Complex.ONE(), i, i+1);
			mat.r(Complex.ONE(), i+1, i);
		}
		return mat;
	}
	
	public static Matrix<Complex> cubeGraph( int dim ) {
		if ( dim <= 0 ) return null;
		if ( dim == 1 ) {
			return completeGraph(2);
		}
		else {
			Matrix<Complex> smallerCube = cubeGraph( dim-1 );
			return BaseMatrix.identityMatrix(2).kronecker(smallerCube).add(completeGraph(2).kronecker(BaseMatrix.identityMatrix(1 << (dim-1))));
		}
	}
}
