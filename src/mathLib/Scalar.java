package mathLib;



public abstract class Scalar<T>{
	
	protected T value;
	
	public abstract T add(T num);
	public abstract T sub(T num);
	public abstract T mult(T num);
	public abstract T div(T num);
	public abstract T exp(T num);
	public abstract T sqrt();
	
	public abstract T get1();
	public abstract T getn1();
	public abstract T get0();
	
	public abstract T[] mkZeroArray(int size);
	
	/**
	 * Creates a duplicate of this {@link Scalar}
	 * @param value
	 * @return
	 */
	public abstract Scalar<T> dup(T value);
	
}
