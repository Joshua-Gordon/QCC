package mathLib;


public interface Scalar<T>{
	
	T add(T num);
	T sub(T num);
	T mult(T num);
	T div(T num);
	T pow(T num);
	T sqrt();
	
	T get1();
	T getn1();
	T get0();
	
	T[] mkArray(int size);
}
