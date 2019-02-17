package appFX.framework.exportGates;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import appFX.framework.InputDefinitions.MatrixDefinition;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.gateModels.BasicModel.BasicModelType;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
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
		BasicModel gm = PresetGateType.IDENTITY.getModel();
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
	
	public BasicModelType getGateType() {
		if(gateModel instanceof BasicModel) {
			return ((BasicModel)gateModel).getGateModelType();
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
