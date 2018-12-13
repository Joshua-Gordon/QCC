package framework2FX.solderedGates;

import java.io.Serializable;

import framework2FX.UserDefinitions;
import framework2FX.UserDefinitions.ArgDefinition;
import framework2FX.UserDefinitions.CheckDefinitionRunnable;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MatrixDefinition;
import framework2FX.UserDefinitions.ScalarDefinition;
import framework2FX.gateModels.GateModel;
import framework2FX.gateModels.PresetGateType;
import framework2FX.gateModels.GateModelFactory.PresetGateModel;
import utils.customCollections.ImmutableArray;



public class SolderedGate implements Serializable, CheckDefinitionRunnable {
	private static final long serialVersionUID = 2595030500395644473L;
	
	private final Solderable gateModel;
	private final GroupDefinition parameterSet;
	
	public SolderedGate(Solderable gateModel, String ... parameters) {
		this.gateModel = gateModel;
		
		ImmutableArray<String> modelArguments = gateModel.getArguments();
		if(parameters.length != modelArguments.size())
			throw new RuntimeException("Not all arguments are defined with the user input");
		
		parameterSet = UserDefinitions.evaluateInput(this, parameters);
	}
	
	public static SolderedGate mkIdent() {
		return new SolderedGate(PresetGateType.IDENTITY.getModel());
	}
	
	public Solderable getGateModel() {
		return gateModel;
	}
	
	public GroupDefinition getParameterSet() {
		return parameterSet;
	}
	
	public boolean isIdentity() {
		if (gateModel instanceof GateModel) {
			GateModel gm = (GateModel) gateModel;
			if(gm.isPreset()) {
				PresetGateModel pgm = (PresetGateModel) gm;
				return pgm.getPresetGateType() == PresetGateType.IDENTITY;
			}
		}
		return false;
	}
	
	@Override
	public void checkScalarDefinition(ScalarDefinition definition) {}

	@Override
	public void checkMatrixDefinition(MatrixDefinition definition) {
		throw new RuntimeException("Definition should not define a matrix");
	}

	@Override
	public void checkArgDefinition(ArgDefinition definition) {
		if(definition.isMatrix())
			throw new RuntimeException("Definition should not define a matrix");
	}
	
}
