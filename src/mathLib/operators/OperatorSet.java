package mathLib.operators;

import java.io.Serializable;

public interface OperatorSet<T> extends Serializable {
	
	public T add(T num1, T num2);
	public T sub(T num1, T num2);
	public T mult(T num1, T num2);
	public T div(T num1, T num2);
	public T exp(T num1, T num2);
	public T sqrt(T num);
	
	public T get1();
	public T getn1();
	public T get0();
	
	public T[] mkZeroArray(int size);
}
