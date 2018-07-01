package framework;

import mathLib.Complex;
import mathLib.Matrix;


/**
 * This class provides different ways to get / set Matrixes for different types of Gates
 * 
 * @author quantumresearch
 *
 */
public abstract class AbstractGate {
	
	public static enum GateType{
        I,X,Y,Z,H,CUSTOM, MEASURE, CNOT, SWAP
    }
    public static enum LangType{
        QUIL,QASM,QUIPPER
    }
	
	
	private Matrix<Complex> matrix;
	
	public Matrix<Complex> getMatrix(){
		return matrix;
	}
	
	public void setMatrix(Matrix<Complex> matrix) {
		this.matrix = matrix;
	}
	
	
}
