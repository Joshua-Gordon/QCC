package framework;

import mathLib.Complex;
import mathLib.Matrix;

public abstract class AbstractGate {
	private Matrix<Complex> matrix;
	
	public Matrix<Complex> getMatrix(){
		return matrix;
	}
	
	public void setMatrix(Matrix<Complex> matrix) {
		this.matrix = matrix;
	}
	
	
}
