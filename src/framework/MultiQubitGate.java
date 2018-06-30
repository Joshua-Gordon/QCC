package framework;

import java.util.ArrayList;

import mathLib.Complex;
import mathLib.Matrix;


public class MultiQubitGate extends DefaultGate {
	private static final long serialVersionUID = 8692251843065026400L;
	
	
	public ArrayList<Integer> registers; //render the gate on the first index
	public ArrayList<Matrix<Complex>> matrixes;
	
    public MultiQubitGate(Matrix<Complex> mat, GateType gt, ArrayList<Integer> registers) {
        super(mat, gt);
        this.registers = registers;
    }


}
