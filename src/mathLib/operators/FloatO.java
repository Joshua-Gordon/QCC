package mathLib.operators;

public final class FloatO implements OperatorSet<Float>{
	public static final OperatorSet<Float> OPERATOR_SET = new FloatO();
	
	private FloatO() {}

	@Override
	public Float get1() {
		return 1f;
	}

	@Override
	public Float getn1() {
		return -1f;
	}

	@Override
	public Float get0() {
		return 0f;
	}

	@Override
	public Float[] mkZeroArray(int size) {
		Float[] values = new Float[size];
		for(int i = 0; i < size; i++)
			values[i] = 0f;
		return values;
	}

	@Override
	public Float add(Float num1, Float num2) {
		return num1 + num2;
	}

	@Override
	public Float sub(Float num1, Float num2) {
		return num1 - num2;
	}

	@Override
	public Float mult(Float num1, Float num2) {
		return num1 * num2;
	}

	@Override
	public Float div(Float num1, Float num2) {
		return num1 / num2;
	}

	@Override
	public Float exp(Float num1, Float num2) {
		return (float) Math.pow(num1 , num2);
	}

	@Override
	public Float sqrt(Float num) {
		return (float) Math.pow(num , .5d);
	}
	
}
