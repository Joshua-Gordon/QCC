package testLib;

import mathLib.Complex;
import mathLib.Matrix;

public class BaseMatrix {
	
	// Pauli X matrix
	public static Matrix<Complex> pauliX() {
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), 2, 2,
				Complex.ZERO(), Complex.ONE(),
				Complex.ONE(), Complex.ZERO());
		return mat;
	}
	
	// Pauli Y matrix
	public static Matrix<Complex> pauliY() {
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), 2, 2,
				Complex.ZERO(), Complex.I().negative(),
				Complex.I(), Complex.ZERO());
		return mat;
	}
	
	// Pauli Z matrix
	public static Matrix<Complex> pauliZ() {
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), 2, 2,
				Complex.ONE(), Complex.ZERO(),
				Complex.ZERO(), Complex.ONE().negative());
		return mat;
	}
	
	// Hadamard matrix
	public static Matrix<Complex> hadamard() {
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), 2, 2,
				Complex.ISQRT2(), Complex.ISQRT2(),
				Complex.ISQRT2(), Complex.ISQRT2().negative());
		return mat;
	}
	
	// Hadamard matrix of arbitrary dimension (Sylvester type)
	public static Matrix<Complex> Hadamard( int dim ) {
		if ( dim <= 0 ) return null;
		if ( dim == 1 ) {
			return hadamard();
		}
		else {
			return hadamard().kronecker(Hadamard( dim-1 ));
		}
	}
	
	// this might be redundant
	public static Matrix<Complex> identityMatrix( int size ) {
		Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), size, size);
		for (int i = 0; i < size; ++i) {
			mat.r(Complex.ONE(), i, i);
		}
		return mat;
	}

}
