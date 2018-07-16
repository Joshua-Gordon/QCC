package mathLib.operators;

public class FloatO extends Operators<Float>{
	@Override
	public Float add(Float num) {
		return value + num;
	}

	@Override
	public Float sub(Float num) {
		return value - num;
	}

	@Override
	public Float mult(Float num) {
		return value * num;
	}

	@Override
	public Float div(Float num) {
		return value / num;
	}

	@Override
	public Float exp(Float num) {
		return (float) Math.pow(value, num);
	}

	@Override
	public Float sqrt() {
		return (float) Math.sqrt(value);
	}

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
	public Operators<Float> dup() {
		return new FloatO();
	}
	
}
