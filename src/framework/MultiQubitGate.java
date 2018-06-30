package framework;

import java.util.ArrayList;

import mathLib.Complex;


public class MultiQubitGate extends Gate {
	private static final long serialVersionUID = 8692251843065026400L;
	
	
	public ArrayList<Integer> registers; //render the gate on the first index

    public MultiQubitGate(Complex[][] mat, GateType gt, ArrayList<Integer> registers) {
        super(mat, gt);
        this.registers = registers;
    }


}
