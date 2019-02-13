package mathLib.operators;

public final class IntegerO implements OperatorSet<Integer>{
	private static final long serialVersionUID = -9024031678660839963L;
	
	public static final OperatorSet<Integer> OPERATOR_SET = new IntegerO();

	private IntegerO() {}
	
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
	public Integer add(Integer num1, Integer num2) {
		return num1 + num2;
	}

	@Override
	public Integer sub(Integer num1, Integer num2) {
		return num1 - num2;
	}

	@Override
	public Integer mult(Integer num1, Integer num2) {
		return num1 * num2;
	}

	@Override
	public Integer div(Integer num1, Integer num2) {
		return num1 / num2;
	}

	@Override
	public Integer exp(Integer num1, Integer num2) {
		return (int) Math.round(Math.pow(num1 , num2));
	}

	@Override
	public Integer sqrt(Integer num) {
		return (int) Math.round(Math.pow(num , .5));
	}
	
}
