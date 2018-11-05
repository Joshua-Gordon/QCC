package mathLib.operators;

import mathLib.Complex;

public final class ComplexO implements OperatorSet<Complex> {
	public static final ComplexO OPERATOR_SET = new ComplexO();
	
	private ComplexO() {}
	
	
	@Override
	public Complex add(Complex num1, Complex num2) {
		return num1.add(num2);
	}

	@Override
	public Complex sub(Complex num1, Complex num2) {
		return num1.sub(num2);
	}

	@Override
	public Complex mult(Complex num1, Complex num2) {
		return num1.mult(num2);
	}

	@Override
	public Complex div(Complex num1, Complex num2) {
		return num1.div(num2);
	}

	@Override
	public Complex exp(Complex num1, Complex num2) {
		return num1.exp(num2);
	}

	@Override
	public Complex get1() {
		return Complex.ONE();
	}

	@Override
	public Complex getn1() {
		return new Complex(-1, 0);
	}

	@Override
	public Complex get0() {
		return Complex.ZERO();
	}

	@Override
	public Complex[] mkZeroArray(int size) {
		Complex[] temp = new Complex[size];
		for(int i = 0; i < size; i++)
			temp[i] = get0();
		return temp;
	}

	@Override
	public Complex sqrt(Complex num) {
		return num.sqrt();
	}

}
