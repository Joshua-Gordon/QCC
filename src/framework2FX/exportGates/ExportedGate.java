package framework2FX.exportGates;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import framework2FX.UserDefinitions.MatrixDefinition;
import framework2FX.gateModels.DefaultModel;
import framework2FX.gateModels.DefaultModel.DefaultModelType;
import framework2FX.gateModels.GateModel;
import framework2FX.gateModels.GateModelFactory.PresetGateModel;
import framework2FX.gateModels.PresetGateType;
import mathLib.Complex;
import mathLib.Matrix;

public class ExportedGate {	
	private final GateModel gateModel;
	private final Hashtable<String, Complex> argParamMap;
	private final int[] gateRegisters;
	private final Control[] controls;
	private final Matrix<Complex>[] matrixes;
	
	
	@SuppressWarnings("unchecked")
	public static ExportedGate mkIdentAt(int register) {
		DefaultModel gm = PresetGateType.IDENTITY.getModel();
		Matrix<Complex> m = ((MatrixDefinition) gm.getDefinitions().get(0)).getMatrix();
		
		return new ExportedGate(gm, new Hashtable<>(), new int[] {register}, new Control[0], new Matrix[] { m });
	}
	
	
	public ExportedGate(GateModel gateModel, Hashtable<String, Complex> argParamMap, int[] gateRegisters, Control[] controls, Matrix<Complex>[] matrixes) {
		this.gateModel = gateModel;
		this.gateRegisters = gateRegisters;
		this.controls = controls;
		this.matrixes = matrixes;
		this.argParamMap = argParamMap;
	}
	
	public GateModel getGateModel() {
		return gateModel;
	}
	
	public Complex getParameter (String parameter) {
		return argParamMap.get(parameter);
	}
	
	public Set<String> getArgumentSet() {
		return argParamMap.keySet();
	}
	
	public Collection<Complex> getParameters() {
		return argParamMap.values();
	}
	
	public int[] getGateRegister() {
		return gateRegisters;
	}
	
	public boolean isPresetGate() {
		return gateModel.isPreset();
	}
	
	public PresetGateType getPresetGateType() {
		if(gateModel.isPreset()) 
			return  ((PresetGateModel) gateModel).getPresetGateType();
		else return null;
	}
	
	public DefaultModelType getGateType() {
		if(gateModel instanceof DefaultModel) {
			return ((DefaultModel)gateModel).getGateModelType();
		}
		return null;
	}
	
	
	public Control[] getControls(){
		return controls;
	}
	
	public Matrix<Complex>[] getInputMatrixes() {
		return matrixes;
	}
	
}