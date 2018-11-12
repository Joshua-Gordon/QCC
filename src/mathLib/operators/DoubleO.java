package mathLib.operators;

//                        7
//                        ^  
public final class DoubleO implements OperatorSet<Double>{
	public static final DoubleO OPERATOR_SET = new DoubleO();
	
	private DoubleO() {}
	
	@Override
	public Double get1() {
		return 1d;
	}

	@Override
	public Double getn1() {
		return -1d;
	}

	@Override
	public Double get0() {
		return 0d;
	}

	@Override
	public Double[] mkZeroArray(int size) {
		Double[] comps = new Double[size];
		for(int i = 0; i < size; i++)
			comps[i] = 0d;
		return comps;
	}

	@Override
	public Double add(Double num1, Double num2) {
		return num1 + num2;
	}

	@Override
	public Double sub(Double num1, Double num2) {
		return num1 - num2;
	}

	@Override
	public Double mult(Double num1, Double num2) {
		return num1 * num2;
	}

	@Override
	public Double div(Double num1, Double num2) {
		return num1 / num2;
	}

	@Override
	public Double exp(Double num1, Double num2) {
		return Math.pow(num1 , num2 );
	}

	@Override
	public Double sqrt(Double num) {
		return Math.pow(num , .5);
	}

}
