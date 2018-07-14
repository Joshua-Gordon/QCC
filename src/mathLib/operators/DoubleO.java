package mathLib.operators;

//                  7
//                  ^  
public class DoubleO extends Operators<Double>{
	
	
	
	@Override
	public Double add(Double num) {
		return value + num;
	}

	@Override
	public Double sub(Double num) {
		return value - num;
	}

	@Override
	public Double mult(Double num) {
		return value * num;
	}

	@Override
	public Double div(Double num) {
		return value / num;
	}

	@Override
	public Double exp(Double num) {
		return Math.pow(value, num);
	}

	@Override
	public Double sqrt() {
		return Math.sqrt(value);
	}

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
	public Operators<Double> dup() {
		return new DoubleO();
	}

}
