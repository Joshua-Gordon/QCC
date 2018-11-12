package mathLib;

import java.io.Serializable;

import javax.swing.tree.VariableHeightLayoutCache;

public abstract class MathValue implements Serializable {
	
	private static final long serialVersionUID = -7885273546489149083L;
	private static double EPSILON = .0000000000000001d;
	
	@SuppressWarnings("unchecked")
	public static MathValue add(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex)
			return ((Complex) value1).add((Complex) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value1).add((Matrix<Complex>) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Complex)
			return ((Matrix<Complex>) value1).add((Complex) value2);
		else if (value1 instanceof Complex && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value2).add((Complex) value1);
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}
	
	@SuppressWarnings("unchecked")
	public static MathValue sub(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex)
			return ((Complex) value1).sub((Complex) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value1).sub((Matrix<Complex>) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Complex)
			return ((Matrix<Complex>) value1).sub((Complex) value2);
		else if (value1 instanceof Complex && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value2).mult(Complex.NEG_ONE()).add((Complex) value1);
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}
	
	@SuppressWarnings("unchecked")
	public static MathValue mult(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex)
			return ((Complex) value1).mult((Complex) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value1).mult((Matrix<Complex>) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Complex)
			return ((Matrix<Complex>) value1).mult((Complex) value2);
		else if (value1 instanceof Complex && value2 instanceof Matrix<?>)
			return ((Matrix<Complex>) value2).mult((Complex) value1);
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}
	
	@SuppressWarnings("unchecked")
	public static MathValue div(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex)
			return ((Complex) value1).div((Complex) value2);
		else if (value1 instanceof Matrix<?> && value2 instanceof Complex)
			return ((Matrix<Complex>) value1).div((Complex) value2);
		else if (value2 instanceof Matrix<?>)
			throw new IllegalArgumentException("Cannot divide by a matrix");
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}
	
	@SuppressWarnings("unchecked")
	public static MathValue neg(MathValue value1) {
		if (value1 instanceof Complex)
			return ((Complex) value1).mult(Complex.NEG_ONE());
		else if (value1 instanceof Matrix<?>)
			return ((Matrix<Complex>) value1).mult(Complex.NEG_ONE());
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}
	
	
	// TODO: define matrix powers
	public static MathValue pow(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex)
			return ((Complex) value1).exp((Complex) value2);
		else if (value1 instanceof Matrix<?> || value2 instanceof Matrix<?>)
			throw new IllegalArgumentException("Arguments must be complex, undefined behavior for matrixes otherwise");
		else throw new IllegalArgumentException("Arguments must either be a complex matrix or a complex number");
	}

	public static MathValue xOR(MathValue value1, MathValue value2) {
		if (value1 instanceof Complex && value2 instanceof Complex) {
			Complex c1 = (Complex) value1;
			Complex c2 = (Complex) value2;
			if(isInteger(c1) && isInteger(c2))
				return new Complex(Math.round(c1.getReal()) ^ Math.round(c2.getReal()), 0);
		}
		throw new IllegalArgumentException("Cannot xor these values");
	}
	
	@SuppressWarnings("unchecked")
	public static MathValue tensor(MathValue value1, MathValue value2) {
		if (value1 instanceof Matrix<?> && value2 instanceof Matrix<?>) {
			Matrix<Complex> m1 = (Matrix<Complex>) value1;
			Matrix<Complex> m2 = (Matrix<Complex>) value2;
			return m1.kronecker(m2);
		}
		throw new IllegalArgumentException("Cannot xor these values");
	}
	
	public static long getInteger(MathValue value) {
		if(value instanceof Complex) {
			Complex c = (Complex) value;
			if(isInteger(c))
				return Math.round(c.getReal());
		}
		throw new IllegalArgumentException("This expects integer as a parameter.");
	}
	
	private static boolean isInteger(Complex value) {
		double a = value.getReal();
		double b = value.getImaginary();
		return fuzzyEquals(b, 0) && (a == Math.floor(a)) && !Double.isInfinite(a);
	}
	
	public static boolean fuzzyEquals(double d1, double d2) {
		if(Math.abs(d1 - d2) <= EPSILON) return true;
		return false;
	}
	
}
