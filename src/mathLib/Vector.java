package mathLib;

import java.util.ArrayList;

public class Vector<T> extends Matrix<T>{

	private static final long serialVersionUID = 406190986104372479L;

	@SafeVarargs
	public Vector(T ... components){
		super(components.length, 1, components);
	}
	
	@SafeVarargs
	public Vector(Operators<T> operation, boolean isVertical, T ... components){		
		super(operation, isVertical? components.length:1, 
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
		Operators<T> o1 = this.o.dup();
		Operators<T> o2 = this.o.dup();
		
		T sum = o1.get0();
		for(int i = 0; i < cutoff; i++)
			sum = o1.op(sum).add(o2.op(v(i)).mult(v(i)));
		return o1.op(sum).sqrt();
	}
	
	public T dot(Vector<T> vec){
		Operators<T> o1 = this.o.dup();
		Operators<T> o2 = this.o.dup();
		
		T sum = o1.get0();
		for(int i = 0; i < length(); i++)
			sum = o1.op(sum).add(o2.op(v(i)).mult(vec.v(i)));
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
