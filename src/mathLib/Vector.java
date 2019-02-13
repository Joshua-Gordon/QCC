package mathLib;

import java.util.ArrayList;

import mathLib.operators.OperatorSet;

public class Vector<T> extends Matrix<T>{

	private static final long serialVersionUID = 406190986104372479L;

	@SafeVarargs
	public Vector(T ... components){
		super(components.length, 1, components);
	}

	public Vector(Vector<T> v) {
		super(v.length(),1,v.getComponents());
	}

	@SafeVarargs
	public Vector(T elementToInferOperator, boolean isVertical, T ... components){		
		super(elementToInferOperator, isVertical? components.length:1, 
				isVertical? 1:components.length, components);
	}
	
	@SafeVarargs
	public Vector(OperatorSet<T> operatorSet, boolean isVertical, T ... components){		
		super(operatorSet, isVertical? components.length:1, 
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
		
		T sum = o.get0();
		for(int i = 0; i < cutoff; i++)
			sum = o.add(sum ,  o.mult(v(i) , v(i)));
		return o.sqrt(sum);
	}
	
	public T dot(Vector<T> vec){
		T sum = o.get0();
		for(int i = 0; i < length(); i++)
			sum = o.add(sum ,  o.mult(v(i) , vec.v(i)));
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

	public Matrix<T> outerProduct(Vector<T> other) {
		Matrix<T> out = new Matrix<T>(this.o,this.length(),this.length());
		for(int y = 0; y < this.length(); ++y) {
			for(int x = 0; x < this.length(); ++x) {
				out.r(o.mult(this.v(y),other.v(x)),x,y);
			}
		}
		return out;
	}
}
