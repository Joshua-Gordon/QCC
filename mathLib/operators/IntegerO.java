package mathLib.operators;

public class IntegerO extends Operators<Integer>{
	
	@Override
	public Integer add(Integer num) {
		return value + num;
	}

	@Override
	public Integer sub(Integer num) {
		return value - num;
	}

	@Override
	public Integer mult(Integer num) {
		return value * num;
	}

	@Override
	public Integer div(Integer num) {
		return value / num;
	}

	@Override
	public Integer exp(Integer num) {
		return (int) Math.pow(value, num);
	}

	@Override
	public Integer sqrt() {
		return (int) Math.sqrt(value);
	}

	@Override
	public Integer get1() {
		return 1;
	}

	@Override
	public Integer getn1() {
		return -1;
	}

	@Override
	public Integer get0() {
		return 0;
	}

	@Override
	public Integer[] mkZeroArray(int size) {
		Integer[] values = new Integer[size];
		for(int i = 0; i < size; i++)
			values[i] = 0;
		return values;
	}

	@Override
	public Operators<Integer> dup() {
		return new IntegerO();
	}
	
}
