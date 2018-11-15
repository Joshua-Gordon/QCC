package framework2FX;

import framework2FX.gateModels.GateModel;
import mathLib.Complex;
import mathLib.Matrix;

public class ExportedGate {	
	private final GateModel gateModel;
	private final Complex[] parameters;
	private final int[] gateRegisters;
	private final Control[] controls;
	private final Matrix<Complex>[] matrixes;
	
	
	public ExportedGate(GateModel gateModel, Complex[] parameters, int[] gateRegisters, Control[] controls, Matrix<Complex>[] matrixes) {
		this.gateModel = gateModel;
		this.gateRegisters = gateRegisters;
		this.controls = controls;
		this.matrixes = matrixes;
		this.parameters = parameters;
	}
	
	public GateModel getGateModel() {
		return gateModel;
	}
	
	public Complex[] getParameters () {
		return parameters;
	}
	
	public int[] getGateRegister() {
		return gateRegisters;
	}
	
	public Control[] getControls(){
		return controls;
	}
	
	public Matrix<Complex>[] getMatrixes() {
		return matrixes;
	}
	
}
