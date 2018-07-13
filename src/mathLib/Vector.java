package mathLib;


import java.util.ArrayList;

public class Vector<T extends Scalar<T>> extends Matrix<T>{
	private static final long serialVersionUID = 406190986104372479L;

	@SafeVarargs
	public Vector(T ... components){
		super(components.length, 1, components);
	}
	
	public Vector(T[] components, boolean isVertical){		
		super(isVertical? components.length:1, 
				isVertical? 1:components.length, components);
	}
	
	public int length(){
		return getComponents().length;
	}
	
	public Vector<T> norm(){
		return div(mag()).toVector();
	}
	
	public T mag(){
		return mag(length());
	}
	
	public T mag(int cutoff){
		T sum = m.get0();
		for(int i = 0; i < cutoff; i++)
			sum = sum.add(v(i).mult(v(i)));
		return sum.sqrt();
	}
	
	public T dot(Vector<T> vec){
		T sum = m.get0();
		for(int i = 0; i < length(); i++)
			sum = sum.add(v(i).mult(vec.v(i)));
		return sum;
	}
	
	public void absorb(Vector<T> vec){
		for(int i = 0; i < length(); i++){
			r(vec.v(i), i);
		}
	}
	
	public T v(int index){
		return getComponents()[index];
	}
	
	public void r(T value, int index){
		getComponents()[index] = value;
	}

	public ArrayList<T> toArrayList() {
		ArrayList<T> comps = new ArrayList<>();
		for(int i = 0; i < getRows(); ++i) {
			comps.add(v(i));
		}
		return comps;
	}
}
